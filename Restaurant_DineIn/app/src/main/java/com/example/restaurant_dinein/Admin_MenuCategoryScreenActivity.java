package com.example.restaurant_dinein;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.restaurant_dinein.R;

import java.util.List;
import java.util.Map;

public class Admin_MenuCategoryScreenActivity extends AppCompatActivity {
    SimpleAdapter simpleAdapter;
    ListView listView;

    static String AdminName = "";

    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_menucategoryscreen);

        Intent intent = new Intent();
        AdminName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Menu Category");

        listView = findViewById(R.id.ListViewLayout);
        List<Map<String, String>> MyDataList = null;
        Admin_MenuCategory_List MyData = new Admin_MenuCategory_List();
        MyDataList = MyData.getList();

        String[] Fromw = {"foodCategory_Name"};
        int[] Tow = {R.id.foodCategory_Name};
        simpleAdapter = new SimpleAdapter(getApplicationContext(), MyDataList, R.layout.menucategories_listlayouttemplate, Fromw, Tow);
        listView.setAdapter(simpleAdapter);

        List<Map<String, String>> finalMyDataList = MyDataList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                String selectedItemName = selectedItem.replace("{foodCategory_Name=", "").replace("}", "");

                Intent intent = new Intent(Admin_MenuCategoryScreenActivity.this, Admin_MenuCategory_UpdateDeleteActivity.class);
                intent.putExtra("selectedItemName", selectedItemName);
                startActivity(intent);
            }
        });

        imageView_Add.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Admin_MenuCategoryScreenActivity.this, Admin_MenuCategory_AddActivity.class);
                startActivity(intent);
            }
        }));
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
        recreate();
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
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