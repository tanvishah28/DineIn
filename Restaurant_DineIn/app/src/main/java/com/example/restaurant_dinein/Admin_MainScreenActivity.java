package com.example.restaurant_dinein;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Admin_MainScreenActivity extends AppCompatActivity {
    TextView textview_availabletables, textview_notavailabletables;
    TextView textview_availablemenuitem, textview_notavailablemenuitem;

    static String AdminName="", StaffCategory="Admin";
    String engaged_table ="", available_table ="", not_available_menu = "", available_menu = "", table_query ="", menu_query ="";

    DrawerLayout drawerLayout;
    TextView toolbar_Text, textview_name;
    ImageView imageView_Add;

    Connection connect;
    String ConnectionResult = "", query="", selectedTableStatus="";
    Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mainscreen);

        Intent intent = new Intent();
        AdminName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Dashboard");
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setVisibility(View.GONE);
        textview_name = findViewById(R.id.textview_name);
        textview_name.setText("Welcome " + AdminName);

        textview_availabletables = findViewById(R.id.textview_availabletables);
        textview_notavailabletables = findViewById(R.id.textview_notavailabletables);
        textview_availablemenuitem = findViewById(R.id.textview_availablemenuitem);
        textview_notavailablemenuitem = findViewById(R.id.textview_notavailablemenuitem);
    }

    //Menu
    public void ClickMenu (View view) {
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    //Logo
    public void ClickLogo (View view) {
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    //Tables
    public void ClickTables (View view) {
        redirectActivity(this, Admin_TableScreenActivity.class);
    }

    //Menu Categories
    public void ClickMenuCategories (View view) {
        redirectActivity(this, Admin_MenuCategoryScreenActivity.class);
    }

    //Menu Items
    public void ClickMenuItems (View view) {
        redirectActivity(this, Admin_MenuItemScreenActivity.class);
    }

    //Staff Member Details
    public void ClickStaffDetails (View view) {
        redirectActivity(this, Admin_StaffScreenActivity.class);
    }

    //Orders
    public void ClickOrders (View view) {
        redirectActivity(this, Admin_OrderScreenActivity.class);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    //Home
    public void ClickHome (View view) {
        recreate();
        Admin_MainScreenActivity.closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity, aclass);
        intent.putExtra("staff_name", AdminName);
        intent.putExtra("staff_category", StaffCategory);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
       // activity.finish();
    }

    //Logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickLogout (View view) {
        logout(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void logout(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_logout_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_logout_dialog.setText("Are you sure you want to logout?");
        Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
        button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                redirectActivity(activity, Restaurant_WelcomeScreenActivity.class);
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

    //Exit
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickExit (View view) {
        exit(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void exit(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_logout_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_logout_dialog.setText("Are you sure you want to close the app?");
        Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
        button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finishAffinity();
                System.exit(0);
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
