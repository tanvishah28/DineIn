package com.example.restaurant_dinein;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Chef_OrderDetails_Activity extends AppCompatActivity {
    ListView listView;
    ArrayList<ChefOrderDetails_List> list;
    ChefOrderDetails_List_Adapter adapter = null;

    TextView textView_OrderId;
    Button button_Close,button_ReadyToServe;
    ImageView chef_order_status_image;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "", order_Id="",selectedItem="",selectedOrderId="", selectedOrderStatus="";
    String updatePreparingQuery ="", updateServeQuery ="", input_Mobile="", ChefName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_orderdetails);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        ChefName = getIntent().getStringExtra("staff_name");
        System.out.println("Chef Order No. & ChefName : " +ChefName);
        selectedItem = getIntent().getStringExtra("selectedItem");

        String[] arrOfStr = selectedItem.split(",", 10);
        selectedOrderId = arrOfStr[4].trim();
        selectedOrderStatus = arrOfStr[5].trim();
        System.out.println("Chef Order No. and Order Status : " + selectedOrderId + "," + selectedOrderStatus);

        textView_OrderId = (TextView) findViewById(R.id.textView_OrderId);
        textView_OrderId.setText(selectedOrderId);

        chef_order_status_image = (ImageView) findViewById(R.id.chef_order_status_image);

        button_Close =(Button) findViewById(R.id.button_Close);
        button_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Chef_OrderDetails_Activity.this,Chef_OrdersActivity.class);
                intent.putExtra("staff_name", ChefName);
                startActivity(intent);
                finish();
            }
        });
        button_ReadyToServe =(Button) findViewById(R.id.button_ReadyToServe);
        if (selectedOrderStatus.equals("Accepted"))
        {
            button_ReadyToServe.setText("Preparing");
            chef_order_status_image.setImageResource(R.drawable.image_orderplaced);
            button_ReadyToServe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updatePreparingQuery = "UPDATE [CUSTOMER].[OrderDetails] SET order_Status = 'Preparing',date_updated = GETDATE()"
                            +" WHERE order_Id = '"+ selectedOrderId
                            + "' and chef_Name ='" + ChefName
                            + "' and order_Status = 'Accepted';";
                    System.out.println("Chef Preparing query: "+updatePreparingQuery);
                    try {
                        if (connect != null) {
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(updatePreparingQuery);
                            ConnectionResult="Success";
                            isSuccess=true;
                            Intent intent=new Intent(Chef_OrderDetails_Activity.this,Chef_OrdersActivity.class);
                            intent.putExtra("staff_name", ChefName);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            ConnectionResult="Failed";
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }
        else if(selectedOrderStatus.equals("Preparing"))
        {
            button_ReadyToServe.setText("Ready To Serve");
            chef_order_status_image.setImageResource(R.drawable.image_foodispreparing);
            button_ReadyToServe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateServeQuery = "UPDATE [CUSTOMER].[OrderDetails] SET order_Status = 'Ready to Serve',date_updated = GETDATE()"
                            +" WHERE order_Id = '"+ selectedOrderId
                            + "' and chef_Name ='" + ChefName
                            + "' and order_Status = 'Preparing';";
                    System.out.println("Chef Serve query: "+updateServeQuery);
                    try {
                        if (connect != null) {
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(updateServeQuery);
                            ConnectionResult="Success";
                            isSuccess=true;
                            Intent intent=new Intent(Chef_OrderDetails_Activity.this,Chef_OrdersActivity.class);
                            intent.putExtra("staff_name", ChefName);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            ConnectionResult="Failed";
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }

        else if(selectedOrderStatus.equals("Ready to Serve"))
        {
            button_ReadyToServe.setText("Served");
            chef_order_status_image.setImageResource(R.drawable.image_readytoserve);
            button_ReadyToServe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateServeQuery = "UPDATE [CUSTOMER].[OrderDetails] SET order_Status = 'Served',date_updated = GETDATE()"
                            +" WHERE order_Id = '"+ selectedOrderId
                            + "' and chef_Name ='" + ChefName
                            + "' and order_Status = 'Ready to Serve';";
                    System.out.println("Chef Serve query: "+updateServeQuery);
                    try {
                        if (connect != null) {
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(updateServeQuery);
                            ConnectionResult="Success";
                            isSuccess=true;
                            Intent intent=new Intent(Chef_OrderDetails_Activity.this,Chef_OrdersActivity.class);
                            intent.putExtra("staff_name", ChefName);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            ConnectionResult="Failed";
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });
        }

        listView = (ListView) findViewById(R.id.ListViewLayout);
        list = new ArrayList<>();
        adapter = new ChefOrderDetails_List_Adapter(this, R.layout.cheforderdetails_listlayouttemplate, list);
        getData();
    }

    private void getData() {
        query = "SELECT fi.foodItem_Name, od.quantity "
                +" FROM [ADMINISTRATOR].[FoodItems] fi INNER JOIN [CUSTOMER].[OrderDetails] od ON od.foodItem_Id = fi.foodItem_Id "
                +" WHERE order_Id = '" + selectedOrderId + "';";
        System.out.println("Chef order details: "+query);
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                list.clear();
                while (resultSet.next()) {
                    String foodItem_Name = resultSet.getString("foodItem_Name");
                    String quantity = resultSet.getString("quantity");

                    list.add(new ChefOrderDetails_List(foodItem_Name,quantity));
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

    public class ChefOrderDetails_List_Adapter extends BaseAdapter {
        private Context context;
        private int layout;
        private ArrayList<ChefOrderDetails_List> ChefOrderDetails_List, cod;

        Connection connection;

        public ChefOrderDetails_List_Adapter(Context context, int layout, ArrayList<ChefOrderDetails_List> ChefOrderDetails_List) {
            this.context = context;
            this.layout = layout;
            this.ChefOrderDetails_List = ChefOrderDetails_List;
        }

        @Override
        public int getCount() {
            return ChefOrderDetails_List.size();
        }

        @Override
        public Object getItem(int position) {
            return ChefOrderDetails_List.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView textView_FoodItemName,textView_QtyOrdered;
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

                holder.textView_FoodItemName = (TextView) row.findViewById(R.id.textView_FoodItemName);
                holder.textView_QtyOrdered = (TextView) row.findViewById(R.id.textView_QtyOrdered);

                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            ChefOrderDetails_List chef = ChefOrderDetails_List.get(position);
            holder.textView_FoodItemName.setText(chef.getName());
            holder.textView_QtyOrdered.setText(chef.getQuantity());

            return row;
        }
    }
}
