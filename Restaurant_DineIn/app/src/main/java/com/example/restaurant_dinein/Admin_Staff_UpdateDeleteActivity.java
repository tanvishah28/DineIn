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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Admin_Staff_UpdateDeleteActivity extends AppCompatActivity {
    String selectedStaff="", selectedStaffName="", selectedMobileNo="", selectedPassword="",
            selectedStaffCategory="", staffCategorySelectedName="" ,query="";
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;

    EditText editText_StaffName, editText_MobileNo, editText_Password ;
    Spinner spinner_StaffCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_updatedelete_staffmember);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        spinner_StaffCategory = findViewById(R.id.spinner_StaffCategory);
        editText_StaffName = findViewById(R.id.editText_StaffName);
        editText_MobileNo = findViewById(R.id.editText_MobileNo);
        editText_Password = findViewById(R.id.editText_Password);

        Intent intent = getIntent();
        selectedStaff = intent.getStringExtra("selectedStaff");

        String[] arrOfStr = selectedStaff.replace("{", "").replace("}", "").split(",",6);
        selectedStaffCategory = arrOfStr[2].replace("staff_Category=", "").trim();
        selectedStaffName = arrOfStr[0].replace("staff_Name=", "").trim();
        selectedMobileNo = arrOfStr[3].replace("staff_MobileNo=","").replace("+91-", "").trim();
        selectedPassword = arrOfStr[1].replace("staff_Password=","").trim();

        editText_StaffName = (EditText) findViewById(R.id.editText_StaffName);
        editText_StaffName.setText("" + selectedStaffName);

        editText_MobileNo = (EditText) findViewById(R.id.editText_MobileNo);
        editText_MobileNo.setText("" + selectedMobileNo);

        editText_Password = (EditText) findViewById(R.id.editText_Password);
        editText_Password.setText("" + selectedPassword);

        spinner_StaffCategory = (Spinner) findViewById(R.id.spinner_StaffCategory);
        fillSpinner(selectedStaffCategory);

        spinner_StaffCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
                staffCategorySelectedName = spinner_StaffCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button buttonUpdateCategoryName = findViewById(R.id.buttonUpdateStaff);
        buttonUpdateCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((editText_StaffName.getText().toString().equals(selectedStaffName.toString())) &&
                        (editText_MobileNo.getText().toString().equals(selectedMobileNo.toString())) &&
                        (editText_Password.getText().toString().equals(selectedPassword.toString())) &&
                        (spinner_StaffCategory.getSelectedItem().toString().equals(selectedStaffCategory.toString()))) {
                    Toast.makeText(Admin_Staff_UpdateDeleteActivity.this,
                            "Modify the staff details", Toast.LENGTH_SHORT).show();
                } else if(editText_StaffName.getText().toString().trim().equals("")
                        || editText_MobileNo.getText().toString().trim().equals("")
                        || editText_Password.getText().toString().trim().equals("")) {
                    Toast.makeText(Admin_Staff_UpdateDeleteActivity.this, "Enter required details", Toast.LENGTH_SHORT).show();
                } else if(editText_StaffName.getText().toString().contains("0") || editText_StaffName.getText().toString().contains("1")
                        || editText_StaffName.getText().toString().contains("2") || editText_StaffName.getText().toString().contains("3")
                        || editText_StaffName.getText().toString().contains("4") || editText_StaffName.getText().toString().contains("5")
                        || editText_StaffName.getText().toString().contains("6") || editText_StaffName.getText().toString().contains("7")
                        || editText_StaffName.getText().toString().contains("8") || editText_StaffName.getText().toString().contains("9")
                        || editText_StaffName.getText().toString().contains("$") || editText_StaffName.getText().toString().contains("*")
                        || editText_StaffName.getText().toString().contains("#") || editText_StaffName.getText().toString().contains("@")
                        || editText_StaffName.getText().toString().contains("^") || editText_StaffName.getText().toString().contains("%")
                        || editText_StaffName.getText().toString().contains("!") || editText_StaffName.getText().toString().contains("+")) {
                    Toast.makeText(Admin_Staff_UpdateDeleteActivity.this, "Invalid Staff Name", Toast.LENGTH_SHORT).show();
                } else if(editText_MobileNo.getText().toString().startsWith("0") || editText_MobileNo.getText().toString().startsWith("1")
                        || editText_MobileNo.getText().toString().startsWith("2") || editText_MobileNo.getText().toString().startsWith("3")
                        || editText_MobileNo.getText().toString().startsWith("4") || editText_MobileNo.getText().toString().startsWith("5")
                        || editText_MobileNo.getText().toString().startsWith("6") || editText_MobileNo.getText().toString().trim().length()<10) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                    editText_MobileNo.setText("");
                }                 else {
                    query = "UPDATE [ADMINISTRATOR].[Staff_Details] SET staff_Name = '" + editText_StaffName.getText().toString()
                            + "', staff_Category = '" + spinner_StaffCategory.getSelectedItem().toString()
                            + "', staff_MobileNo = '" + editText_MobileNo.getText().toString()
                            + "', staff_Password = '" + editText_Password.getText().toString()
                            + "' WHERE staff_Name = '" + selectedStaffName
                            + "' AND staff_Category = '" + selectedStaffCategory
                            + "' AND staff_MobileNo = '" + selectedMobileNo
                            + "' AND staff_Password = '" + selectedPassword
                            + "';";
                    executeQuery(query);
                }
            }
        });

        Button buttonDeleteStaff = findViewById(R.id.buttonDeleteStaff);
        buttonDeleteStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = "UPDATE [ADMINISTRATOR].[Staff_Details] SET staff_IsDeleted = '1'"
                        + " WHERE staff_Name = '" + selectedStaffName
                        + "' AND staff_Category = '" + selectedStaffCategory
                        + "' AND staff_MobileNo = '" + selectedMobileNo
                        + "' AND staff_Password = '" + selectedPassword
                        + "';";
                executeQuery(query);
            }
        });
    }

    public void fillSpinner(String selectedStaffCategory) {
        String query="";
        try {
            if (connect != null) {
                query = "SELECT DISTINCT staffDetailsCategory_Name FROM [ADMINISTRATOR].[StaffDetails_Categories] WHERE staffDetailsCategory_IsDeleted = '0';";
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

                for (int i = 0; i <= arrayAdapter.getCount(); i++) {
                    if (spinner_StaffCategory.getItemAtPosition(i).equals(selectedStaffCategory)) {
                        spinner_StaffCategory.setSelection(i);
                        break;
                    }
                }
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Admin_Staff_UpdateDeleteActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void executeQuery(String query_UpdateDelete) {
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                statement.executeUpdate(query);
                ConnectionResult="Success";
                isSuccess=true;
                Intent intent=new Intent(Admin_Staff_UpdateDeleteActivity.this,Admin_StaffScreenActivity.class);
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
}
