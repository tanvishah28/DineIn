package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Password_ChangeScreenActivity extends AppCompatActivity {

    String staff_member = "", staff_name = "", queryManager = "", queryChef = "", query = "";
    EditText editText_CurrentPwd, editText_NewPwd, editText_ConfirmPwd;
    Button buttonCancelPwd, buttonSavePwd;
    TextView textViewStaffName;
    static String StaffName = "", StaffCategory = "";

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_changescreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        StaffName = getIntent().getStringExtra("staff_name");
        StaffCategory = getIntent().getStringExtra("staff_category");

        textViewStaffName = (TextView) findViewById(R.id.textViewStaffName);
        textViewStaffName.setText("Hello " + StaffName);

        buttonCancelPwd = (Button) findViewById(R.id.buttonCancelPwd);
        buttonCancelPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StaffCategory.equals("Manager")) {
                    Intent intent = new Intent(Password_ChangeScreenActivity.this, Manager_MainScreenActivity.class);
                    intent.putExtra("staff_name", StaffName);
                    startActivity(intent);
                    finish();
                } else if (StaffCategory.equals("Chef")) {
                    Intent intent = new Intent(Password_ChangeScreenActivity.this, Chef_OrdersActivity.class);
                    intent.putExtra("staff_name", StaffName);
                    startActivity(intent);
                    finish();
                } else if (StaffCategory.equals("Admin")) {
                    Intent intent = new Intent(Password_ChangeScreenActivity.this, Admin_MainScreenActivity.class);
                    intent.putExtra("staff_name", StaffName);
                    startActivity(intent);
                    finish();
                }
            }
        });

        editText_CurrentPwd = (EditText) findViewById(R.id.editText_CurrentPwd);
        editText_NewPwd = (EditText) findViewById(R.id.editText_NewPwd);
        editText_ConfirmPwd = (EditText) findViewById(R.id.editText_ConfirmPwd);

        buttonSavePwd = (Button) findViewById(R.id.buttonSavePwd);
        buttonSavePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText_CurrentPwd.getText().toString().trim().equals("")
                        || editText_NewPwd.getText().toString().trim().equals("")
                        || editText_ConfirmPwd.getText().toString().trim().equals("")) {
                    Toast.makeText(Password_ChangeScreenActivity.this, "Please enter required details", Toast.LENGTH_SHORT).show();
                    editText_CurrentPwd.requestFocus();
                } else if (editText_CurrentPwd.getText().toString().trim().equals(editText_NewPwd.getText().toString().trim())) {
                    Toast.makeText(Password_ChangeScreenActivity.this, "New password cannot be same as old password", Toast.LENGTH_SHORT).show();
                    editText_NewPwd.setText("");
                    editText_ConfirmPwd.setText("");
                    editText_NewPwd.requestFocus();
                } else if (!editText_NewPwd.getText().toString().trim().equals(editText_ConfirmPwd.getText().toString().trim())) {
                    Toast.makeText(Password_ChangeScreenActivity.this, "Password does not match", Toast.LENGTH_SHORT).show();
                    editText_NewPwd.setText("");
                    editText_ConfirmPwd.setText("");
                    editText_NewPwd.requestFocus();
                } else {
                    try {
                        if (connect != null) {
                            query = " UPDATE [ADMINISTRATOR].[Staff_Details] SET staff_Password = '" + editText_ConfirmPwd.getText().toString()
                                    + "' ,date_updated = GETDATE() "
                                    + "  WHERE staff_Category = '" + StaffCategory
                                    + "' and staff_Name ='" + StaffName + "';";
                            Statement statement = connect.createStatement();
                            statement.executeUpdate(query);
                            ConnectionResult = "Success";
                            isSuccess = true;
                            Toast.makeText(Password_ChangeScreenActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            if (StaffCategory.equals("Manager")) {
                                Intent intent = new Intent(Password_ChangeScreenActivity.this, Manager_MainScreenActivity.class);
                                intent.putExtra("staff_name", StaffName);
                                startActivity(intent);
                                finish();
                            } else if (StaffCategory.equals("Chef")) {
                                Intent intent = new Intent(Password_ChangeScreenActivity.this, Chef_OrdersActivity.class);
                                intent.putExtra("staff_name", StaffName);
                                startActivity(intent);
                                finish();
                            } else if (StaffCategory.equals("Admin")) {
                                Intent intent = new Intent(Password_ChangeScreenActivity.this, Admin_MainScreenActivity.class);
                                intent.putExtra("staff_name", StaffName);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            ConnectionResult = "Failed";
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        });
    }
}