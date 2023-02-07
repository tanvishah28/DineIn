package com.example.restaurant_dinein;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager_Table_List {
    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String qu = "";

    public List<Map<String,String>>getList(String selectedTableStatus)
    {
        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String,String>>();
        if(selectedTableStatus.equals("Available") || selectedTableStatus.equals("Engaged")){
            qu = "SELECT T.table_Id, no_of_People_Allowed_Max, table_Status, "
                    + " CASE WHEN FB.time_slot IS NOT NULL"
                    + " THEN '(*) Reserved at ' + LEFT(CAST(FB.time_slot AS time), 5) + ' for +91-' + FB.mobileNo "
                    + " ELSE '(*) No Future Booking' END AS future_booking"
                    + " FROM [ADMINISTRATOR].[Tables] T LEFT JOIN [ADMINISTRATOR].[FutureTableBookings] FB"
                    + " ON T.table_Id = FB.table_Id "
                    + " WHERE table_Status = '" + selectedTableStatus
                    + "' ORDER BY T.date_entered desc, FB.date_entered desc;";
        } else {
            qu = "SELECT T.table_Id, no_of_People_Allowed_Max, table_Status, "
                    + " CASE WHEN FB.time_slot IS NOT NULL"
                    + " THEN '(*) Reserved at ' + LEFT(CAST(FB.time_slot AS time), 5) + ' for +91-' + FB.mobileNo "
                    + " ELSE '(*) No Future Booking' END AS future_booking"
                    + " FROM [ADMINISTRATOR].[Tables] T LEFT JOIN [ADMINISTRATOR].[FutureTableBookings] FB"
                    + " ON T.table_Id = FB.table_Id "
                    + " ORDER BY T.date_entered desc, FB.date_entered desc;";
        }
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(qu);
                while (resultSet.next()) {
                    Map<String, String> dtname = new HashMap<String, String>();
                    dtname.put("table_Id", "Table No.: " + resultSet.getString("table_Id"));
                    dtname.put("capacity", "Capacity: " + resultSet.getString("no_of_People_Allowed_Max"));
                    dtname.put("table_Status" , "Current Status: " + resultSet.getString("table_Status"));
                    dtname.put("future_booking", resultSet.getString("future_booking"));
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