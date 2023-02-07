package com.example.restaurant_dinein;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.io.Console;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    /* Database Connection */
    Connection connection = null;
    String connectionURL = "";
    String ip="", port="", databasename="", username="", password="", error="";

    @SuppressLint("NewApi")
    public Connection connectionClass () {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ip = "192.168.1.105";
        //ip = "192.168.73.72";
        port = "1433";
        databasename = "DineIn";
        username = "sa";
        password = "sqladmin";

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + databasename;
            connection = DriverManager.getConnection(connectionURL, username, password);
        }
        catch (Exception ex) {
            Log.e("Error:: ", ex.getMessage());
            System.out.println("Error: " + ex);
        }
        return connection;
    }
}
