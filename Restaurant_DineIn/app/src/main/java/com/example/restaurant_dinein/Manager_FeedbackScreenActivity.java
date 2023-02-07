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

public class Manager_FeedbackScreenActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<FeedbackList> list;
    Manager_FeedbackList_Adapter adapter = null;

    static String ManagerName = "";
    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "";

    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_view_feedback);

        Intent intent = new Intent();
        ManagerName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setImageResource(R.drawable.ic_refresh);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Feedback");

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new Manager_FeedbackList_Adapter(this, R.layout.feedback_listlayouttemplate, list);
        listView.setAdapter(adapter);

        getData();

        imageView_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manager_MainScreenActivity.redirectActivity(Manager_FeedbackScreenActivity.this, Manager_FeedbackScreenActivity.class);
            }
        });
    }

    private void getData() {
        query = "SELECT f.order_Id, f.mobileNo, quality_rating, quantity_rating, service_rating, price_rating, ambience_rating, avg_rating "
                + " FROM [CUSTOMER].[FeedbackDetails] f LEFT JOIN ( "
                + " SELECT order_Id, mobileNo, "
                + " AVG(quality_rating + quantity_rating + service_rating + price_rating + ambience_rating)/5 AS avg_rating "
                + " FROM [CUSTOMER].[FeedbackDetails] "
                + " GROUP BY order_Id, mobileNo) AS temp "
                + " ON f.order_Id = temp.order_ID "
                + " AND f.mobileNo = temp.mobileNo "
                + " ORDER BY f.date_updated DESC;";
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                list.clear();
                while (resultSet.next()) {
                    String order_Id = "Order No.: " + resultSet.getString("order_Id");
                    String mobileNo = "Mobile No.: +91-" + resultSet.getString("mobileNo");
                    String quality_rating = "Quality of food: " + resultSet.getString("quality_rating") + " out of 5";
                    String quantity_rating = "Quantity of food: " + resultSet.getString("quantity_rating") + " out of 5";
                    String service_rating = "Service: " + resultSet.getString("service_rating") + " out of 5";
                    String price_rating = "Price: " + resultSet.getString("price_rating") + " out of 5";
                    String ambience_rating = "Ambience: " + resultSet.getString("ambience_rating") + " out of 5";
                    String avg_rating = resultSet.getString("avg_rating");

                    list.add(new FeedbackList(order_Id, mobileNo, quality_rating, avg_rating, quantity_rating,
                            service_rating, price_rating, ambience_rating));
                }
                adapter.notifyDataSetChanged();
                ConnectionResult = "Success";
                isSuccess = true;
                connect.close();
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
        Manager_MainScreenActivity.redirectActivity(this, Manager_OrderScreenActivity.class);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        Manager_MainScreenActivity.redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    //Feedback
    public void ClickFeedback (View view) {
        recreate();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
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
