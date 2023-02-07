package com.example.customer_dinein;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class OrderPlacedTimerActivity extends AppCompatActivity {
    TextView textViewOrder;
    Button buttonDownloadBill,buttonGiveFeedback;
    int pageHeight = 1120;
    int pagewidth = 792;

    Bitmap bmp, scaledbmp;
    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "", mobile ="", tableID="", order_Id="", card_No="", paid_Via="", order_placed ="",
            get_total_amount_query="", Amount="", Tax="", total_Amount_Paid="";
    String replaced_cardNo;
    int  listViewCtr_Top = 400;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed_timer);

        Intent intent = new Intent();
        mobile = getIntent().getStringExtra("mobile");
        tableID = getIntent().getStringExtra("tableID");
        order_Id = getIntent().getStringExtra("order_Id");

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        calculate_total_amount(mobile, tableID, order_Id);

        textViewOrder = findViewById(R.id.textViewOrder);
        buttonDownloadBill = findViewById(R.id.buttonDownloadBill);
        buttonGiveFeedback = findViewById(R.id.buttonGiveFeedback);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.dinein_app_logo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 120, 120, false);

        buttonGiveFeedback.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderPlacedTimerActivity.this, FeedbackActivity.class);
                intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                intent.putExtra("tableID", tableID);
                intent.putExtra("order_Id", order_Id);
                startActivity(intent);
                finish();
            }
        });

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        buttonDownloadBill.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                generatePDF();
            }
        });
    }

    public void calculate_total_amount(String mobile, String tableID, String order_Id) {
        get_total_amount_query =  "SELECT pd.order_Id, pd.mobileNo, pd.tableID, pd.card_No, pd.paid_Via,"
                + " SUM(od.price_Amount) AS Amount, CAST(SUM(od.tax_Amount) AS NUMERIC(9,2)) AS Tax, "
                + " CAST(SUM(od.price_Amount + od.tax_Amount) AS NUMERIC(9,2)) AS total_Amount_Paid, "
                + " CAST(pd.date_updated AS VARCHAR(12)) + ' | ' + convert(varchar, cast(pd.date_updated as time), 100)"
                + " FROM [CUSTOMER].[OrderDetails] od INNER JOIN [CUSTOMER].[PaymentDetails] pd ON od.order_Id = pd.order_Id "
                + " WHERE pd.mobileNo = '" + mobile.replace("+91-", "")
                + "' AND pd.tableID = '" + tableID + "' AND pd.order_Id = '" + order_Id + "' AND pd.payment_Status = 'Paid'"
                + " GROUP BY pd.mobileNo , pd.tableID, pd.order_Id, pd.card_No, pd.paid_Via,"
                + " CAST(pd.date_updated AS VARCHAR(12)) + ' | ' + convert(varchar, cast(pd.date_updated as time), 100);";

        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(get_total_amount_query);
                while (resultSet.next()) {
                    order_Id = resultSet.getString(1);
                    mobile = resultSet.getString(2);
                    tableID = resultSet.getString(3);
                    card_No = resultSet.getString(4);
                    paid_Via = resultSet.getString(5);
                    Amount = resultSet.getString(6);
                    Tax = resultSet.getString(7);
                    total_Amount_Paid = resultSet.getString(8);
                    order_placed = resultSet.getString(9);
                }
                replaced_cardNo = card_No.replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1XXXXXXXX$3");
                System.out.println("Card No: " + replaced_cardNo);

                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();
        //canvas.drawBitmap(scaledbmp, 20, 20, paint);
        canvas.drawColor(Color.WHITE);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        title.setColor(ContextCompat.getColor(this, R.color.colorWhite));
        title.setTextSize(32);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("DineIn", 400, 60, title);
        canvas.drawText("Bill Receipt", 396, 100, title);

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(26);
        title.setColor(ContextCompat.getColor(this, R.color.colorWhite));

        canvas.drawText("Order#: ", 200, 130, title);
        canvas.drawText(order_Id,270,130,title);
        canvas.drawText("Table No.: ", 500, 130, title);
        canvas.drawText(tableID,580,130,title);
        canvas.drawText("Date Time: ", 220, 170, title);
        canvas.drawText(order_placed,430,170,title);
        canvas.drawText("Mobile No.: ", 220, 210, title);
        canvas.drawText(mobile,410,210,title);
        canvas.drawText("Card No.: ", 220, 250, title);
        canvas.drawText(replaced_cardNo,415,250,title);

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(30);
        title.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("-------------------------------------------------------", 100, 310, title);
        canvas.drawText("Ordered Items", 100, 340, title);
        canvas.drawText("Qty", 450, 340, title);
        canvas.drawText("Amount", 600, 340, title);
        canvas.drawText("-------------------------------------------------------", 100, 370, title);

        query = "SELECT fi.foodItem_Name, od.price_Amount, od.quantity"
                + " FROM [ADMINISTRATOR].[FoodItems] fi INNER JOIN [CUSTOMER].[OrderDetails] od ON od.foodItem_Id = fi.foodItem_Id "
                + " INNER JOIN [CUSTOMER].[PaymentDetails] pd ON od.order_Id = pd.order_Id"
                + " WHERE od.mobileNo = '" + mobile.replace("+91-", "")
                + "' AND od.tableID = '" + tableID + "' AND od.order_Id = '" + order_Id + "' AND payment_Status = 'Paid' ;";
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    title.setTextSize(26);
                    canvas.drawText(resultSet.getString("foodItem_Name"), 100, listViewCtr_Top, title);
                    canvas.drawText(resultSet.getString("quantity"), 450, listViewCtr_Top, title);
                    canvas.drawText("₹" + resultSet.getString("price_Amount"), 600, listViewCtr_Top, title);
                    listViewCtr_Top = listViewCtr_Top + 40;
                }
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        canvas.drawText("------------------------------------------------------------", 100, listViewCtr_Top, title);

        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Amount: ", 200, 895, title);
        canvas.drawText("₹" + Amount,300,895,title);
        canvas.drawText("Tax: ", 450, 895, title);
        canvas.drawText("₹" + Tax,520,895,title);
        canvas.drawText("Amount Paid: ", 200, 945, title);
        canvas.drawText("₹" + total_Amount_Paid,340,945,title);
        canvas.drawText(" via " + paid_Via,480,945,title);
        canvas.drawText("Have a good day !!", 396, 1000, title);
        pdfDocument.finishPage(myPage);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(directory, "DineIn_Invoice_Order#" + order_Id + ".pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(OrderPlacedTimerActivity.this,"Bill downloaded successfully",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong while generating and downloading PDF: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        pdfDocument.close();
        //openGeneratedPDF();
        try {
            if (connect != null) {
                String update_query = "UPDATE [ADMINISTRATOR].[Tables] "
                        + " SET reserved_by = NULL, mobileNo = NULL, table_Status = 'Available'"
                        + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                        + "' AND table_Id = '" + tableID + "';";
                PreparedStatement preStmt = connect.prepareStatement(update_query);
                preStmt.executeUpdate();
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(OrderPlacedTimerActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openGeneratedPDF(){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(directory, "DineIn_Invoice_Order#" + order_Id + ".pdf");
        if (file.exists())
        {
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(file);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try
            {
                startActivity(intent);
            }
            catch(ActivityNotFoundException e)
            {
                Toast.makeText(OrderPlacedTimerActivity.this, "No Application available to view pdf", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(OrderPlacedTimerActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_menuitem_dialog.setText("Are you sure you want to logout and exit?");
        Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
        button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connect != null) {
                        String update_query = "UPDATE [ADMINISTRATOR].[Tables] "
                                + " SET reserved_by = NULL, mobileNo = NULL, table_Status = 'Available'"
                                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND table_Id = '" + tableID + "';";
                        PreparedStatement preStmt = connect.prepareStatement(update_query);
                        preStmt.executeUpdate();
                        ConnectionResult="Success";
                        isSuccess=true;
                        dialog.dismiss();
                        OrderPlacedTimerActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(OrderPlacedTimerActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
        Button button_menuitem_no = (Button) dialog.findViewById(R.id.button_menuitem_no);
        button_menuitem_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.create();
        dialog.show();
    }
}
