package com.example.restaurant_dinein;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Manager_MenuItemScreenActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<MenuItem> list;
    Admin_MenuList_Adapter adapter = null;

    String switchStatus_update="", selectedspinner_MenuItemCategory="", selectedspinner_MenuItemStatus="";
    Spinner spinner_MenuItemCategory, spinner_MenuItemStatus;
    ImageView image_FoodItemIcon;
    final Context context = this;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "";

    static String ManagerName = "";

    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text, textview_welcome;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_view_menuitemscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        ManagerName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setImageResource(R.drawable.ic_refresh);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Menu Item");

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new Admin_MenuList_Adapter(this, R.layout.menuitem_listlayouttemplate, list);
        listView.setAdapter(adapter);

        spinner_MenuItemCategory = (Spinner) findViewById(R.id.spinner_MenuItemCategory);
        fill_spinner_MenuItemCategory();
        spinner_MenuItemStatus = (Spinner) findViewById(R.id.spinner_MenuItemStatus);
        fill_spinner_MenuItemStatus();

        getData(selectedspinner_MenuItemCategory, selectedspinner_MenuItemStatus);

        spinner_MenuItemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedspinner_MenuItemCategory = spinner_MenuItemCategory.getSelectedItem().toString().trim();

                spinner_MenuItemStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedspinner_MenuItemStatus = spinner_MenuItemStatus.getSelectedItem().toString().trim();
                        getData(selectedspinner_MenuItemCategory, selectedspinner_MenuItemStatus);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                getData(selectedspinner_MenuItemCategory, selectedspinner_MenuItemStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem menuitem = list.get(position);
                String selectedItemName = menuitem.getName();
                String selectedItemAvailable = menuitem.getStatus();
                System.out.println("Selected1: " + selectedItemName + selectedItemAvailable);
                if(selectedItemAvailable.equals("Available")) {
                    selectedItemAvailable = "Not Available";
                }
                else {
                    selectedItemAvailable = "Available";
                }

                String finalSelectedItemAvailable = selectedItemAvailable;
                String finalSelectedItemName = selectedItemName;
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_dialog);
                TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
                text_menuitem_dialog.setText("Are you sure you want to mark '" + selectedItemName + "' as "
                        + selectedItemAvailable.toLowerCase() + "?");
                Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
                button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData(finalSelectedItemName, finalSelectedItemAvailable);
                        getData(selectedspinner_MenuItemCategory, selectedspinner_MenuItemStatus);
                        dialog.cancel();
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

                getData(selectedspinner_MenuItemCategory, selectedspinner_MenuItemStatus);
            }
        });

        imageView_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manager_MainScreenActivity.redirectActivity(Manager_MenuItemScreenActivity.this, Manager_MenuItemScreenActivity.class);
            }
        });
    }

    private void getData(String selectedspinner_MenuItemCategory, String selectedspinner_MenuItemStatus) {
        if((selectedspinner_MenuItemCategory.equals("All Category")
                || selectedspinner_MenuItemCategory.equals(""))
                && (selectedspinner_MenuItemStatus.equals("All Status")
                || selectedspinner_MenuItemStatus.equals(""))) {
            query = "SELECT foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Image, foodItem_Status FROM [ADMINISTRATOR].[FoodItems]"
                    + " ORDER BY date_updated desc;";
        }
        else if((selectedspinner_MenuItemCategory.equals("All Category")
                || selectedspinner_MenuItemCategory.equals(""))
                && selectedspinner_MenuItemStatus != "All Status") {
            query = "SELECT foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Image, foodItem_Status FROM [ADMINISTRATOR].[FoodItems]"
                    + " WHERE foodItem_Status = '" + selectedspinner_MenuItemStatus
                    + "' ORDER BY date_updated desc;";
        }
        else if(selectedspinner_MenuItemCategory != "All Category"
                && (selectedspinner_MenuItemStatus.equals("All Status")
                || selectedspinner_MenuItemStatus.equals(""))) {
            query = "SELECT foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Image, foodItem_Status FROM [ADMINISTRATOR].[FoodItems]"
                    + " WHERE foodItem_Category = '" + selectedspinner_MenuItemCategory
                    + "' ORDER BY date_updated desc;";
        }
        else {
            query = "SELECT foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Image, foodItem_Status FROM [ADMINISTRATOR].[FoodItems]"
                    + " WHERE foodItem_Category = '" + selectedspinner_MenuItemCategory
                    + "' AND foodItem_Status = '" + selectedspinner_MenuItemStatus
                    + "' ORDER BY date_updated desc;";
        }
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                list.clear();
                while (resultSet.next()) {
                    String category = resultSet.getString("foodItem_Category");
                    String name = resultSet.getString("foodItem_Name");
                    String price = '₹' + resultSet.getString("foodItem_Price");
                    String status = resultSet.getString("foodItem_Status");
                    String images = resultSet.getString("foodItem_Image");
                    byte[] image = Base64.decode(images, Base64.DEFAULT);

                    list.add(new MenuItem(name, price, status, category, image));
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

    public void updateData(String selectedItemName, String selectedItemAvailable) {
        try {
            if (connect != null) {
                query = "UPDATE [ADMINISTRATOR].[FoodItems] "
                        + "SET foodItem_Status = '" + selectedItemAvailable
                        + "', date_updated = GETDATE()"
                        + " WHERE foodItem_Name = '" + selectedItemName
                        + "';";
                Statement statement = connect.createStatement();
                statement.executeUpdate(query);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void fill_spinner_MenuItemCategory() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT foodItem_Category FROM [ADMINISTRATOR].[FoodItems];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Category";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("foodItem_Category");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_MenuItemCategory.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Manager_MenuItemScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void fill_spinner_MenuItemStatus() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT foodItem_Status FROM [ADMINISTRATOR].[FoodItems];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Status";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("foodItem_Status");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_MenuItemStatus.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Manager_MenuItemScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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
        recreate();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Orders
    public void ClickOrders (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_OrderScreenActivity.class);
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