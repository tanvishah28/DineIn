package com.example.customer_dinein;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class AddToCartMenuItemScreen extends AppCompatActivity {

    ListView listView;
    ArrayList<MenuItem> list;
    AddToCartMenuItem_List_Adapter adapter = null;

    Button button_addMoreItem, button_placeOrder;
    TextView textview_total_item_amount;

    DrawerLayout drawerLayout;
    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "", get_total_amount_query="", price="";
    String mobile ="", tableID="";
    int no_of_items = 0, counter_orderDetails = 0;

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtocart_screen);

        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Food Cart");
        imageView_Cart = findViewById(R.id.ic_click_cart);
        imageView_Cart.setVisibility(View.GONE);

        Intent intent = new Intent();
        mobile = getIntent().getStringExtra("mobile");
        tableID = getIntent().getStringExtra("tableID");
        System.out.println("AddToCart: " + mobile + "-" + tableID);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new AddToCartMenuItem_List_Adapter(this, R.layout.menuitem_listlayouttemplate, list);
        getData(mobile, tableID);

        textview_total_item_amount = (TextView) findViewById(R.id.textview_total_item_amount);

        button_addMoreItem = (Button) findViewById(R.id.button_addMoreItem);
        button_addMoreItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AddToCartMenuItemScreen.this, com.example.customer_dinein.CustomerHomeScreen.class);
                intent1.putExtra("mobile", mobile);
                intent1.putExtra("tableID", tableID);
                startActivity(intent1);
            }
        });

        button_placeOrder = (Button) findViewById(R.id.button_placeOrder);
        button_placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCounter();
                String order_Id = String.valueOf(counter_orderDetails);
                System.out.println("button_placeOrder - order_Id - " + order_Id);
                String insert_query = "INSERT INTO [CUSTOMER].[OrderDetails] (order_Id, mobileNo, tableId, foodItem_Id, quantity, price_Amount)"
                        + " SELECT '" + counter_orderDetails + "', mobileNo, tableId, A.foodItem_Id,"
                        + " quantity, (quantity * foodItem_Price) AS price_Amount"
                        + " FROM [CUSTOMER].[AddToCart] A INNER JOIN [ADMINISTRATOR].[FoodItems] FI"
                        + " ON A.foodItem_Id = FI.foodItem_Id"
                        + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                        + "' AND tableId = '" + tableID + "';";
                executeInsertDeleteQuery(insert_query);

                Intent intent1 = new Intent(AddToCartMenuItemScreen.this, com.example.customer_dinein.GenerateBillActivity.class);
                intent1.putExtra("mobile", getIntent().getStringExtra("mobile"));
                intent1.putExtra("tableID", tableID);
                intent1.putExtra("order_Id", order_Id);
                startActivity(intent1);
                finish();
            }
        });

        calculate_total_amount(mobile, tableID);
        textview_total_item_amount.setText(price+"");
    }

    private void executeInsertDeleteQuery(String insert_query) {
        try {
            if (connect != null) {
                PreparedStatement preStmt = connect.prepareStatement(insert_query);
                preStmt.executeUpdate();
                String delete_query = "DELETE FROM [CUSTOMER].[AddToCart]"
                        + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                        + "' AND tableId = '" + tableID + "';";
                PreparedStatement preStmt1 = connect.prepareStatement(delete_query);
                preStmt1.executeUpdate();
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(AddToCartMenuItemScreen.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public int getCounter () {
        String getCounter_query = "SELECT (NEXT VALUE FOR [CUSTOMER].[sequence_OrderDetails]);";
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(getCounter_query);
                while (resultSet.next()) {
                    counter_orderDetails = Integer.parseInt(resultSet.getString(1));
                    System.out.println("counter_orderDetails - " + counter_orderDetails);
                }
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return counter_orderDetails;
    }

    public String calculate_total_amount(String mobile, String tableID) {
        price = "";
        get_total_amount_query = "SELECT SUM(foodItem_Price * quantity) FROM [ADMINISTRATOR].[FoodItems] fi "
                + " INNER JOIN [CUSTOMER].[AddToCart] a ON a.foodItem_Id = fi.foodItem_Id "
                + " WHERE a.mobileNo = '" + mobile.replace("+91-", "")
                + "' AND a.tableID = '" + tableID
                + "';";
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(get_total_amount_query);
                while (resultSet.next()) {
                    price = '₹' + resultSet.getString(1);
                }
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return price;
    }

    private void getData(String mobile, String tableID) {
        query = "SELECT foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Image, foodItem_Status, a.quantity"
                + " FROM [ADMINISTRATOR].[FoodItems] fi INNER JOIN [CUSTOMER].[AddToCart] a ON a.foodItem_Id = fi.foodItem_Id "
                + " WHERE a.mobileNo = '" + mobile.replace("+91-", "")
                + "' AND a.tableID = '" + tableID
                + "';";
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
                    String quantity = resultSet.getString("quantity");
                    String images = resultSet.getString("foodItem_Image");
                    byte[] image = Base64.decode(images, Base64.DEFAULT);

                    list.add(new MenuItem(name, price, status, category, quantity, image));
                }
                listView.setAdapter(adapter);
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public String getMobile_tableID() {
        return mobile + "," + tableID;
    }

    public class AddToCartMenuItem_List_Adapter extends BaseAdapter {

        private Context context;
        private int layout;
        private ArrayList<MenuItem> MenuItem, mi;

        int num = 1;
        String mobile = "", tableID = "", foodItem_Id = "";

        Connection connection;
        String ConnectionResult = "";
        Boolean isSuccess = false;
        String query = "";

        public AddToCartMenuItem_List_Adapter(Context context, int layout, ArrayList<MenuItem> MenuItem) {
            this.context = context;
            this.layout = layout;
            this.MenuItem = MenuItem;

            String mobile_tableID = ((AddToCartMenuItemScreen) context).getMobile_tableID();
            String[] arrOfStr = mobile_tableID.split(",", 4);
            mobile = arrOfStr[0].trim();
            tableID = arrOfStr[1].trim();
        }

        @Override
        public int getCount() {
            return MenuItem.size();
        }

        @Override
        public Object getItem(int position) {
            return MenuItem.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            ImageView imageView, imgMinus, imgPlus, remove_icon;
            TextView txtName, txtPrice, txtStatus, txtCategory, txtNumbers;
            Button button_addtocart;
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

                holder.txtName = (TextView) row.findViewById(R.id.foodItem_Name);
                holder.txtPrice = (TextView) row.findViewById(R.id.foodItem_Price);
                holder.txtCategory = (TextView) row.findViewById(R.id.foodItem_Category);
                holder.imageView = (ImageView) row.findViewById(R.id.foodItem_Image);
                holder.button_addtocart = (Button) row.findViewById(R.id.button_addtocart);
                holder.imgMinus = (ImageView) row.findViewById(R.id.imgMinus);
                holder.imgPlus = (ImageView) row.findViewById(R.id.imgPlus);
                holder.remove_icon = (ImageView) row.findViewById(R.id.remove_icon);
                holder.txtNumbers = (TextView) row.findViewById(R.id.txtNumbers);

                holder.button_addtocart.setVisibility(View.GONE);
                holder.txtNumbers.setVisibility(View.VISIBLE);
                holder.imgMinus.setVisibility(View.VISIBLE);
                holder.imgPlus.setVisibility(View.VISIBLE);
                holder.remove_icon.setVisibility(View.VISIBLE);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            MenuItem menu = MenuItem.get(position);
            holder.txtName.setText(menu.getName());
            holder.txtPrice.setText(menu.getPrice());
            holder.txtCategory.setText(menu.getCategory());
            holder.txtNumbers.setText(menu.getQuantity());

            byte[] foodimage = menu.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(foodimage, 0, foodimage.length);
            holder.imageView.setImageBitmap(bitmap);


            ViewHolder finalHolder = holder;
            holder.imgPlus.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    num = Integer.parseInt(finalHolder.txtNumbers.getText().toString());

                    if (num >=9) {
                        Toast.makeText(context,
                                "You have reached max. no. of quantity that can be selected per dish", Toast.LENGTH_SHORT).show();
                    } else {
                        num++;
                        finalHolder.txtNumbers.setText(num + "");
                        executeUpdateQuery(menu.getName(), menu.getCategory());
                    }
                }
            }));

            holder.imgMinus.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    num = Integer.parseInt(finalHolder.txtNumbers.getText().toString());

                    if (num > 1) {
                        num--;
                        finalHolder.txtNumbers.setText(num + "");
                        executeUpdateQuery(menu.getName(), menu.getCategory());
                    }
                }
            }));

            holder.remove_icon.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    executeDeleteQuery(menu.getName(), menu.getCategory());
                    //MenuItem.remove(position);
                    ((AddToCartMenuItemScreen)context).getData(mobile, tableID);
                }
            }));

            return row;
        }

        private void executeUpdateQuery(String foodItem_Name, String foodItem_Category) {
            try {
                String getfoodItemId = "SELECT foodItem_Id FROM [ADMINISTRATOR].[FoodItems] WHERE foodItem_Name = '"
                        + foodItem_Name + "' AND foodItem_Category = '" + foodItem_Category + "';";
                execute_getFoodItemId_Query(getfoodItemId);

                String update_query = "UPDATE [CUSTOMER].[AddToCart] SET quantity = '" + num
                        + "', date_updated = GETDATE()"
                        + " WHERE mobileNo = '" + mobile.replace("+91-", "") + "' AND  tableId = '" + tableID
                        + "' AND foodItem_Id = '" + foodItem_Id + "';";

                if (connection != null) {
                    PreparedStatement preStmt = connection.prepareStatement(update_query);
                    preStmt.executeUpdate();
                    ConnectionResult = "Success";
                    isSuccess = true;
                    Toast.makeText(context, "Quantity for the dish has been updated", Toast.LENGTH_SHORT).show();
                    String price = ((AddToCartMenuItemScreen)context).calculate_total_amount(mobile, tableID);
                    ((AddToCartMenuItemScreen)context).textview_total_item_amount.setText(price+"");
                } else {
                    ConnectionResult = "Failed";
                }
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }

        private void executeDeleteQuery(String foodItem_Name, String foodItem_Category) {
            try {
                String getfoodItemId = "SELECT foodItem_Id FROM [ADMINISTRATOR].[FoodItems] WHERE foodItem_Name = '"
                        + foodItem_Name + "' AND foodItem_Category = '" + foodItem_Category + "';";
                execute_getFoodItemId_Query(getfoodItemId);

                String delete_query = "DELETE FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                        + mobile.replace("+91-", "") + "' AND  tableId = '" + tableID
                        + "' AND foodItem_Id = '" + foodItem_Id + "';";

                if (connection != null) {
                    PreparedStatement preStmt = connection.prepareStatement(delete_query);
                    preStmt.executeUpdate();
                    ConnectionResult = "Success";
                    isSuccess = true;
                    Toast.makeText(context, "Dish has been removed from the cart", Toast.LENGTH_SHORT).show();

                    try {
                        if (connect != null) {
                            query = "SELECT COUNT(*) FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                                    + mobile.replace("+91-", "") + "' AND tableId = '" + tableID + "'";
                            System.out.println("No of items query" + query);
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
                        String price = ((AddToCartMenuItemScreen)context).calculate_total_amount(mobile, tableID);
                        ((AddToCartMenuItemScreen)context).textview_total_item_amount.setText(price+"");
                    }
                    else {
                        Intent intent1 = new Intent(((AddToCartMenuItemScreen)context), com.example.customer_dinein.CustomerHomeScreen.class);
                        intent1.putExtra("mobile", getIntent().getStringExtra("mobile"));
                        intent1.putExtra("tableID", tableID);
                        startActivity(intent1);
                        finish();
                        Toast.makeText(((AddToCartMenuItemScreen)context), "All dishes are removed from cart", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ConnectionResult = "Failed";
                }
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }

        public void execute_getFoodItemId_Query(String getfoodItemId) {
            try {
                if (connection != null) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(getfoodItemId);
                    while (resultSet.next()) {
                        foodItem_Id = resultSet.getString("foodItem_Id");
                    }
                    ConnectionResult = "Success";
                    isSuccess = true;
                } else {
                    ConnectionResult = "Failed";
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(AddToCartMenuItemScreen.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_menuitem_dialog.setText("Are you sure you want to exit?");
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
                        AddToCartMenuItemScreen.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(AddToCartMenuItemScreen.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                finish();
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
        CustomerHomeScreen.redirectActivity(this, ContactUsActivity.class);
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