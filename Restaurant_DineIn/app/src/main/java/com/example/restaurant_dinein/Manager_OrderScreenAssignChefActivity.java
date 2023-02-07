package com.example.restaurant_dinein;

import android.annotation.SuppressLint;
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
import java.util.List;
import java.util.Map;

public class Manager_OrderScreenAssignChefActivity extends AppCompatActivity {
    SimpleAdapter simpleAdapter;
    ListView listView;
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;
    String query = "", spinnerselectedChefName="", selectedItem="", selectedOrderId="";
    final Context context=this;

    static String ManagerName = "";

    Spinner spinner_ChefName;
    Button button_cancel, button_assign;
    TextView textView_order;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_assignchef_orderscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        ManagerName = getIntent().getStringExtra("staff_name");
        selectedItem = getIntent().getStringExtra("selectedItem");

        String[] arrOfStr = selectedItem.split(",", 7);
        selectedOrderId = arrOfStr[2].trim();

        listView = (ListView) findViewById(R.id.ListViewLayout);
        List<Map<String,String>> MyDataList = null;
        Manager_Chef_Order_List MyData = new Manager_Chef_Order_List();
        MyDataList = MyData.getList();

        String[] Fromw = {"staff_Name", "order_Status", "order_Id"};
        int[] Tow = {R.id.textView_displayChefName, R.id.textView_displayOrderStatus, R.id.textView_displayNoOfOrders};
        simpleAdapter = new SimpleAdapter(Manager_OrderScreenAssignChefActivity.this,
                MyDataList, R.layout.cheforderassignment_listlayouttemplate, Fromw, Tow);
        listView.setAdapter(simpleAdapter);

        textView_order = (TextView) findViewById(R.id.textView_order);
        textView_order.setText("Assign Chef to Order# " + selectedOrderId);

        spinner_ChefName = (Spinner) findViewById(R.id.spinner_ChefName);
        fillSpinner();

        spinner_ChefName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerselectedChefName = spinner_ChefName.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button_cancel = (Button) findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Manager_OrderScreenAssignChefActivity.this, Manager_OrderScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button_assign = (Button) findViewById(R.id.button_assign);
        button_assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(selectedOrderId);
                Intent intent = new Intent(Manager_OrderScreenAssignChefActivity.this, Manager_OrderScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void updateData(String selectedOrderId) {
        try {
            query = "UPDATE [CUSTOMER].[OrderDetails] "
                    + "SET order_Status = 'Accepted', "
                    + " chef_Name = '" + spinnerselectedChefName
                    + "' WHERE order_Id = '" + selectedOrderId
                    + "';";
            if (connect != null) {
                Statement statement = connect.createStatement();
                statement.executeUpdate(query);
                ConnectionResult="Success";
                isSuccess=true;
                Intent intent=new Intent(Manager_OrderScreenAssignChefActivity.this,Manager_OrderScreenActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                ConnectionResult="Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void fillSpinner() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT staff_Name FROM [ADMINISTRATOR].[Staff_Details] WHERE staff_Category = 'Chef';";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while (resultSet.next()) {
                    String staff_Name = resultSet.getString("staff_Name");
                    data.add(staff_Name);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_ChefName.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Manager_OrderScreenAssignChefActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
}