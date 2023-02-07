package com.example.restaurant_dinein;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_OrderScreenActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<OrderItem> list;
    Admin_OrderList_Adapter adapter = null;

    SimpleAdapter simpleAdapter;
    Connection connect;
    String ConnectionResult = "", query="", selectedOrderStatus="", selectedPaymentStatus="";
    static String AdminName = "";
    Boolean isSuccess = false;

    Spinner spinner_OrderStatus, spinner_PaymentStatus;
    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_orderscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        AdminName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setVisibility(View.GONE);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Orders");

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new Admin_OrderList_Adapter(this, R.layout.vieworders_listlayouttemplate, list);
        listView.setAdapter(adapter);

        spinner_OrderStatus = (Spinner) findViewById(R.id.spinner_OrderStatus);
        fill_spinner_OrderStatus();
        spinner_PaymentStatus = (Spinner) findViewById(R.id.spinner_PaymentStatus);
        fill_spinner_PaymentStatus();

        getData(selectedOrderStatus, selectedPaymentStatus);

        spinner_OrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrderStatus = spinner_OrderStatus.getSelectedItem().toString().trim();

                spinner_PaymentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedPaymentStatus = spinner_PaymentStatus.getSelectedItem().toString().trim();
                        getData(selectedOrderStatus, selectedPaymentStatus);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                getData(selectedOrderStatus, selectedPaymentStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fill_spinner_OrderStatus() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT order_Status FROM [CUSTOMER].[OrderDetails];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Orders";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("order_Status");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_OrderStatus.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Admin_OrderScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void fill_spinner_PaymentStatus() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT CASE WHEN payment_Status = 'Paid' "
                        + " THEN payment_Status + ' via ' + paid_via ELSE payment_Status "
                        + " END AS payment_Status FROM [CUSTOMER].[PaymentDetails];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Payments";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("payment_Status");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_PaymentStatus.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Admin_OrderScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void getData(String selectedOrderStatus, String selectedPaymentStatus) {
        if((selectedOrderStatus.equals("All Orders")
                || selectedOrderStatus.equals(""))
                && (selectedPaymentStatus.equals("All Payments")
                || selectedPaymentStatus.equals(""))) {
            query = "SELECT * FROM [ADMINISTRATOR].[Order_Payment_Details] "
                    + " ORDER BY date_updated_distinct desc;";
        }
        else if((selectedOrderStatus.equals("All Orders")
                || selectedOrderStatus.equals(""))
                && selectedPaymentStatus != "All Payments") {
            query = "SELECT * FROM [ADMINISTRATOR].[Order_Payment_Details] "
                    + " WHERE payment_Status + ' via ' + paid_via = '" + selectedPaymentStatus
                    + "' ORDER BY date_updated_distinct desc;";
        }
        else if(selectedOrderStatus != "All Orders"
                && (selectedPaymentStatus.equals("All Payments")
                || selectedPaymentStatus.equals(""))) {
            query = "SELECT * FROM [ADMINISTRATOR].[Order_Payment_Details] "
                    + " WHERE order_Status = '" + selectedOrderStatus
                    + "' ORDER BY date_updated_distinct desc;";
        }
        else {
            query = "SELECT * FROM [ADMINISTRATOR].[Order_Payment_Details] "
                    + " WHERE order_Status = '" + selectedOrderStatus
                    + "' AND payment_Status + ' via ' + paid_via = '" + selectedPaymentStatus
                    + "' ORDER BY date_updated_distinct desc;";
        }

        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                list.clear();
                while (resultSet.next()) {
                    String mobileNo = "+91-" + resultSet.getString("mobileNo");
                    String tableId = resultSet.getString("tableId");
                    String order_Id = resultSet.getString("order_Id");
                    String date_updated = "Order# " + order_Id + ", placed on " + resultSet.getString("date_updated");
                    String order_Status = resultSet.getString("order_Status");
                    String chef_Name = resultSet.getString("chef_Name");
                    String no_of_dishes = resultSet.getString("no_of_dishes");
                    String total_Amount = "₹" + resultSet.getString("total_Amount");
                    String payment_Status = resultSet.getString("payment_Status");
                    String payment_Status_paidvia = payment_Status;
                    if(payment_Status.equals("Paid")) {
                        payment_Status_paidvia = payment_Status + " via " + resultSet.getString("paid_Via");
                    }
                    list.add(new OrderItem(mobileNo, tableId, order_Id, date_updated,
                            order_Status, chef_Name, no_of_dishes, total_Amount, payment_Status_paidvia));
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

    //Menu
    public void ClickMenu (View view) {
        Admin_MainScreenActivity.openDrawer(drawerLayout);
    }

    //Logo
    public void ClickLogo (View view) {
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Tables
    public void ClickTables (View view) {
        Admin_MainScreenActivity.redirectActivity(this, Admin_TableScreenActivity.class);
    }

    //Menu Categories
    public void ClickMenuCategories (View view) {
        Admin_MainScreenActivity.redirectActivity(this, Admin_MenuCategoryScreenActivity.class);
    }

    //Menu Items
    public void ClickMenuItems (View view) {
        Admin_MainScreenActivity.redirectActivity(this, Admin_MenuItemScreenActivity.class);
    }

    //Staff Member Details
    public void ClickStaffDetails (View view) {
        Admin_MainScreenActivity.redirectActivity(this, Admin_StaffScreenActivity.class);
    }

    //Orders
    public void ClickOrders (View view) {
        recreate();
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        Admin_MainScreenActivity.redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    //Home
    public void ClickHome (View view) {
        Admin_MainScreenActivity.redirectActivity(this, Admin_MainScreenActivity.class);
    }

    //Logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickLogout (View view) {
        Admin_MainScreenActivity.logout(this);
    }

    //exit
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickExit (View view) {
        Admin_MainScreenActivity.exit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
    }
}