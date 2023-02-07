package com.example.restaurant_dinein;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class Admin_Table_AddActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;
    String query = "";
    EditText editNoOfPeopleMin,editNoOfPeopleMax;
    Button btnAvailable;
    Button btnEngaged;
    String noOfpeopleMin;
    String noOfpeopleMax;
    String tableStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_tablescreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        editNoOfPeopleMin = findViewById(R.id.editTextMin);
        editNoOfPeopleMax = findViewById(R.id.editTextMax);
        btnAvailable = findViewById(R.id.buttonAvailable);
        btnEngaged = findViewById(R.id.buttonEngaged);

        btnAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noOfpeopleMin = editNoOfPeopleMin.getText().toString();
                noOfpeopleMax = editNoOfPeopleMax.getText().toString();
                tableStatus = "Available";
                if(noOfpeopleMin.trim().equals("0") || noOfpeopleMin.trim().equals("")
                        || noOfpeopleMax.trim().equals("0") || noOfpeopleMax.equals("")
                        || noOfpeopleMin.trim().length() == 0 || noOfpeopleMax.trim().length() == 0 ) {
                    Toast.makeText(getApplicationContext(),"Capacity should be at-least one",Toast.LENGTH_SHORT).show();
                    editNoOfPeopleMin.setText("");
                    editNoOfPeopleMax.setText("");
                    editNoOfPeopleMin.requestFocus();
                }
                else if (Integer.parseInt(noOfpeopleMin) >= Integer.parseInt(noOfpeopleMax)) {
                    Toast.makeText(getApplicationContext(),"Invalid Capacity",Toast.LENGTH_SHORT).show();
                    editNoOfPeopleMin.setText("");
                    editNoOfPeopleMax.setText("");
                    editNoOfPeopleMin.requestFocus();
                }
                else {
                    try {
                        if(connect!=null) {
                            query = "INSERT INTO [ADMINISTRATOR].[Tables] (no_of_People_Allowed_Min,no_of_People_Allowed_Max,table_Status) VALUES "
                                    + "('" + noOfpeopleMin + "','" + noOfpeopleMax + "','" + tableStatus + "');";
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(query);
                            ConnectionResult="Success";
                            Toast.makeText(getApplicationContext(),"Table Added Successfully",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Admin_Table_AddActivity.this,Admin_TableScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            ConnectionResult="Fail";
                        }
                    }
                    catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });

        btnEngaged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noOfpeopleMin = editNoOfPeopleMin.getText().toString();
                noOfpeopleMax = editNoOfPeopleMax.getText().toString();
                tableStatus = "Engaged";
                if(noOfpeopleMin.trim().equals("0") || noOfpeopleMin.trim().equals("")
                        || noOfpeopleMax.trim().equals("0") || noOfpeopleMax.equals("")
                        || noOfpeopleMin.trim().length() == 0 || noOfpeopleMax.trim().length() == 0 ) {
                    Toast.makeText(getApplicationContext(),"Capacity should be at-least one",Toast.LENGTH_SHORT).show();
                    editNoOfPeopleMin.setText("");
                    editNoOfPeopleMax.setText("");
                    editNoOfPeopleMin.requestFocus();
                }
                else if (Integer.parseInt(noOfpeopleMin) >= Integer.parseInt(noOfpeopleMax)) {
                    Toast.makeText(getApplicationContext(),"Invalid Capacity",Toast.LENGTH_SHORT).show();
                    editNoOfPeopleMin.setText("");
                    editNoOfPeopleMax.setText("");
                    editNoOfPeopleMin.requestFocus();
                }
                else {
                    try {
                        if(connect!=null) {
                            query = "INSERT INTO [ADMINISTRATOR].[Tables] (no_of_People_Allowed_Min,no_of_People_Allowed_Max,table_Status) VALUES "
                                    + "('" + noOfpeopleMin + "','" + noOfpeopleMax + "','" + tableStatus + "');";
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(query);
                            ConnectionResult="Success";
                            Toast.makeText(getApplicationContext(),"Table Added Successfully",Toast.LENGTH_SHORT).show();
							Intent intent=new Intent(Admin_Table_AddActivity.this,Admin_TableScreenActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            ConnectionResult="Fail";
                        }
                    }
                    catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }
}
