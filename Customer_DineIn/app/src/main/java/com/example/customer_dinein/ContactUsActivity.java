package com.example.customer_dinein;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ContactUsActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart;

    Connection connect;
    String ConnectionResult = "", mobile="", tableID="";
    Boolean isSuccess = false;
    int no_of_items = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus_screen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        mobile = getIntent().getStringExtra("mobile");
        tableID = getIntent().getStringExtra("tableID");
        System.out.println("Contact Us - Mobile and tableID: " + mobile + "-" + tableID);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Contact Us");
        imageView_Cart = findViewById(R.id.ic_click_cart);
        imageView_Cart.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(ContactUsActivity.this);
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
                        ContactUsActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(ContactUsActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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

    //HomeScreen
    public void ClickHomeScreen (View view) {
        CustomerHomeScreen.redirectActivity(this, CustomerHomeScreen.class);
    }

    //Orders
    public void ClickOrders (View view) {
        CustomerHomeScreen.redirectActivity(this, OrderScreenActivity.class);
    }

    //Contact Us
    public void ClickContactUs(View view) {
        recreate();
        CustomerHomeScreen.closeDrawer(drawerLayout);
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
