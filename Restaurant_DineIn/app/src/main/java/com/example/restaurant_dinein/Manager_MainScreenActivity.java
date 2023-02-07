package com.example.restaurant_dinein;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Manager_MainScreenActivity extends AppCompatActivity {
    TextView textView_manager_manage_table, textView_manager_menu_item, textView_manager_view_orders;

    static String ManagerName="", StaffCategory="Manager";

    DrawerLayout drawerLayout;
    TextView toolbar_Text;
    ImageView imageView_Add;
    TextView textview_availabletables, textview_notavailabletables, textview_pending_totalorders, textview_accepted_totalorders;
    TextView textview_availablemenuitem, textview_notavailablemenuitem, textview_name, textview_totalsales, textView_AvgRating;

    Connection connect;
    String ConnectionResult = "";
    String engaged_table ="", available_table ="", not_available_menu = "", available_menu = "";
    String table_query ="", menu_query ="", pending_total_orders ="", pending_order_query="", accepted_order_query="";
    String sales_query="", total_sales="", accepted_total_orders="", avgRating_query = "", AvgRating="";
    Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_mainscreen);

        Intent intent = new Intent();
        ManagerName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Dashboard");
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setVisibility(View.GONE);
        textview_name = findViewById(R.id.textview_name);
        textview_name.setText("Welcome " + ManagerName);

        textview_availabletables = findViewById(R.id.textview_availabletables);
        textview_notavailabletables = findViewById(R.id.textview_notavailabletables);
        textview_availablemenuitem = findViewById(R.id.textview_availablemenuitem);
        textview_notavailablemenuitem = findViewById(R.id.textview_notavailablemenuitem);
        textview_pending_totalorders = findViewById(R.id.textview_pending_totalorders);
        textview_accepted_totalorders = findViewById(R.id.textview_accepted_totalorders);
        textview_totalsales = findViewById(R.id.textview_totalsales);
        textView_AvgRating = findViewById(R.id.textView_AvgRating);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        try {
            if (connect != null) {
                table_query = "SELECT ISNULL(SUM(available),0) AS available, ISNULL(SUM (engaged),0) AS engaged FROM ("
                        + "SELECT table_Status, CASE WHEN table_Status = 'Available' THEN COUNT(1) END AS available, "
                        + " CASE WHEN table_Status = 'Engaged' THEN COUNT(1) END AS engaged "
                        + " FROM [ADMINISTRATOR].[Tables] GROUP BY table_Status ) AS T;";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(table_query);
                while (resultSet.next()) {
                    available_table = resultSet.getString("available");
                    engaged_table = resultSet.getString("engaged");
                }
                textview_availabletables.setText("" + available_table);
                textview_notavailabletables.setText("" + engaged_table);


                menu_query = "SELECT SUM(available) AS available, SUM (not_available) AS not_available FROM ("
                        + "SELECT foodItem_Status, CASE WHEN foodItem_Status = 'Available' THEN COUNT(1) END AS available, "
                        + " CASE WHEN foodItem_Status = 'Not Available' THEN COUNT(1) END AS not_available "
                        + " FROM [ADMINISTRATOR].[FoodItems] GROUP BY foodItem_Status) AS T;";
                Statement statement1 = connect.createStatement();
                ResultSet resultSet1 = statement1.executeQuery(menu_query);
                while (resultSet1.next()) {
                    available_menu = resultSet1.getString("available");
                    not_available_menu = resultSet1.getString("not_available");
                }
                textview_availablemenuitem.setText("" + available_menu);
                textview_notavailablemenuitem.setText("" + not_available_menu);


                avgRating_query = "SELECT CAST(ISNULL(AVG((ISNULL(quality_rating, 0) + ISNULL(quantity_rating, 0) "
                        + " + ISNULL(service_rating, 0) + ISNULL(price_rating, 0) "
                        + " + ISNULL(ambience_rating, 0)) / 5), '0.0') AS NUMERIC(18,1)) as avg"
                        + " FROM [CUSTOMER].[FeedbackDetails];";
                Statement statement4 = connect.createStatement();
                ResultSet resultSet4 = statement4.executeQuery(avgRating_query);
                while (resultSet4.next()) {
                    AvgRating = resultSet4.getString("avg");
                }
                textView_AvgRating.setText("" + AvgRating);


                pending_order_query = "SELECT COUNT(distinct order_Id) AS pending_total_orders "
                        + " FROM [CUSTOMER].[OrderDetails]"
                        + " WHERE order_Status = 'Pending';";
                Statement statement2 = connect.createStatement();
                ResultSet resultSet2 = statement2.executeQuery(pending_order_query);
                while (resultSet2.next()) {
                    pending_total_orders = resultSet2.getString("pending_total_orders");
                }
                accepted_order_query = "SELECT COUNT(distinct order_Id) AS accepted_total_orders "
                        + " FROM [CUSTOMER].[OrderDetails]"
                        + " WHERE order_Status = 'Accepted';";
                Statement statement21 = connect.createStatement();
                ResultSet resultSet21 = statement21.executeQuery(accepted_order_query);
                while (resultSet21.next()) {
                    accepted_total_orders = resultSet21.getString("accepted_total_orders");
                }
                textview_pending_totalorders.setText("Pending: " + pending_total_orders);
                textview_accepted_totalorders.setText("Accepted: " + accepted_total_orders);


                sales_query = "SELECT ISNULL(SUM(total_Amount), '0.00') AS total_Sales "
                        + " FROM [CUSTOMER].[PaymentDetails]"
                        + " WHERE payment_Status = 'Paid';";
                Statement statement3 = connect.createStatement();
                ResultSet resultSet3 = statement3.executeQuery(sales_query);
                while (resultSet3.next()) {
                    total_sales = resultSet3.getString("total_Sales");
                    System.out.println("total_sales" + total_sales);
                }
                if(total_sales.equals("0") || total_sales.equals("")) {
                    textview_totalsales.setText("₹0.00");
                } else {
                    textview_totalsales.setText("₹" + total_sales);
                }


                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Manager_MainScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

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
        redirectActivity(this, Manager_TableScreenActivity.class);
    }

    //Menu Items
    public void ClickMenuItems (View view) {
        redirectActivity(this, Manager_MenuItemScreenActivity.class);
    }

    //Orders
    public void ClickOrders (View view) {
        redirectActivity(this, Manager_OrderScreenActivity.class);
    }

    //Feedback
    public void ClickFeedback (View view) {
        redirectActivity(this, Manager_FeedbackScreenActivity.class);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    //Home
    public void ClickHome (View view) {
        recreate();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity, aclass);
        intent.putExtra("staff_name", ManagerName);
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

