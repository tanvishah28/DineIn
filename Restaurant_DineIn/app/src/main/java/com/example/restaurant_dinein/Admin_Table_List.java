package com.example.restaurant_dinein;

import android.widget.Button;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_Table_List {
    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String qu = "";

    public List<Map<String,String>>getList(String selectedTableStatus)
    {
        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String,String>>();
        if(selectedTableStatus.equals("Available") || selectedTableStatus.equals("Engaged")){
            qu = "SELECT table_Id, no_of_People_Allowed_Max, table_Status FROM [ADMINISTRATOR].[Tables]"
                + " WHERE table_Status = '" + selectedTableStatus + "' ORDER BY date_entered desc;";
        } else {
            qu = "SELECT table_Id, no_of_People_Allowed_Max, table_Status FROM [ADMINISTRATOR].[Tables] ORDER BY date_entered desc;";
        }
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(qu);
                while (resultSet.next()) {
                    Map<String, String> dtname = new HashMap<String, String>();
                    dtname.put("table_Id", resultSet.getString("table_Id"));
                    dtname.put("capacity", resultSet.getString("no_of_People_Allowed_Max"));
                    dtname.put("table_Status" , resultSet.getString("table_Status"));
                    data.add(dtname);
                }
                ConnectionResult = "Success";
                isSuccess = true;
                connect.close();
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return data;
    }
}