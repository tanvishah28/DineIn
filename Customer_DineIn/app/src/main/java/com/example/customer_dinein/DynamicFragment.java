package com.example.customer_dinein;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DynamicFragment extends Fragment {
    View view;
    String selectedTab="", mobile="", tableID="";
    int val;

    private Context context;
    private int layout;

    ListView listView;
    ArrayList<MenuItem> list;
    MenuList_Adapter adapter = null;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query_catgory = "", query_menuitem="";
    List<String> category_Array = new ArrayList<String>();
    List<Integer> status_Array = new ArrayList<Integer>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_dynamic, container, false);
        val = getArguments().getInt("someInt", 0);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        listView = (ListView) view.findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new MenuList_Adapter(getActivity(), R.layout.menuitem_listlayouttemplate, list);
        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            if (connect != null) {
                query_catgory = "SELECT foodItemCategory_Name FROM [ADMINISTRATOR].[FoodItem_Categories] WHERE foodItemCategory_IsDeleted = '0' ORDER BY date_updated asc;";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query_catgory);
                while (resultSet.next()) {
                    category_Array.add(resultSet.getString("foodItemCategory_Name"));
                }
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for (int i=0; i<=category_Array.size(); i++) {
            if(i == val) {
                selectedTab = category_Array.get(i);
            }
        }

        query_menuitem = "SELECT foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Image, foodItem_Status, a.quantity"
                + " FROM [ADMINISTRATOR].[FoodItems] fi LEFT JOIN [CUSTOMER].[AddToCart] a ON a.foodItem_Id = fi.foodItem_Id"
                + " WHERE foodItem_Category = '" + selectedTab
                + "';";
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query_menuitem);
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
                adapter.notifyDataSetChanged();
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException  throwables) {
            throwables.printStackTrace();
        }
    }

    public static DynamicFragment addfrag(int val) {
        DynamicFragment fragment = new DynamicFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", val);
        fragment.setArguments(args);
        return fragment;
    }

    public class MenuList_Adapter extends BaseAdapter {

        private Context context;
        private int layout;
        private ArrayList<MenuItem> MenuItem;
        int num = 0;
        String mobile = "", tableID = "", foodItem_Id = "0", foodItem_Name = "", foodItem_Category = "";

        Connection connection;

        String ConnectionResult = "";
        Boolean isSuccess = false;
        String query = "";
        int data_in_already_cart = 0;

        public MenuList_Adapter(Context context, int layout, ArrayList<MenuItem> MenuItem) {
            this.context = context;
            this.layout = layout;
            this.MenuItem = MenuItem;
            String mobile_tableID = ((CustomerHomeScreen)context).getMobile_tableID();
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

        private class ViewHolder{
            ImageView imageView, imgMinus, imgPlus, remove_icon;
            TextView txtName, txtPrice, txtStatus, txtCategory, txtNumbers;
            Button button_addtocart;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            View row = view;
            ViewHolder holder = null;
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connection = connectionHelper.connectionClass();

            if(row==null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row=inflater.inflate(layout, null);
                holder = new ViewHolder();

                holder.txtName = (TextView) row.findViewById(R.id.foodItem_Name);
                holder.txtPrice = (TextView) row.findViewById(R.id.foodItem_Price);
                holder.txtCategory = (TextView) row.findViewById(R.id.foodItem_Category);
                holder.imageView = (ImageView) row.findViewById(R.id.foodItem_Image);
                holder.button_addtocart = (Button) row.findViewById(R.id.button_addtocart);
                holder.imgMinus = (ImageView) row.findViewById(R.id.imgMinus);
                holder.imgPlus = (ImageView) row.findViewById(R.id.imgPlus);
                holder.remove_icon = (ImageView) row.findViewById(R.id.remove_icon);
                holder.txtNumbers = (TextView) row.findViewById(R.id.txtNumbers);

                holder.button_addtocart.setVisibility(View.VISIBLE);
                holder.txtNumbers.setVisibility(View.VISIBLE);
                holder.imgMinus.setVisibility(View.VISIBLE);
                holder.imgPlus.setVisibility(View.VISIBLE);
                holder.remove_icon.setVisibility(View.GONE);

                row.setTag(holder);
            }
            else {
                holder = (ViewHolder) row.getTag();
            }

            MenuItem menu = MenuItem.get(position);
            holder.txtName.setText(menu.getName());
            holder.txtPrice.setText(menu.getPrice());
            holder.txtCategory.setText(menu.getCategory());
            byte[] foodimage = menu.getImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(foodimage, 0, foodimage.length);
            holder.imageView.setImageBitmap(bitmap);

            holder.button_addtocart.setVisibility(View.VISIBLE);
            holder.txtNumbers.setVisibility(View.VISIBLE);
            holder.imgMinus.setVisibility(View.VISIBLE);
            holder.imgPlus.setVisibility(View.VISIBLE);
            holder.remove_icon.setVisibility(View.GONE);

            if(menu.getStatus().equals("Not Available")) {
                holder.txtNumbers.setVisibility(View.GONE);
                holder.imgPlus.setVisibility(View.GONE);
                holder.imgMinus.setVisibility(View.GONE);
                holder.button_addtocart.setVisibility(View.GONE);
                holder.remove_icon.setVisibility(View.GONE);
            }

            try {
                String getfoodItemId = "SELECT foodItem_Id FROM [ADMINISTRATOR].[FoodItems] WHERE foodItem_Name = '"
                        + menu.getName() + "' AND foodItem_Category = '" + menu.getCategory() + "';";
                execute_getFoodItemId_Query(getfoodItemId);

                data_in_already_cart = 0;
                if(connection != null) {
                    String isfoodItemIdInCart = "SELECT COUNT(*), quantity FROM [CUSTOMER].[AddToCart] WHERE foodItem_Id = '"
                            + foodItem_Id  + "' AND mobileNo = '" + mobile.replace("+91-", "")
                            + "' AND  tableId = '" + tableID + "' GROUP BY quantity;";

                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(isfoodItemIdInCart);
                    while(resultSet.next()) {
                        data_in_already_cart = Integer.parseInt(resultSet.getString(1));
                        num =Integer.parseInt(resultSet.getString("quantity"));
                    }

                    if(data_in_already_cart > 0) {
                        holder.txtNumbers.setVisibility(View.VISIBLE);
                        holder.imgPlus.setVisibility(View.VISIBLE);
                        holder.imgMinus.setVisibility(View.VISIBLE);
                        holder.button_addtocart.setVisibility(View.GONE);
                        holder.remove_icon.setVisibility(View.GONE);

                        holder.txtNumbers.setText(num+"");
                    }
                    ConnectionResult="Success";
                    isSuccess=true;
                }
                else {
                    ConnectionResult="Failed";
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            ViewHolder finalHolder = holder;
            holder.button_addtocart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalHolder.button_addtocart.setVisibility(View.GONE);
                    finalHolder.txtNumbers.setVisibility(View.VISIBLE);
                    finalHolder.imgMinus.setVisibility(View.VISIBLE);
                    finalHolder.imgPlus.setVisibility(View.VISIBLE);
                    finalHolder.remove_icon.setVisibility(View.GONE);

                    String getfoodItemId = "SELECT foodItem_Id FROM [ADMINISTRATOR].[FoodItems] WHERE foodItem_Name = '"
                            + menu.getName() + "' AND foodItem_Category = '" + menu.getCategory() + "';";
                    execute_getFoodItemId_Query(getfoodItemId);

                    data_in_already_cart = 0;
                    try {
                        if(connection != null) {
                            String isfoodItemIdInCart = "SELECT COUNT(*), quantity FROM [CUSTOMER].[AddToCart] WHERE foodItem_Id = '"
                                    + foodItem_Id  + "' AND mobileNo = '" + mobile.replace("+91-", "")
                                    + "' AND  tableId = '" + tableID + "' GROUP BY quantity;";
                            Statement statement = connection.createStatement();
                            ResultSet resultSet = statement.executeQuery(isfoodItemIdInCart);
                            while(resultSet.next()) {
                                data_in_already_cart = Integer.parseInt(resultSet.getString(1));
                                num =Integer.parseInt(resultSet.getString("quantity"));
                            }
                            if(data_in_already_cart > 0) {
                                finalHolder.button_addtocart.setVisibility(View.GONE);
                                finalHolder.txtNumbers.setVisibility(View.VISIBLE);
                                finalHolder.imgMinus.setVisibility(View.VISIBLE);
                                finalHolder.imgPlus.setVisibility(View.VISIBLE);
                                finalHolder.remove_icon.setVisibility(View.GONE);

                                finalHolder.txtNumbers.setText(num+"");
                                executeUpdateQuery(mobile, tableID);
                            }
                            else {
                                num = 1;
                                String insert_query = "INSERT INTO [CUSTOMER].[AddToCart] (mobileNo, tableId, foodItem_Id, quantity) VALUES ('"
                                        + mobile.replace("+91-", "") + "', '" + tableID + "', '" + foodItem_Id + "', '" + num + "');";
                                executeInsertQuery(insert_query);
                            }
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
            });

            holder.imgPlus.setOnClickListener((new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    num = Integer.parseInt(finalHolder.txtNumbers.getText().toString());
                    if(num>=9) {
                        Toast.makeText(context,
                                "You have reached max. no. of quantity that can be selected per dish", Toast.LENGTH_SHORT).show();
                    }
                    else {
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
                    if(num>1) {
                        num--;
                        finalHolder.txtNumbers.setText(num+"");

                        executeUpdateQuery(menu.getName(), menu.getCategory());
                    }
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
                    ConnectionResult="Success";
                    isSuccess=true;
                    Toast.makeText(context, "Quantity for the dish has been updated", Toast.LENGTH_SHORT).show();
                }
                else {
                    ConnectionResult="Failed";
                }
            } catch(Exception ex) {
                Toast.makeText(context, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }

        private void executeInsertQuery(String insert_query) {
            try {
                if (connection != null) {
                    PreparedStatement preStmt = connection.prepareStatement(insert_query);
                    preStmt.executeUpdate();
                    ConnectionResult="Success";
                    isSuccess=true;
                    Toast.makeText(context,
                            "Dish has been added to food cart", Toast.LENGTH_SHORT).show();
                }
                else {
                    ConnectionResult="Failed";
                }
            } catch(Exception ex) {
                Toast.makeText(context, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }

        private void execute_getFoodItemId_Query (String getfoodItemId) {
            try {
                if(connection != null) {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(getfoodItemId);
                    while(resultSet.next()) {
                        foodItem_Id = resultSet.getString("foodItem_Id");
                    }
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
    }
}