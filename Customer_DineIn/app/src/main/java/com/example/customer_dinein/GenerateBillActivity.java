package com.example.customer_dinein;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class GenerateBillActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<GenerateBill_List> list;
    GenerateBillActivity.GenerateBill_List_Adapter adapter = null;

    Button button_PayHere , button_CancelOrder;
    TextView textView_AmountDisplay, textView_TaxDisplay, textView_OrderNo;
    TextView textView_MobNo, textView_TableNo, textView_OrderDateTime, textView_TotalPayableAmountDisplay;

    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart, imageMenu;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "", mobile ="", tableID="", order_Id="", order_placed ="",
            get_total_amount_query="", Amount="", Tax="", total_Amount="";
    int no_of_items = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generatebill_screen);

        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Order Bill");
        imageView_Cart = findViewById(R.id.ic_click_cart);
        imageView_Cart.setVisibility(View.GONE);
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);

        Intent intent = new Intent();
        mobile = String.format("+91-%s", getIntent().getStringExtra("mobile"));
        tableID = getIntent().getStringExtra("tableID");
        order_Id = getIntent().getStringExtra("order_Id");

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new GenerateBillActivity.GenerateBill_List_Adapter(this, R.layout.generatebill_listlayouttemplate, list);
        getData(mobile, tableID, order_Id);

        textView_OrderNo = (TextView) findViewById(R.id.textView_OrderNo);
        textView_MobNo = (TextView) findViewById(R.id.textView_MobNo);
        textView_TableNo = (TextView) findViewById(R.id.textView_TableNo);
        textView_AmountDisplay = (TextView) findViewById(R.id.textView_AmountDisplay);
        textView_TaxDisplay = (TextView) findViewById(R.id.textView_TaxDisplay);
        textView_OrderDateTime = (TextView) findViewById(R.id.textView_OrderDateTime);
        textView_TotalPayableAmountDisplay = (TextView) findViewById(R.id.textView_TotalPayableAmountDisplay);
        button_CancelOrder = (Button) findViewById(R.id.button_CancelOrder);
        button_PayHere = (Button) findViewById(R.id.button_PayHere);

        button_CancelOrder.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(GenerateBillActivity.this);
                dialog.setContentView(R.layout.custom_dialog);
                TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
                text_menuitem_dialog.setText("Are you sure you want to cancel this order?");
                Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
                button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (connect != null) {
                                String update_query = "UPDATE [CUSTOMER].[OrderDetails] SET order_Status = 'Cancelled',date_updated = GETDATE()"
                                        + " WHERE order_Status = 'Pending' AND order_Id = '" + order_Id
                                        + "' AND mobileNo = '" + mobile.replace("+91-", "")
                                        + "' AND tableId = '" + tableID + "';";
                                PreparedStatement preStmt = connect.prepareStatement(update_query);
                                preStmt.executeUpdate();
                                ConnectionResult="Success";
                                isSuccess=true;
                                dialog.dismiss();

                                Intent intent1 = new Intent(GenerateBillActivity.this, com.example.customer_dinein.CustomerHomeScreen.class);
                                intent1.putExtra("mobile", getIntent().getStringExtra("mobile"));
                                intent1.putExtra("tableID", tableID);
                                startActivity(intent1);
                                finish();
                            }
                            else {
                                ConnectionResult="Failed";
                            }
                        } catch(Exception ex) {
                            Toast.makeText(GenerateBillActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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
        });

        calculate_total_amount(mobile, tableID, order_Id);

        button_PayHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connect != null) {
                        String insert_query = "INSERT INTO [CUSTOMER].[PaymentDetails]"
                                + " (order_Id, mobileNo, tableId, noOfDishes, total_Amount)"
                                + " SELECT DISTINCT order_Id, mobileNo, tableId, COUNT(foodItem_Id) AS noOfDishes,"
                                + " SUM(price_Amount + tax_Amount) AS total_Amount"
                                + " FROM [CUSTOMER].[OrderDetails]"
                                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND tableId = '" + tableID + "' AND order_Id = '" + order_Id
                                + "' AND order_Status = 'Pending'"
                                + " GROUP BY order_Id, mobileNo, tableId;";
                        PreparedStatement preStmt = connect.prepareStatement(insert_query);
                        preStmt.executeUpdate();
                        ConnectionResult="Success";
                        isSuccess=true;

                        Intent intent1 = new Intent(GenerateBillActivity.this, PaymentActivity.class);
                        intent1.putExtra("mobile", getIntent().getStringExtra("mobile"));
                        intent1.putExtra("tableID", tableID);
                        intent1.putExtra("order_Id", order_Id);
                        startActivity(intent1);
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                }
                catch(Exception ex) {
                    Toast.makeText(GenerateBillActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getData(String mobile, String tableID, String order_Id) {
        query = "SELECT fi.foodItem_Name, od.price_Amount, od.quantity, foodItem_Image"
                + " FROM [ADMINISTRATOR].[FoodItems] fi INNER JOIN [CUSTOMER].[OrderDetails] od ON od.foodItem_Id = fi.foodItem_Id "
                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                + "' AND tableID = '" + tableID + "' AND order_Id = '" + order_Id + "';";
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                list.clear();
                while (resultSet.next()) {
                    String name = resultSet.getString("foodItem_Name");
                    String price = "₹" + resultSet.getString("price_Amount");
                    String quantity = "Qty: " + resultSet.getString("quantity");
                    String images = resultSet.getString("foodItem_Image");
                    byte[] image = Base64.decode(images, Base64.DEFAULT);

                    list.add(new GenerateBill_List(name, price, quantity, image));
                }
                listView.setAdapter(adapter);
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void calculate_total_amount(String mobile, String tableID, String order_Id) {
        get_total_amount_query =  "SELECT order_Id, mobileNo, tableID,"
                + " SUM(price_Amount) AS Amount, CAST(SUM(tax_Amount) AS NUMERIC(9,2)) AS Tax, "
                + " CAST(SUM(price_Amount + tax_Amount) AS NUMERIC(9,2)) AS total_Amount, "
                + " CAST(date_entered AS VARCHAR(12)) + ' | ' + convert(varchar, cast(date_entered as time), 100)"
                + " FROM [CUSTOMER].[OrderDetails] "
                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                + "' AND tableID = '" + tableID + "' AND order_Id = '" + order_Id
                + "' GROUP BY mobileNo , tableID, order_Id,"
                + " CAST(date_entered AS VARCHAR(12)) + ' | ' + convert(varchar, cast(date_entered as time), 100);";
        
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(get_total_amount_query);
                while (resultSet.next()) {
                    order_Id = resultSet.getString(1);
                    mobile = resultSet.getString(2);
                    tableID = resultSet.getString(3);
                    Amount = resultSet.getString(4);
                    Tax = resultSet.getString(5);
                    total_Amount = resultSet.getString(6);
                    order_placed = resultSet.getString(7);
                }
                textView_OrderNo.setText("Order#: " + order_Id);
                textView_MobNo.setText("Mobile No.: +91-" + mobile);
                textView_TableNo.setText("Table No.: " + tableID);
                textView_AmountDisplay.setText("₹" + Amount);
                textView_TaxDisplay.setText("₹" + Tax);
                textView_OrderDateTime.setText("Order Date: " + order_placed);
                textView_TotalPayableAmountDisplay.setText("₹" + total_Amount);

                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(GenerateBillActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_menuitem_dialog.setText("Are you sure you want to cancel this order and exit?");
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
                        String delete_query = "DELETE FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                                + mobile.replace("+91-", "") + "' AND tableId = '" + tableID + "';";
                        PreparedStatement preStmtDelete = connect.prepareStatement(delete_query);
                        preStmtDelete.executeUpdate();
                        String update_order_query = "UPDATE [CUSTOMER].[OrderDetails] SET order_Status = 'Cancelled',date_updated = GETDATE()"
                                + " WHERE order_Status = 'Pending' AND order_Id = '" + order_Id
                                + "' AND mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND tableId = '" + tableID + "';";
                        PreparedStatement preStmt_Order = connect.prepareStatement(update_order_query);
                        preStmt_Order.executeUpdate();
                        ConnectionResult="Success";
                        isSuccess=true;
                        dialog.dismiss();
                        GenerateBillActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(GenerateBillActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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

    public String getMobile_tableID_order_Id() {
        return mobile + "," + tableID + "," + order_Id;
    }

    public class GenerateBill_List_Adapter extends BaseAdapter {
        private Context context;
        private int layout;
        private ArrayList<GenerateBill_List> GenerateBill_List, gb;

        String mobile = "", tableID = "", foodItem_Id = "";

        Connection connection;

        public GenerateBill_List_Adapter(Context context, int layout, ArrayList<GenerateBill_List> GenerateBill_List) {
            this.context = context;
            this.layout = layout;
            this.GenerateBill_List = GenerateBill_List;

            String mobile_tableID_order_id = ((GenerateBillActivity) context).getMobile_tableID_order_Id();
            String[] arrOfStr = mobile_tableID_order_id.split(",", 4);
            mobile = arrOfStr[0].trim();
            tableID = arrOfStr[1].trim();
            order_Id = arrOfStr[2].trim();
            System.out.println("Customer-GenerateBillAdapter " + mobile_tableID_order_id + "-" + mobile + " " + tableID + " " + order_Id);
        }

        @Override
        public int getCount() {
            return GenerateBill_List.size();
        }

        @Override
        public Object getItem(int position) {
            return GenerateBill_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView txtName, txtPrice, txtQuantity;
            ImageView imageView;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View row = view;
            GenerateBillActivity.GenerateBill_List_Adapter.ViewHolder holder = new GenerateBillActivity.GenerateBill_List_Adapter.ViewHolder();

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionClass();

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layout, null);

                holder.txtName = (TextView) row.findViewById(R.id.textView_FoodItem);
                holder.txtPrice = (TextView) row.findViewById(R.id.textView_Price);
                holder.txtQuantity = (TextView) row.findViewById(R.id.textView_Qty);
                holder.imageView = (ImageView) row.findViewById(R.id.foodItem_Image);

                row.setTag(holder);
            } else {
                holder = (GenerateBillActivity.GenerateBill_List_Adapter.ViewHolder) row.getTag();
            }

            GenerateBill_List bill_list = GenerateBill_List.get(position);
            holder.txtName.setText(bill_list.getName());
            holder.txtPrice.setText(bill_list.getPrice());
            holder.txtQuantity.setText(bill_list.getQuantity());
            byte[] foodimage = bill_list.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(foodimage, 0, foodimage.length);
            holder.imageView.setImageBitmap(bitmap);

            return row;
        }
    }
}