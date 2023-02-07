package com.example.restaurant_dinein;

import android.app.Dialog;
import android.content.Context;
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

public class Manager_OrderScreenActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<OrderItem> list;
    Admin_OrderList_Adapter adapter = null;
    final Context context = this;

    SimpleAdapter simpleAdapter;
    Connection connect;
    String ConnectionResult = "", query="", selectedOrderStatus="", selectedPaymentStatus="";
    static String ManagerName = "";
    Boolean isSuccess = false;

    Spinner spinner_OrderStatus, spinner_PaymentStatus;
    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_view_orderscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        ManagerName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setImageResource(R.drawable.ic_refresh);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderItem orderItem = list.get(position);
                String selectedItem = orderItem.getMobileNo()+","+orderItem.getTableId()+","+orderItem.getOrder_Id()
                        +","+orderItem.getOrder_Status()+","+orderItem.getPayment_Status();
                System.out.print("selectedItem - " + selectedItem);

                if(orderItem.getOrder_Status().equals("Cancelled") || orderItem.getOrder_Status().equals("Ready to Serve")
                        || orderItem.getOrder_Status().equals("Preparing") || orderItem.getOrder_Status().equals("Served")) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog);
                    TextView text_orderitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
                    text_orderitem_dialog.setText("Order# " + orderItem.getOrder_Id() + " is " + orderItem.getOrder_Status()
                            + ". No operations can be performed on this order.");
                    Button button_orderitem_ok = (Button) dialog.findViewById(R.id.button_menuitem_yes);
                    button_orderitem_ok.setText("Ok");
                    button_orderitem_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    Button button_orderitem_cancel = (Button) dialog.findViewById(R.id.button_menuitem_no);
                    button_orderitem_cancel.setVisibility(View.GONE);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.create();
                    dialog.show();
                }
                else if(orderItem.getOrder_Status().equals("Pending")
                        && (orderItem.getPayment_Status().equals("Pending") || orderItem.getPayment_Status().equals(""))) {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog);
                    TextView text_orderitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
                    text_orderitem_dialog.setText("Order# " + orderItem.getOrder_Id() + " is " + orderItem.getOrder_Status()
                            + " with Pending payment. No operations can be performed on this order.");
                    Button button_orderitem_ok = (Button) dialog.findViewById(R.id.button_menuitem_yes);
                    button_orderitem_ok.setText("Ok");
                    button_orderitem_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    Button button_orderitem_cancel = (Button) dialog.findViewById(R.id.button_menuitem_no);
                    button_orderitem_cancel.setVisibility(View.GONE);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.create();
                    dialog.show();
                }
                else if(orderItem.getOrder_Status().equals("Pending") && orderItem.getPayment_Status().contains("Paid")) {
                    Intent intent = new Intent(Manager_OrderScreenActivity.this, Manager_OrderScreenAssignChefActivity.class);
                    intent.putExtra("selectedItem", selectedItem);
                    startActivity(intent);
                }
                else if(orderItem.getOrder_Status().equals("Accepted") && orderItem.getPayment_Status().contains("Paid")) {
                    Intent intent = new Intent(Manager_OrderScreenActivity.this, Manager_OrderScreenAssignChefActivity.class);
                    intent.putExtra("selectedItem", selectedItem);
                    startActivity(intent);
                }
            }
        });

        imageView_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manager_MainScreenActivity.redirectActivity(Manager_OrderScreenActivity.this, Manager_OrderScreenActivity.class);
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
            Toast.makeText(Manager_OrderScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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
            Toast.makeText(Manager_OrderScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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
                    + " WHERE CASE WHEN payment_Status not in ('', 'Pending') THEN payment_Status + ' via ' + paid_via ELSE payment_Status END = '" + selectedPaymentStatus
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
                    + "' AND CASE WHEN payment_Status not in ('', 'Pending') THEN payment_Status + ' via ' + paid_via ELSE payment_Status END = '" + selectedPaymentStatus
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
        Manager_MainScreenActivity.openDrawer(drawerLayout);
    }

    //Logo
    public void ClickLogo (View view) {
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Tables
    public void ClickTables (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_TableScreenActivity.class);
    }

    //Menu Items
    public void ClickMenuItems (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_MenuItemScreenActivity.class);
    }

    //Orders
    public void ClickOrders (View view) {
        recreate();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Feedback
    public void ClickFeedback (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_FeedbackScreenActivity.class);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        Manager_MainScreenActivity.redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    //Home
    public void ClickHome (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_MainScreenActivity.class);
    }

    //Logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickLogout (View view) {
        Manager_MainScreenActivity.logout(this);
    }

    //exit
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickExit (View view) {
        Manager_MainScreenActivity.exit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }
}