package com.example.restaurant_dinein;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Chef_ServedOrdersActivity extends AppCompatActivity {
    ListView listView ;
    ArrayList<ChefOrders_List> list;
    ChefOrders_List_Adapter adapter = null;

    TextView textView_Chef_Name;
    Spinner spinner_OrderSorting;

    DrawerLayout drawerLayout;
    TextView toolbar_Text;
    ImageView imageView_Add;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    static String ChefName="", StaffCategory="Chef";
    String query = "",get_orders_query="", chef_Name="",query_today = "",query_yesterday = "",query_lastmonth = "",query_old = "";
    String mobile ="", tableID="", order_Id="", order_placed ="", order_Status="", noOfItems = "",selectedOrderSorting="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_servedorders);

        Intent intent= new Intent();
        ChefName = getIntent().getStringExtra("staff_name");

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Past Served Orders");
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setImageResource(R.drawable.ic_refresh);

        textView_Chef_Name = (TextView) findViewById(R.id.textView_Chef_Name);
        spinner_OrderSorting = (Spinner) findViewById(R.id.spinner_OrderSorting);
        fill_spinner_OrderSorting();
        textView_Chef_Name.setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new ChefOrders_List_Adapter(this, R.layout.cheforders_listlayouttemplate, list);
        chef_Name = textView_Chef_Name.getText().toString();
        getOrderData(selectedOrderSorting);

        spinner_OrderSorting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedOrderSorting = spinner_OrderSorting.getSelectedItem().toString().trim();
                getOrderData(selectedOrderSorting);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void fill_spinner_OrderSorting() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT"
                        + " CASE WHEN CAST(date_updated as date) = CAST(GETDATE() AS DATE) THEN 'Today' "
                        + "	WHEN CAST(date_updated as date) = DATEADD(dd, -1, CAST(GETDATE() AS DATE)) THEN 'Yesterday'"
                        + "	WHEN MONTH(CAST(date_updated as date)) = MONTH(DATEADD(mm, 0, CAST(GETDATE() AS DATE)))"
                        + "		AND YEAR(CAST(date_updated as date)) = YEAR(CAST(GETDATE() AS DATE)) THEN 'This Month'"
                        + "	WHEN MONTH(CAST(date_updated as date)) = MONTH(DATEADD(mm, -1, CAST(GETDATE() AS DATE)))"
                        + "		AND YEAR(CAST(date_updated as date)) = YEAR(CAST(GETDATE() AS DATE)) THEN 'Last Month'"
                        + "	WHEN MONTH(CAST(date_updated as date)) < MONTH(DATEADD(mm, -1, CAST(GETDATE() AS DATE))) THEN 'Older'"
                        + "	END AS orders"
                        + " FROM [CUSTOMER].[OrderDetails]"
                        + " WHERE order_Status = 'Served' And chef_Name = '" + ChefName + "';";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Orders";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("orders");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_OrderSorting.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Chef_ServedOrdersActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void getOrderData(String selectedOrderSorting) {
        if(selectedOrderSorting.equals("Today")) {
            get_orders_query = "SELECT order_Id, mobileNo, tableID, COUNT(foodItem_Id) AS no_Of_Items, order_Status, "
                    +" CAST(date_updated as date) AS date_updated FROM [CUSTOMER].[OrderDetails]"
                    +" WHERE CAST(date_updated as date) = CAST(GETDATE() AS DATE) AND order_Status = 'Served'"
                    +" AND chef_name = '" + ChefName
                    +"' GROUP BY order_Id, mobileNo, tableID, order_Status, CAST(date_updated as date);";
        }
        else if(selectedOrderSorting.equals("Yesterday")) {
            get_orders_query = "SELECT order_Id, mobileNo, tableID, COUNT(foodItem_Id) AS no_Of_Items, order_Status, "
                    +" CAST(date_updated as date) AS date_updated FROM [CUSTOMER].[OrderDetails]"
                    +" WHERE CAST(date_updated as date) = DATEADD(dd, -1, CAST(GETDATE() AS DATE)) AND order_Status = 'Served'"
                    +" AND chef_name = '" + ChefName
                    +"' GROUP BY order_Id, mobileNo, tableID, order_Status, CAST(date_updated as date);";
        }
        else if(selectedOrderSorting.equals("This Month")) {
            get_orders_query = "SELECT order_Id, mobileNo, tableID, COUNT(foodItem_Id) AS no_Of_Items, order_Status, "
                    +" CAST(date_updated as date) AS date_updated FROM [CUSTOMER].[OrderDetails]"
                    +" WHERE MONTH(CAST(date_updated as date)) = MONTH(DATEADD(mm, 0, CAST(GETDATE() AS DATE))) "
                    +" AND YEAR(CAST(date_updated as date)) = YEAR(CAST(GETDATE() AS DATE)) AND order_Status = 'Served'"
                    +" AND chef_name = '" + ChefName
                    +"' GROUP BY order_Id, mobileNo, tableID, order_Status, CAST(date_updated as date);";
        }
        else if(selectedOrderSorting.equals("Last Month")) {
            get_orders_query = "SELECT order_Id, mobileNo, tableID, COUNT(foodItem_Id) AS no_Of_Items, order_Status, "
                    +" CAST(date_updated as date) AS date_updated FROM [CUSTOMER].[OrderDetails]"
                    +" WHERE MONTH(CAST(date_updated as date)) = MONTH(DATEADD(mm, -1, CAST(GETDATE() AS DATE))) "
                    +" AND YEAR(CAST(date_updated as date)) = YEAR(CAST(GETDATE() AS DATE)) AND order_Status = 'Served'"
                    +" AND chef_name = '" + ChefName
                    +"' GROUP BY order_Id, mobileNo, tableID, order_Status, CAST(date_updated as date);";
        }
        else if(selectedOrderSorting.equals("Older")) {
            get_orders_query = "SELECT order_Id, mobileNo, tableID, COUNT(foodItem_Id) AS no_Of_Items, order_Status, "
                    +" CAST(date_updated as date) AS date_updated FROM [CUSTOMER].[OrderDetails]"
                    +" WHERE MONTH(CAST(date_updated as date)) < MONTH(DATEADD(mm, -1, CAST(GETDATE() AS DATE))) "
                    +" AND order_Status = 'Served' AND chef_name = '" + ChefName
                    +"' GROUP BY order_Id, mobileNo, tableID, order_Status, CAST(date_updated as date);";
        }
        else {
            get_orders_query = "SELECT order_Id, mobileNo, tableID, COUNT(foodItem_Id), order_Status, "
                    +" CAST(date_updated AS VARCHAR(12)) + ' | ' + convert(varchar, cast(date_updated as time), 100) AS date_updated "
                    +" FROM [CUSTOMER].[OrderDetails] WHERE order_Status = 'Served' "
                    +" AND chef_Name = '" + ChefName
                    +"' GROUP BY mobileNo , tableID, order_Id, order_Status, CAST(date_updated AS VARCHAR(12)) + ' | ' + convert(varchar, cast(date_updated as time), 100)"
                    +" ORDER BY date_updated desc ;";
        }

        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(get_orders_query);
                list.clear();
                while (resultSet.next()) {
                    order_Id = resultSet.getString(1);
                    mobile = "+91-" + resultSet.getString(2);
                    tableID = resultSet.getString(3);
                    noOfItems = resultSet.getString(4);
                    order_Status = resultSet.getString(5);
                    order_placed = "Order# " + order_Id + ", placed on " + resultSet.getString(6);

                    list.add(new ChefOrders_List(order_placed,mobile,tableID,order_Id,order_Status,noOfItems));
                }
                listView.setAdapter(adapter);
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

    public class ChefOrders_List_Adapter extends BaseAdapter {
        private Context context;
        private int layout;
        private ArrayList<ChefOrders_List> ChefOrders_List,co;

        Connection connection;

        public ChefOrders_List_Adapter(Context context, int layout,ArrayList<ChefOrders_List> ChefOrders_List) {
            this.context = context;
            this.layout = layout;
            this.ChefOrders_List = ChefOrders_List;
        }

        @Override
        public int getCount() {
            return ChefOrders_List.size();
        }

        @Override
        public Object getItem(int position) {
            return ChefOrders_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView_order_DateTime,textView_MobNo, textView_TableNo, textView_OrderId,
                    textView_NoOfItemsOrdered, textView_OrderStatus;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View row = view;
            ViewHolder holder = new ViewHolder();

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionClass();

            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(layout, null);

                holder.textView_order_DateTime = (TextView) row.findViewById(R.id.textView_order_DateTime);
                holder.textView_MobNo = (TextView) row.findViewById(R.id.textView_MobNo);
                holder.textView_TableNo = (TextView) row.findViewById(R.id.textView_TableNo);
                holder.textView_OrderStatus = (TextView) row.findViewById(R.id.textView_OrderStatus);
                holder.textView_NoOfItemsOrdered = (TextView) row.findViewById(R.id.textView_NoOfItemsOrdered);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            ChefOrders_List chef = ChefOrders_List.get(position);
            holder.textView_order_DateTime.setText(chef.getDate());
            holder.textView_MobNo.setText(chef.getMobno());
            holder.textView_TableNo.setText(chef.getTableno());
            holder.textView_OrderStatus.setText(chef.getOrder_Status());
            holder.textView_NoOfItemsOrdered.setText(chef.getNoOfItems());

            return row;
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

    //Orders
    public void ClickOrders (View view) {
        redirectActivity(this, Chef_OrdersActivity.class);
    }

    //Past Orders
    public void ClickPastOrders (View view) {
        redirectActivity(this, Chef_ServedOrdersActivity.class);
    }

    //Home
    public void ClickHome (View view) {
        recreate();
        Chef_OrdersActivity.closeDrawer(drawerLayout);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    public static void redirectActivity(Activity activity, Class aclass) {
        Intent intent = new Intent(activity, aclass);
        intent.putExtra("staff_name", ChefName);
        intent.putExtra("staff_category", StaffCategory);
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
