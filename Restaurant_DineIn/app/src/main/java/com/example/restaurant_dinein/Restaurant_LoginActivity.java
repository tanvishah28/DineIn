package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class Restaurant_LoginActivity extends AppCompatActivity {
    String staff_member = "", query = "", staff_name = "";
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;

    TextView inputMobile;
    TextView inputPassword;
    TextView textview_no_of_attempt;

    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_loginscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = getIntent();
        staff_member = intent.getStringExtra("staff_member");

        TextView textview_staff_member = (TextView) findViewById(R.id.staff_member);
        textview_staff_member.setText(staff_member + ", Sign In");

        inputMobile = (TextView)findViewById(R.id.inputMobile);
        inputPassword = (TextView)findViewById(R.id.inputPassword);
        textview_no_of_attempt = (TextView)findViewById(R.id.textview_no_of_attempt);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputMobile.getText().toString().trim().equals("")
                        || inputPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter required details", Toast.LENGTH_SHORT).show();
                }
                else if(inputMobile.getText().toString().startsWith("0") || inputMobile.getText().toString().startsWith("1")
                        || inputMobile.getText().toString().startsWith("2") || inputMobile.getText().toString().startsWith("3")
                        || inputMobile.getText().toString().startsWith("4") || inputMobile.getText().toString().startsWith("5")
                        || inputMobile.getText().toString().startsWith("6") || inputMobile.getText().toString().trim().length()<10) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                    inputMobile.setText("");
                    inputPassword.setText("");
                    counter--;
                    textview_no_of_attempt.setText("No. of attempt Left : " + counter);
                    inputPassword.setText("");
                }
                else {
                    try {
                        if (connect != null) {
                            query = "SELECT staff_Name FROM [ADMINISTRATOR].[Staff_Details] WHERE staff_Category = '" + staff_member
                                    + "' AND staff_MobileNo = '" + inputMobile.getText().toString()
                                    + "' AND staff_Password = '" + inputPassword.getText().toString() + "';";
                            Statement statement = connect.createStatement();
                            ResultSet resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                staff_name = resultSet.getString("staff_Name");
                                if (staff_member.equals("Admin")) {
                                    Intent intent = new Intent(Restaurant_LoginActivity.this, Admin_MainScreenActivity.class);
                                    intent.putExtra("staff_name", staff_name);
                                    startActivity(intent);
                                    connect.close();
                                    finish();
                                } else if (staff_member.equals("Manager")) {
                                    Intent intent = new Intent(Restaurant_LoginActivity.this, Manager_MainScreenActivity.class);
                                    intent.putExtra("staff_name", staff_name);
                                    startActivity(intent);
                                    connect.close();
                                    finish();
                                } else if (staff_member.equals("Chef")) {
                                    Intent intent = new Intent(Restaurant_LoginActivity.this, Chef_OrdersActivity.class);
                                    intent.putExtra("staff_name", staff_name);
                                    startActivity(intent);
                                    connect.close();
                                    finish();
                                }
                                ConnectionResult = "Success";
                                isSuccess = true;
                            } else {
                                counter--;
                                textview_no_of_attempt.setText("No. of attempt Left : " + counter);
                                inputPassword.setText("");
                                Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            ConnectionResult = "Failed";
                            Toast.makeText(getApplicationContext(), "Connection Issue", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                if (counter == 0) {
                    Intent intent = new Intent(Restaurant_LoginActivity.this, Restaurant_WelcomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}