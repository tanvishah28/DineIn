package com.example.restaurant_dinein;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Manager_Chef_Order_List {
    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String query = "";

    public List<Map<String,String>> getList()
    {
        List<Map<String, String>> data = null;
        data = new ArrayList<>();
        query = "SELECT temp.staff_Name, temp.order_Status,"
                + " ISNULL(STUFF(("
                + " 		SELECT ',' + CAST(order_Id AS VARCHAR)"
                + " 		FROM [ADMINISTRATOR].[Staff_Details] SD LEFT JOIN ("
                + " 		SELECT DISTINCT chef_name, order_Status, order_Id "
                + " 		FROM [CUSTOMER].[OrderDetails]"
                + " 		WHERE order_Status IN ('Accepted', 'Ready to Serve')) OD"
                + " 		ON SD.staff_Name = OD.chef_Name"
                + " 		WHERE staff_Category = 'Chef'"
                + " 		AND staff_Name = temp.staff_Name AND order_Status = temp.order_Status "
                + " 		FOR XML PATH('')"
                + " 	),1,1,'') , 'Not Assigned') AS order_Id"
                + " FROM ("
                + " 	SELECT DISTINCT staff_Name, ISNULL(order_Status, ' N/A') AS order_Status"
                + " 	FROM [ADMINISTRATOR].[Staff_Details] SD LEFT JOIN ("
                + " 	SELECT DISTINCT chef_name, order_Status, order_Id "
                + " 	FROM [CUSTOMER].[OrderDetails]"
                + " 	WHERE order_Status IN ('Accepted', 'Ready to Serve')) OD"
                + " 	ON SD.staff_Name = OD.chef_Name"
                + " 	WHERE staff_Category = 'Chef'"
                + " ) AS temp;";
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    Map<String, String> dtname = new HashMap<String, String>();
                    dtname.put("staff_Name", resultSet.getString("staff_Name"));
                    dtname.put("order_Status", resultSet.getString("order_Status"));
                    dtname.put("order_Id" , resultSet.getString("order_Id"));
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