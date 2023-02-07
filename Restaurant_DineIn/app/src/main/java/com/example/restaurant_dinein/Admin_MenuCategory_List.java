package com.example.restaurant_dinein;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_MenuCategory_List {
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;
    String query = "";

    public List<Map<String,String>> getList() {
        List<Map<String,String>> data = null;
        data = new ArrayList<Map<String,String>>();

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if(connect != null) {
                query = "SELECT foodItemCategory_Name FROM [ADMINISTRATOR].[FoodItem_Categories] WHERE foodItemCategory_IsDeleted = '0' ORDER BY date_updated desc;";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()) {
                    Map<String,String> dtname = new HashMap<String,String>();
                    dtname.put("foodCategory_Name", resultSet.getString("foodItemCategory_Name"));
                    data.add(dtname);
                }
                ConnectionResult="Success";
                isSuccess=true;
                connect.close();
            }
            else {
                ConnectionResult="Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return data;
    }
}