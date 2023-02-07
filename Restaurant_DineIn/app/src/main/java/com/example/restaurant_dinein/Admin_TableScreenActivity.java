package com.example.restaurant_dinein;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Admin_TableScreenActivity extends AppCompatActivity {
    SimpleAdapter simpleAdapter;
    ListView listView;

    Connection connect;
    String ConnectionResult = "", query="", selectedTableStatus="";
    static String AdminName = "";
    Boolean isSuccess = false;

    Spinner spinner_TableStatus;
    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_table);
        listView = (ListView) findViewById(R.id.simpleListView);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        AdminName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Tables");

        spinner_TableStatus = (Spinner) findViewById(R.id.spinner_TableStatus);
        fillSpinner();

        spinner_TableStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTableStatus = spinner_TableStatus.getSelectedItem().toString().trim();
                List<Map<String,String>> MyDataList = null;
                Admin_Table_List MyData = new Admin_Table_List();
                MyDataList = MyData.getList(selectedTableStatus);

                String[] Fromw = {"table_Id", "capacity", "table_Status"};
                int[] Tow = {R.id.textViewTableId, R.id.textViewNoOfPeople, R.id.textViewTableStatus};
                simpleAdapter = new SimpleAdapter(Admin_TableScreenActivity.this, MyDataList, R.layout.table_listlayouttemplate, Fromw, Tow);
                listView.setAdapter(simpleAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imageView_Add.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_TableScreenActivity.this,Admin_Table_AddActivity.class);
                startActivity(intent);
            }
        }));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                Intent intent = new Intent(Admin_TableScreenActivity.this, Admin_Table_UpdateDeleteActivity.class);
                intent.putExtra("selectedItem", selectedItem);
                startActivity(intent);
            }
        });
    }

    public void fillSpinner() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT table_Status FROM [ADMINISTRATOR].[Tables];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Table Status";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("table_Status");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_TableStatus.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Admin_TableScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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
        recreate();
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
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
        Admin_MainScreenActivity.redirectActivity(this, Admin_OrderScreenActivity.class);
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