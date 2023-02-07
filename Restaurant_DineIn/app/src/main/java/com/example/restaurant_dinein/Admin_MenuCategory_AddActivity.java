package com.example.restaurant_dinein;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.restaurant_dinein.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Admin_MenuCategory_AddActivity extends AppCompatActivity {
    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;

    EditText editText_CategoryName;
    Button buttonAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_menucategoryscreen);

        editText_CategoryName = findViewById(R.id.editText_CategoryName);
        buttonAddCategory = findViewById(R.id.buttonAddCategory);

        buttonAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });
    }

    public void UploadData() {
        String query="";
        if(editText_CategoryName.getText().toString().trim().equals("")) {
            Toast.makeText(Admin_MenuCategory_AddActivity.this, "Enter required detail", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Admin_MenuCategory_AddActivity.this, "Invalid Category Name", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                ConnectionHelper connectionHelper = new ConnectionHelper();
                connect = connectionHelper.connectionClass();
                if (connect != null) {
                    query = "INSERT INTO [ADMINISTRATOR].[FoodItem_Categories] (foodItemCategory_Name) "
                            + "VALUES ('" + editText_CategoryName.getText().toString() + "');";
                    PreparedStatement preStmt = connect.prepareStatement(query);
                    preStmt.executeUpdate();
                    ConnectionResult="Success";
                    isSuccess=true;
                    connect.close();
                    Toast.makeText(Admin_MenuCategory_AddActivity.this, "Menu Category added Successfully", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(Admin_MenuCategory_AddActivity.this,Admin_MenuCategoryScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    ConnectionResult="Failed";
                }
            } catch(Exception ex) {
                Toast.makeText(Admin_MenuCategory_AddActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}