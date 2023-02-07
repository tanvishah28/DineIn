package com.example.customer_dinein;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.smarteist.autoimageslider.SliderView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CustomerHomeScreen extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tab;
    private ViewPager viewPager;

    DrawerLayout drawerLayout;
    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "";
    
    String selectedTabName = "";
    int no_of_items = 0;
    int counter = 1;
    static String mobile = "", tableID = "";

    String url1 = getURLForResource_WelcomeScreen(R.drawable.home_screen_1);
    String url2 = getURLForResource_WelcomeScreen(R.drawable.home_screen_2);
    String url3 = getURLForResource_WelcomeScreen(R.drawable.home_screen_3);
    String url4 = getURLForResource_WelcomeScreen(R.drawable.home_screen_4);
    String url5 = getURLForResource_WelcomeScreen(R.drawable.home_screen_5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_screen);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("DineIn");
        imageView_Cart = findViewById(R.id.ic_click_cart);

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final DynamicFragment myFragment = new DynamicFragment();

        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();
        SliderView sliderView = findViewById(R.id.slider);
        sliderDataArrayList.add(new SliderData(url1));
        sliderDataArrayList.add(new SliderData(url2));
        sliderDataArrayList.add(new SliderData(url3));
        sliderDataArrayList.add(new SliderData(url4));
        sliderDataArrayList.add(new SliderData(url5));
        SliderAdapter sliderAdapter = new SliderAdapter(this, sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setScrollTimeInSec(2);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        mobile = String.format("+91-%s", getIntent().getStringExtra("mobile"));
        tableID = getIntent().getStringExtra("tableID");

        viewPager = findViewById(R.id.view_pager);
        tab = findViewById(R.id.tabLayout);

        try {
            if (connect != null) {
                query = "SELECT foodItemCategory_Name FROM [ADMINISTRATOR].[FoodItem_Categories] WHERE foodItemCategory_IsDeleted = '0' ORDER BY date_updated asc;";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    tab.addTab(tab.newTab().setText(resultSet.getString("foodItemCategory_Name")));
                }
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        adapter = new TabAdapter(getSupportFragmentManager(), tab.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(15);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        adapter.notifyDataSetChanged();

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                selectedTabName = tab.getText().toString();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imageView_Cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connect != null) {
                        query = "SELECT COUNT(*) FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                                + mobile.replace("+91-", "") + "' AND tableId = '" + tableID + "'";
                        Statement statement = connect.createStatement();
                        ResultSet resultSet = statement.executeQuery(query);
                        while (resultSet.next()) {
                            no_of_items = Integer.parseInt(resultSet.getString(1));
                        }
                        ConnectionResult = "Success";
                        isSuccess = true;
                    } else {
                        ConnectionResult = "Failed";
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                if(no_of_items > 0) {
                    Intent intent1 = new Intent(CustomerHomeScreen.this, com.example.customer_dinein.AddToCartMenuItemScreen.class);
                    intent1.putExtra("mobile", getIntent().getStringExtra("mobile"));
                    intent1.putExtra("tableID", tableID);
                    startActivity(intent1);
                    finish();
                }
                else {
                    Toast.makeText(CustomerHomeScreen.this, "No dish has been added to cart", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getURLForResource_WelcomeScreen (int resourceId) {
        //use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +resourceId).toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(CustomerHomeScreen.this);
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
                        if(no_of_items > 0) {
                            String delete_query = "DELETE FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                                    + mobile.replace("+91-", "") + "' AND tableId = '" + tableID + "';";
                            PreparedStatement preStmtDelete = connect.prepareStatement(delete_query);
                            preStmtDelete.executeUpdate();
                        }
                        ConnectionResult="Success";
                        isSuccess=true;
                        dialog.dismiss();
                        CustomerHomeScreen.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(CustomerHomeScreen.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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

    public String getMobile_tableID() {
        return mobile + "," + tableID;
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

    //HomeScreen
    public void ClickHomeScreen (View view) {
        recreate();
        CustomerHomeScreen.closeDrawer(drawerLayout);
    }

    //Orders
    public void ClickOrders (View view) {
        redirectActivity(this, OrderScreenActivity.class);
    }

    //Contact Us
    public void ClickContactUs(View view) {
        redirectActivity(this, ContactUsActivity.class);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity, aclass);
        intent.putExtra("mobile", mobile);
        intent.putExtra("tableID", tableID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    //Logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickLogout (View view) {
        logout(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void logout(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        Connection connect;
        String ConnectionResult = "";
        Boolean isSuccess = false;
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_logout_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_logout_dialog.setText("Are you sure you want to logout?");
        Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
        button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                        dialog.dismiss();
                    }
                    else {

                    }
                } catch(Exception ex) {

                }
                redirectActivity(activity, SplashActivity.class);
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