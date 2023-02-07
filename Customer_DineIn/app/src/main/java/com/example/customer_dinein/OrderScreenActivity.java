package com.example.customer_dinein;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class OrderScreenActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<OrderItem> list;
    OrderList_Adapter adapter = null;

    SimpleAdapter simpleAdapter;
    Connection connect;
    String ConnectionResult = "", query="", mobile="", tableID="";
    static String AdminName = "";
    Boolean isSuccess = false;

    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_orders);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        mobile = String.format("+91-%s", getIntent().getStringExtra("mobile"));
        tableID = getIntent().getStringExtra("tableID");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_cart);
        imageView_Add.setVisibility(View.GONE);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("My Orders");

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new OrderList_Adapter(this, R.layout.vieworders_listlayouttemplate, list);
        listView.setAdapter(adapter);
        getData();
    }

    private void getData() {
        query = "SELECT * FROM [ADMINISTRATOR].[Order_Payment_Details] "
                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                + "' ORDER BY date_updated_distinct desc;";

        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                list.clear();
                while (resultSet.next()) {
                    String tableId = resultSet.getString("tableId");
                    String order_Id = resultSet.getString("order_Id");
                    String date_updated = "Order# " + order_Id + ", placed on " + resultSet.getString("date_updated");
                    String order_Status = resultSet.getString("order_Status");
                    String no_of_dishes = resultSet.getString("no_of_dishes");
                    String total_Amount = "₹" + resultSet.getString("total_Amount");
                    String payment_Status = resultSet.getString("payment_Status");
                    String payment_Status_paidvia = payment_Status;
                    if(payment_Status.equals("Paid")) {
                        payment_Status_paidvia = payment_Status + " via " + resultSet.getString("paid_Via");
                    }
                    list.add(new OrderItem(tableId, order_Id, date_updated,
                            order_Status, no_of_dishes, total_Amount, payment_Status_paidvia));
                }
                adapter.notifyDataSetChanged();
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
        final Dialog dialog = new Dialog(OrderScreenActivity.this);
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
                        String delete_query = "DELETE FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                                + mobile.replace("+91-", "") + "' AND tableId = '" + tableID + "';";
                        PreparedStatement preStmtDelete = connect.prepareStatement(delete_query);
                        preStmtDelete.executeUpdate();
                        ConnectionResult="Success";
                        isSuccess=true;
                        dialog.dismiss();
                        OrderScreenActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(OrderScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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

    //Menu
    public void ClickMenu (View view) {
        CustomerHomeScreen.openDrawer(drawerLayout);
    }

    //Logo
    public void ClickLogo (View view) {
        CustomerHomeScreen.closeDrawer(drawerLayout);
    }

    //Tables
    public void ClickHomeScreen (View view) {
        CustomerHomeScreen.redirectActivity(this, CustomerHomeScreen.class);
    }

    //Orders
    public void ClickOrders (View view) {
        recreate();
        CustomerHomeScreen.closeDrawer(drawerLayout);
    }

    //Orders
    public void ClickContactUs (View view) {
        CustomerHomeScreen.redirectActivity(this, ContactUsActivity.class);
    }

    //Logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickLogout (View view) {
        CustomerHomeScreen.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomerHomeScreen.closeDrawer(drawerLayout);
    }
}