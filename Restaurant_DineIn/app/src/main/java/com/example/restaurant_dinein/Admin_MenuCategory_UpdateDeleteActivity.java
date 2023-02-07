package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Admin_MenuCategory_UpdateDeleteActivity extends AppCompatActivity {
    String selectedItemName="", query="";
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;

    EditText editText_CategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_updatedelete_menucategoryscreen);

        Intent intent = getIntent();
        selectedItemName = intent.getStringExtra("selectedItemName");

        editText_CategoryName = (EditText) findViewById(R.id.editText_CategoryName);
        editText_CategoryName.setText("" + selectedItemName);

        Button buttonUpdateCategoryName = findViewById(R.id.buttonUpdateCategoryName);
        buttonUpdateCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_CategoryName.getText().toString().trim().equals("")) {
                    Toast.makeText(Admin_MenuCategory_UpdateDeleteActivity.this, "Enter required detail", Toast.LENGTH_SHORT).show();
                }
                else if(editText_CategoryName.getText().toString().equals(selectedItemName.toString())) {
                    Toast.makeText(Admin_MenuCategory_UpdateDeleteActivity.this, "Modify the category name", Toast.LENGTH_SHORT).show();
                }
                else if(editText_CategoryName.getText().toString().contains("0") || editText_CategoryName.getText().toString().contains("1")
                        || editText_CategoryName.getText().toString().contains("2") || editText_CategoryName.getText().toString().contains("3")
                        || editText_CategoryName.getText().toString().contains("4") || editText_CategoryName.getText().toString().contains("5")
                        || editText_CategoryName.getText().toString().contains("6") || editText_CategoryName.getText().toString().contains("7")
                        || editText_CategoryName.getText().toString().contains("8") || editText_CategoryName.getText().toString().contains("9")
                        || editText_CategoryName.getText().toString().contains("$") || editText_CategoryName.getText().toString().contains("*")
                        || editText_CategoryName.getText().toString().contains("#") || editText_CategoryName.getText().toString().contains("@")
                        || editText_CategoryName.getText().toString().contains("^") || editText_CategoryName.getText().toString().contains("%")
                        || editText_CategoryName.getText().toString().contains("!") || editText_CategoryName.getText().toString().contains("+")) {
                    Toast.makeText(Admin_MenuCategory_UpdateDeleteActivity.this, "Invalid Category Name", Toast.LENGTH_SHORT).show();
                }
                else {
                    query = "UPDATE [ADMINISTRATOR].[FoodItem_Categories] SET foodItemCategory_Name = '" + editText_CategoryName.getText().toString()
                            + "', date_updated = GETDATE() WHERE foodItemCategory_Name = '" + selectedItemName + "';";
                    executeQuery(query);
                }
            }
        });

        Button buttonDeleteCategoryName = findViewById(R.id.buttonDeleteCategoryName);
        buttonDeleteCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = "UPDATE [ADMINISTRATOR].[FoodItem_Categories] SET foodItemCategory_IsDeleted = '1'"
                        + " WHERE foodItemCategory_Name = '" + selectedItemName + "';";
                executeQuery(query);
            }
        });
    }

    public void executeQuery(String query_UpdateDelete) {
        String query = query_UpdateDelete;
        try {
            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.connectionClass();
            if (connect != null) {
                Statement statement = connect.createStatement();
                statement.executeUpdate(query);
                ConnectionResult="Success";
                isSuccess=true;
                connect.close();
                Intent intent=new Intent(Admin_MenuCategory_UpdateDeleteActivity.this,Admin_MenuCategoryScreenActivity.class);
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