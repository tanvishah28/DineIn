package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Admin_Table_UpdateDeleteActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;
    String query = "", selectedItem = "", selectedTableId = "";
    String table_Status = "", no_of_People_Allowed_Max = "", no_of_People_Allowed_Min= "";
    TextView textView_TableId;
    EditText editNoOfPeopleMin,editNoOfPeopleMax;
    Button buttonUpdateTable, buttonDeleteTable;
    String noOfpeopleMin, noOfpeopleMax, tableStatus;
    Spinner spinner_TableStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_updatedelete_tablescreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = getIntent();
        selectedItem = intent.getStringExtra("selectedItem");

        String[] arrOfStr = selectedItem.split(",", 6);
        selectedTableId = arrOfStr[0].replace("{table_Id=","").trim();

        textView_TableId = findViewById(R.id.textView_TableId);
        editNoOfPeopleMin = findViewById(R.id.editTextMin);
        editNoOfPeopleMax = findViewById(R.id.editTextMax);
        buttonUpdateTable = findViewById(R.id.buttonUpdateTable);
        buttonDeleteTable = findViewById(R.id.buttonDeleteTable);

        textView_TableId.setText("Table #" + selectedTableId + " Details");

        query = "SELECT table_Id, no_of_People_Allowed_Min, no_of_People_Allowed_Max, table_Status "
                + " FROM [ADMINISTRATOR].[Tables] WHERE table_Id = '" + selectedTableId + "';";
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    no_of_People_Allowed_Min = resultSet.getString("no_of_People_Allowed_Min");
                    no_of_People_Allowed_Max = resultSet.getString("no_of_People_Allowed_Max");
                    table_Status = resultSet.getString("table_Status");
                }
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        editNoOfPeopleMin.setText("" + no_of_People_Allowed_Min);
        editNoOfPeopleMax.setText("" + no_of_People_Allowed_Max);

        spinner_TableStatus = (Spinner) findViewById(R.id.spinner_TableStatus);
        fill_spinner_TableStatus();

        buttonUpdateTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editNoOfPeopleMin.getText().toString().trim().equals("") || editNoOfPeopleMax.getText().toString().trim().equals("")) {
                    Toast.makeText(Admin_Table_UpdateDeleteActivity.this, "Enter required detail", Toast.LENGTH_SHORT).show();
                }
                else if((editNoOfPeopleMin.getText().toString().equals(no_of_People_Allowed_Min.toString())) &&
                        (editNoOfPeopleMax.getText().toString().equals(no_of_People_Allowed_Max.toString())) &&
                        (spinner_TableStatus.getSelectedItem().toString().equals(table_Status.toString()))) {
                    Toast.makeText(Admin_Table_UpdateDeleteActivity.this,
                            "Modify required data", Toast.LENGTH_SHORT).show();
                }
                else if(editNoOfPeopleMin.getText().toString().trim().equals("0")
                        || editNoOfPeopleMin.getText().toString().trim().equals("")
                        || editNoOfPeopleMax.getText().toString().trim().equals("0")
                        || editNoOfPeopleMax.getText().toString().trim().equals("")
                        || editNoOfPeopleMin.getText().toString().trim().length() == 0
                        || editNoOfPeopleMax.getText().toString().trim().length() == 0 ) {
                    Toast.makeText(Admin_Table_UpdateDeleteActivity.this,"Capacity should be at-least one",Toast.LENGTH_SHORT).show();
                    editNoOfPeopleMin.requestFocus();
                }
                else if (Integer.parseInt(editNoOfPeopleMin.getText().toString()) >= Integer.parseInt(editNoOfPeopleMax.getText().toString())) {
                    Toast.makeText(Admin_Table_UpdateDeleteActivity.this,"Invalid Capacity",Toast.LENGTH_SHORT).show();
                    editNoOfPeopleMin.requestFocus();
                }
                else {
                    try {
                        if(connect!=null) {
                            query = "UPDATE [ADMINISTRATOR].[Tables] "
                                    + " SET no_of_People_Allowed_Min = '" + editNoOfPeopleMin.getText().toString()
                                    + "', no_of_People_Allowed_Max = '" + editNoOfPeopleMax.getText().toString()
                                    + "', table_Status = '" + spinner_TableStatus.getSelectedItem().toString()
                                    + "' WHERE table_Id = '" + selectedTableId + "';";
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(query);
                            ConnectionResult="Success";
                            Toast.makeText(getApplicationContext(),"Table Updated Successfully",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(Admin_Table_UpdateDeleteActivity.this,Admin_TableScreenActivity.class);
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

        buttonDeleteTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(connect!=null) {
                        query = "DELETE FROM [ADMINISTRATOR].[Tables] WHERE "
                                + " table_Id = '" + selectedTableId + "';";
                        Statement statement = connect.createStatement();
                        statement.executeUpdate(query);
                        ConnectionResult="Success";
                        Toast.makeText(getApplicationContext(),"Table Deleted Successfully",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Admin_Table_UpdateDeleteActivity.this,Admin_TableScreenActivity.class);
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
        });
    }

    public void fill_spinner_TableStatus() {
        ArrayList<String> data = new ArrayList<String>();
        data.add("Available");
        data.add("Engaged");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner_TableStatus.setAdapter(arrayAdapter);
    }
}