package com.example.customer_dinein;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.customer_dinein.ConnectionHelper;
import com.google.android.gms.common.ConnectionResult;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class WelcomeScreenActivity extends AppCompatActivity {

    int num = 0, counter = 0;
    Connection connection;
    String ConnectionResult = "", tableID="", mobile = "";
    Boolean isSuccess;

    TextView tv_number, tv, textview_no_of_people;
    ImageView image_girl_on_table;
    View image_minus, image_plus;
    Button buttonTable;

    Animation blink_anim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connection = connectionHelper.connectionClass();

        blink_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        Intent intent = new Intent();
        mobile = String.format("+91-%s", getIntent().getStringExtra("mobile"));

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String message = "";
        if(timeOfDay >= 0 && timeOfDay < 12){
            message = "Hello, Good Morning!";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            message = "Hello, Good Afternoon!";
        }else if(timeOfDay >= 16 && timeOfDay < 24){
            message = "Hello, Good Evening!";
        }


        tv = findViewById(R.id.textview_greet);
        tv.setText(message);
        tv.startAnimation(blink_anim);

        tv_number = findViewById(R.id.txtNumbers);
        textview_no_of_people = findViewById(R.id.textview_no_of_people);
        image_girl_on_table = findViewById(R.id.image_girl_on_table);

        image_minus = findViewById(R.id.imgMinus);
        image_minus.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num>1) {
                    num--;
                    tv_number.setText(num+"");
                }
            }
        }));

        image_plus = findViewById(R.id.imgPlus);
        image_plus.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(num>11) {
                    Toast.makeText(WelcomeScreenActivity.this,
                            "You have reached max. no. of people that can be selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    num++;
                    tv_number.setText(num+"");
                }
            }
        }));

        buttonTable = findViewById(R.id.buttonTable);

        buttonTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image_minus.setEnabled(true);
                image_plus.setEnabled(true);
                buttonTable.setEnabled(true);
                if(buttonTable.getText().toString().equals("Find Table")
                        || buttonTable.getText().toString().equals("Try Again")) {
                    image_minus.setEnabled(false);
                    image_plus.setEnabled(false);
                    buttonTable.setEnabled(false);
                    String table_already_allocated_query = "SELECT TOP 1 table_id FROM [Administrator].[Tables] "
                            + " WHERE table_Status = 'Engaged' AND reserved_by = 'Customer' AND mobileNo = '"
                            + getIntent().getStringExtra("mobile") + "';";
                    executeTableAllocatedQuery(table_already_allocated_query);
                }
                else if(buttonTable.getText().toString().equals("Proceed")) {
                    String foodItem_Already_AddedToCart_Query = "SELECT COUNT(*) FROM [CUSTOMER].[AddToCart] "
                            + " WHERE mobileNo = '" + getIntent().getStringExtra("mobile")
                            + "' AND tableID = '" + tableID + "';";
                    executeFoodItemAlreadyAddedToCart(foodItem_Already_AddedToCart_Query);
                }
                else {
                    Intent intent = new Intent(WelcomeScreenActivity.this, com.example.customer_dinein.CustomerHomeScreen.class);
                    intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                    intent.putExtra("tableID", tableID);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(WelcomeScreenActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_menuitem_dialog.setText("Are you sure you want to logout and exit?");
        Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
        button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connection != null) {
                        String update_query = "UPDATE [ADMINISTRATOR].[Tables] "
                                + " SET reserved_by = NULL, mobileNo = NULL, table_Status = 'Available'"
                                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND table_Id = '" + tableID + "';";
                        PreparedStatement preStmt = connection.prepareStatement(update_query);
                        preStmt.executeUpdate();
                        ConnectionResult="Success";
                        isSuccess=true;
                        dialog.dismiss();
                        WelcomeScreenActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(WelcomeScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
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

    private void executeFoodItemAlreadyAddedToCart(String foodItem_already_addedToCart_query) {
        int result = 0, result_order = 0, result_payment = 0;
        String order_Id = "";
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(foodItem_already_addedToCart_query);
                while (resultSet.next()) {
                    result = Integer.parseInt(resultSet.getString(1));
                }
                if (result > 0) {
                    Intent intent = new Intent(WelcomeScreenActivity.this, com.example.customer_dinein.AddToCartMenuItemScreen.class);
                    intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                    intent.putExtra("tableID", tableID);
                    startActivity(intent);
                    finish();
                } else {
                    String foodItem_already_ordered_query = "SELECT od.order_Id, COUNT(*) "
                            + " FROM [CUSTOMER].[OrderDetails] od LEFT JOIN [CUSTOMER].[PaymentDetails] pd"
                            + " ON od.order_Id = pd.order_Id AND od.mobileNo = pd.mobileNo AND od.tableID = pd.tableID"
                            + " WHERE od.order_Status = 'Pending' AND od.mobileNo = '" + getIntent().getStringExtra("mobile")
                            + "' AND od.tableID = '" + tableID + "' AND pd.payment_Status IS NULL"
                            + " GROUP BY od.order_Id";
                    Statement statement1 = connection.createStatement();
                    ResultSet resultSet1 = statement1.executeQuery(foodItem_already_ordered_query);
                    while (resultSet1.next()) {
                        order_Id = resultSet1.getString(1);
                        result_order = Integer.parseInt(resultSet1.getString(2));
                    }
                    if (result_order>0) {
                        Intent intent = new Intent(WelcomeScreenActivity.this, com.example.customer_dinein.GenerateBillActivity.class);
                        intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                        intent.putExtra("tableID", tableID);
                        intent.putExtra("order_Id", order_Id);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        order_Id = "";
                        String foodItem_payment_query = "SELECT order_Id, COUNT(*) FROM [CUSTOMER].[PaymentDetails] "
                                + " WHERE mobileNo = '" + getIntent().getStringExtra("mobile")
                                + "' AND tableID = '" + tableID + "' AND payment_Status = 'Pending'"
                                + " GROUP BY order_Id";
                        Statement statement2 = connection.createStatement();
                        ResultSet resultSet2 = statement2.executeQuery(foodItem_payment_query);
                        while (resultSet2.next()) {
                            order_Id = resultSet2.getString(1);
                            result_payment = Integer.parseInt(resultSet2.getString(2));
                        }
                        if (result_payment>0) {
                            Intent intent = new Intent(WelcomeScreenActivity.this, com.example.customer_dinein.PaymentActivity.class);
                            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                            intent.putExtra("tableID", tableID);
                            intent.putExtra("order_Id", order_Id);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(WelcomeScreenActivity.this, com.example.customer_dinein.CustomerHomeScreen.class);
                            intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                            intent.putExtra("tableID", tableID);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void executeTableAllocatedQuery(String table_already_allocated_query) {
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(table_already_allocated_query);
                while (resultSet.next()) {
                    tableID = resultSet.getString(1);
                    counter++;
                }
                if (counter == 0) {
                    String search_query = "SELECT TOP 1 table_id FROM ( "
                            + " SELECT table_id FROM [Administrator].[FutureTableBookings] "
                            + " WHERE mobileNo = '" + getIntent().getStringExtra("mobile")
                            + "' AND DATEDIFF(mi, GETDATE(), time_slot) >= -30 AND DATEDIFF(mi, GETDATE(), time_slot) <= 30"
                            + " UNION ALL "
                            + " SELECT table_id FROM [Administrator].[ReserveTable] "
                            + " WHERE (" + tv_number.getText().toString()
                            + " BETWEEN '1' AND no_of_People_Allowed_Max) ) AS T;";
                    executeSearchQuery(search_query);
                }
                /*if (counter == 0) {
                    String search_query = "SELECT TOP 1 table_id FROM [Administrator].[ReserveTable] "
                            + " WHERE (" + tv_number.getText().toString()
                            + " BETWEEN '1' AND no_of_People_Allowed_Max)"
                            + " ORDER BY no_of_People_Allowed_Min , no_of_People_Allowed_Max;";
                    executeSearchQuery(search_query);
                } */ else {
                    textview_no_of_people.setText("You have already reserved Table no. " + tableID);
                    image_minus.setVisibility(View.GONE);
                    image_plus.setVisibility(View.GONE);
                    tv_number.setVisibility(View.GONE);
                    buttonTable.setText("Proceed");
                    buttonTable.setEnabled(true);
                }
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void executeSearchQuery(String search_query) {
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(search_query);

                while(resultSet.next()) {
                    tableID = resultSet.getString(1);
                    String update_query = "UPDATE [ADMINISTRATOR].[Tables] SET table_Status = 'Engaged'"
                            + ", reserved_by = 'Customer', mobileNo = '" + getIntent().getStringExtra("mobile")
                            + "' WHERE table_id = '" + tableID + "';";
                    executeUpdateQuery(update_query);

                    textview_no_of_people.setText("Please have seat on table no. " + tableID);
                    buttonTable.setText("Proceed");
                    buttonTable.setEnabled(true);
                    image_minus.setVisibility(View.INVISIBLE);
                    image_plus.setVisibility(View.INVISIBLE);
                    tv_number.setVisibility(View.INVISIBLE);
                }

                if (tableID.equals("")) {
                    textview_no_of_people.setText("Please wait until we find table for you");
                    image_minus.setVisibility(View.INVISIBLE);
                    image_plus.setVisibility(View.INVISIBLE);
                    tv_number.setVisibility(View.INVISIBLE);
                    Timer buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buttonTable.setText("Try Again");
                                    buttonTable.setEnabled(true);
                                }
                            });
                        }
                    }, 2000);
                }
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void executeUpdateQuery(String update_query) {
        try {
            if (connection != null) {
                Statement statement = connection.createStatement();
                statement.executeUpdate(update_query);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}