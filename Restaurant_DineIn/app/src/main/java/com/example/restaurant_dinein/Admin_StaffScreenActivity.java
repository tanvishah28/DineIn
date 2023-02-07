package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.restaurant_dinein.R;

import java.util.List;
import java.util.Map;

public class Admin_StaffScreenActivity extends AppCompatActivity {
    SimpleAdapter simpleAdapter;
    ListView listView;

    static String AdminName = "";

    DrawerLayout drawerLayout;
    ImageView imageView_Add, staff_icon;
    TextView toolbar_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_staffmember);

        Intent intent = new Intent();
        AdminName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Staff Member Details");

        listView = findViewById(R.id.ListViewStaffLayout);
        List<Map<String, String>> MyDataList = null;
        Admin_Staff_List MyData = new Admin_Staff_List();
        MyDataList = MyData.getList();

        String[] Fromw = {"staff_Category", "staff_Name","staff_MobileNo"};
        int[] Tow = {R.id.staff_Category, R.id.staff_Name, R.id.staff_MobileNo};

        simpleAdapter = new SimpleAdapter(getApplicationContext(), MyDataList, R.layout.staffmember_listlayouttemplate, Fromw, Tow);
        //setContentView(R.layout.staffmember_listlayouttemplate);

        listView.setAdapter(simpleAdapter);

        List<Map<String, String>> finalMyDataList = MyDataList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedStaff = adapterView.getItemAtPosition(i).toString();

                Intent intent = new Intent(Admin_StaffScreenActivity.this, Admin_Staff_UpdateDeleteActivity.class);
                intent.putExtra("selectedStaff", selectedStaff);
                startActivity(intent);
            }
        });

        imageView_Add.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin_StaffScreenActivity.this,Admin_Staff_AddActivity.class);
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
        Admin_MainScreenActivity.redirectActivity(this, Admin_MenuCategoryScreenActivity.class);
    }

    //Menu Items
    public void ClickMenuItems (View view) {
        Admin_MainScreenActivity.redirectActivity(this, Admin_MenuItemScreenActivity.class);
    }

    //Staff Member Details
    public void ClickStaffDetails (View view) {
        recreate();
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
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
