package com.example.restaurant_dinein;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_Staff_List extends AppCompatActivity
{
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;
    String query = "";

    public List<Map<String, String>> getList() {
        List<Map<String,String>> data = null;
        data = new ArrayList<Map<String,String>>();

        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if(connect != null) {
                query = "SELECT staff_Category, staff_Name, staff_MobileNo, staff_Password FROM [ADMINISTRATOR].[Staff_Details] "
                        + " WHERE staff_IsDeleted = '0' ORDER BY date_updated desc ;";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()) {
                    Map<String,String> dtname = new HashMap<String,String>();
                    dtname.put("staff_Category", resultSet.getString("staff_Category"));
                    dtname.put("staff_Name", resultSet.getString("staff_Name"));
                    dtname.put("staff_MobileNo", "+91-" + resultSet.getString("staff_MobileNo"));
                    dtname.put("staff_Password", resultSet.getString("staff_Password"));
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
