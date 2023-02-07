package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Admin_Staff_AddActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult = "", staffCategorySelectedName = "";
    Boolean isSuccess = false;

    EditText editText_StaffName, editText_MobileNo, editText_Password ;
    Spinner spinner_StaffCategory;
    Button buttonAddStaff;

    byte[] byteArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_staffmember);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        spinner_StaffCategory = findViewById(R.id.spinner_StaffCategory);
        editText_StaffName = findViewById(R.id.editText_StaffName);
        editText_MobileNo = findViewById(R.id.editText_MobileNo);
        editText_Password = findViewById(R.id.editText_Password);
        buttonAddStaff = findViewById(R.id.buttonAddStaff);

        fillSpinner();

        spinner_StaffCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                staffCategorySelectedName = spinner_StaffCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buttonAddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "";
                if(editText_StaffName.getText().toString().trim().equals("")
                        || editText_MobileNo.getText().toString().trim().equals("")
                        || editText_Password.getText().toString().trim().equals("")) {
                    Toast.makeText(Admin_Staff_AddActivity.this, "Enter required details", Toast.LENGTH_SHORT).show();
                } else if(editText_StaffName.getText().toString().contains("0") || editText_StaffName.getText().toString().contains("1")
                        || editText_StaffName.getText().toString().contains("2") || editText_StaffName.getText().toString().contains("3")
                        || editText_StaffName.getText().toString().contains("4") || editText_StaffName.getText().toString().contains("5")
                        || editText_StaffName.getText().toString().contains("6") || editText_StaffName.getText().toString().contains("7")
                        || editText_StaffName.getText().toString().contains("8") || editText_StaffName.getText().toString().contains("9")
                        || editText_StaffName.getText().toString().contains("$") || editText_StaffName.getText().toString().contains("*")
                        || editText_StaffName.getText().toString().contains("#") || editText_StaffName.getText().toString().contains("@")
                        || editText_StaffName.getText().toString().contains("^") || editText_StaffName.getText().toString().contains("%")
                        || editText_StaffName.getText().toString().contains("!") || editText_StaffName.getText().toString().contains("+")) {
                    Toast.makeText(Admin_Staff_AddActivity.this, "Invalid Staff Name", Toast.LENGTH_SHORT).show();
                } else if(editText_MobileNo.getText().toString().startsWith("0") || editText_MobileNo.getText().toString().startsWith("1")
                        || editText_MobileNo.getText().toString().startsWith("2") || editText_MobileNo.getText().toString().startsWith("3")
                        || editText_MobileNo.getText().toString().startsWith("4") || editText_MobileNo.getText().toString().startsWith("5")
                        || editText_MobileNo.getText().toString().startsWith("6") || editText_MobileNo.getText().toString().trim().length()<10) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                    editText_MobileNo.setText("");
                } else {
                    try {
                        if (connect != null) {
                            query = "INSERT INTO [ADMINISTRATOR].[Staff_Details] (staff_Category, staff_Name, staff_MobileNo, staff_Password) "
                                    + "VALUES ('" + staffCategorySelectedName.toString() + "', '" + editText_StaffName.getText().toString() + "', '"
                                    + editText_MobileNo.getText().toString() + "', '" + editText_Password.getText().toString() + "');";
                            PreparedStatement preStmt = connect.prepareStatement(query);
                            preStmt.executeUpdate();
                            ConnectionResult = "Success";
                            isSuccess = true;
                            Toast.makeText(Admin_Staff_AddActivity.this, "Staff Member added Successfully", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Admin_Staff_AddActivity.this, Admin_StaffScreenActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            ConnectionResult = "Failed";
                        }
                    } catch (Exception ex) {
                        Toast.makeText(Admin_Staff_AddActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            });
        }

    public void fillSpinner() {
        String query = "";
        try {
            if (connect != null) {
                query = "SELECT DISTINCT staffDetailsCategory_Name FROM [ADMINISTRATOR].[StaffDetails_Categories];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while (resultSet.next()) {
                    String id = resultSet.getString("staffDetailsCategory_Name");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_StaffCategory.setAdapter(arrayAdapter);
                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (Exception ex) {
            Toast.makeText(Admin_Staff_AddActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
}