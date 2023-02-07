package com.example.restaurant_dinein;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Adapter;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Admin_MenuItem_UpdateDeleteActivity extends AppCompatActivity {
    String selectedItem="", selectedItemName="", selectedItemPrice="", selectedItemCategory="",
            selectedItemAvailable="", switchStatus="", query="", itemCategorySelectedName="", switchStatus_update="";
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;

    EditText editText_ItemName, editText_ItemPrice;
    Spinner spinner_ItemCategory;
    Switch switch_menuitem_available;
    ImageView image_FoodItemIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_updatedelete_menuitemscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        editText_ItemName = findViewById(R.id.editText_ItemName);
        editText_ItemPrice = findViewById(R.id.editText_ItemPrice);
        spinner_ItemCategory = findViewById(R.id.spinner_ItemCategory);
        switch_menuitem_available = findViewById(R.id.switch_menuitem_available);

        Intent intent = getIntent();
        selectedItem = intent.getStringExtra("selectedItem");

        String[] arrOfStr = selectedItem.split(",", 6);
        selectedItemName = arrOfStr[0].trim();
        selectedItemPrice = arrOfStr[1].replace("₹", "").trim();
        selectedItemCategory = arrOfStr[2].trim();
        selectedItemAvailable = arrOfStr[3].trim();

        editText_ItemName = (EditText) findViewById(R.id.editText_ItemName);
        editText_ItemName.setText("" + selectedItemName);

        editText_ItemPrice = (EditText) findViewById(R.id.editText_ItemPrice);
        editText_ItemPrice.setText("" + selectedItemPrice);

        switch_menuitem_available = (Switch) findViewById(R.id.switch_menuitem_available);
        if(selectedItemAvailable.toString().equals("Available")) {
            switch_menuitem_available.setChecked(true);
        }
        else {
            switch_menuitem_available.setChecked(false);
        }

        image_FoodItemIcon = (ImageView) findViewById(R.id.image_FoodItemIcon);
        displayFoodItemImage(selectedItemName, selectedItemPrice, selectedItemCategory, selectedItemAvailable);

        spinner_ItemCategory = (Spinner) findViewById(R.id.spinner_ItemCategory);
        fillSpinner(selectedItemCategory);

        spinner_ItemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemCategorySelectedName = spinner_ItemCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button buttonUpdateItem = findViewById(R.id.buttonUpdateItem);
        buttonUpdateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switch_menuitem_available.isChecked()) {
                    switchStatus_update = switch_menuitem_available.getTextOn().toString();
                }
                else {
                    switchStatus_update = switch_menuitem_available.getTextOff().toString();
                }

                if((editText_ItemName.getText().toString().equals(selectedItemName.toString())) &&
                        (editText_ItemPrice.getText().toString().equals(selectedItemPrice.toString())) &&
                        (spinner_ItemCategory.getSelectedItem().toString().equals(selectedItemCategory.toString())) &&
                        (switchStatus_update.toString().equals(selectedItemAvailable.toString()))) {
                    Toast.makeText(Admin_MenuItem_UpdateDeleteActivity.this,
                            "Modify required data", Toast.LENGTH_SHORT).show();
                }
                else if(editText_ItemName.getText().toString().trim().equals("")
                        || editText_ItemPrice.getText().toString().trim().equals("")
                        || editText_ItemPrice.getText().toString().trim().length() == 0) {
                    Toast.makeText(Admin_MenuItem_UpdateDeleteActivity.this, "Enter required details", Toast.LENGTH_SHORT).show();
                }
                else if(editText_ItemName.getText().toString().contains("0") || editText_ItemName.getText().toString().contains("1")
                        || editText_ItemName.getText().toString().contains("2") || editText_ItemName.getText().toString().contains("3")
                        || editText_ItemName.getText().toString().contains("4") || editText_ItemName.getText().toString().contains("5")
                        || editText_ItemName.getText().toString().contains("6") || editText_ItemName.getText().toString().contains("7")
                        || editText_ItemName.getText().toString().contains("8") || editText_ItemName.getText().toString().contains("9")
                        || editText_ItemName.getText().toString().contains("$") || editText_ItemName.getText().toString().contains("*")
                        || editText_ItemName.getText().toString().contains("#") || editText_ItemName.getText().toString().contains("@")
                        || editText_ItemName.getText().toString().contains("^") || editText_ItemName.getText().toString().contains("%")
                        || editText_ItemName.getText().toString().contains("!") || editText_ItemName.getText().toString().contains("+")) {
                    Toast.makeText(Admin_MenuItem_UpdateDeleteActivity.this, "Invalid Item Name", Toast.LENGTH_SHORT).show();
                }
                else if(editText_ItemPrice.getText().toString().trim().equals("0")) {
                    Toast.makeText(Admin_MenuItem_UpdateDeleteActivity.this, "Invalid Item Price", Toast.LENGTH_SHORT).show();
                }
                else {
                    query = "UPDATE [ADMINISTRATOR].[FoodItems] SET foodItem_Name = '" + editText_ItemName.getText().toString()
                            + "', foodItem_Price = '" + editText_ItemPrice.getText().toString()
                            + "', foodItem_Category = '" + spinner_ItemCategory.getSelectedItem().toString()
                            + "', foodItem_Status = '" + switchStatus_update.toString()
                            + "', date_updated = GETDATE()"
                            + " WHERE foodItem_Name = '" + selectedItemName
                            + "' AND foodItem_Category = '" + selectedItemCategory
                            + "' AND foodItem_Price = '" + selectedItemPrice
                            + "';";
                    executeQuery(query);
                }
            }
        });

        Button buttonDeleteItem = findViewById(R.id.buttonDeleteItem);
        buttonDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = "DELETE FROM [ADMINISTRATOR].[FoodItems] "
                        + " WHERE foodItem_Name = '" + selectedItemName
                        + "' AND foodItem_Category = '" + selectedItemCategory
                        + "' AND foodItem_Price = '" + selectedItemPrice
                        + "';";
                executeQuery(query);
            }
        });
    }

    public void fillSpinner(String selectedItemCategory) {
        String query="";
        try {
            if (connect != null) {
                query = "SELECT DISTINCT foodItemCategory_Name FROM [ADMINISTRATOR].[FoodItem_Categories] WHERE foodItemCategory_IsDeleted = '0';";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                while (resultSet.next()) {
                    String id = resultSet.getString("foodItemCategory_Name");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_ItemCategory.setAdapter(arrayAdapter);

                for (int i = 1; i <= arrayAdapter.getCount(); i++) {
                    if (spinner_ItemCategory.getItemAtPosition(i).equals(selectedItemCategory)) {
                        spinner_ItemCategory.setSelection(i);
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
            Toast.makeText(Admin_MenuItem_UpdateDeleteActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayFoodItemImage(String selectedItemName, String selectedItemPrice, String selectedItemCategory, String selectedItemAvailable) {
        String query = "";
        try {
            if(connect != null) {
                query = "SELECT foodItem_Image FROM [ADMINISTRATOR].[FoodItems] "
                        + "WHERE foodItem_Name = '" + selectedItemName + "' AND foodItem_Price = '" + selectedItemPrice
                        + "' AND foodItem_Status = '" + selectedItemAvailable + "' AND foodItem_Category = '" + selectedItemCategory + "';";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while(resultSet.next()) {
                    String image = resultSet.getString("foodItem_Image");
                    byte[] bytes = Base64.decode(image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    image_FoodItemIcon.setImageBitmap(bitmap);
                }
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void executeQuery(String query_UpdateDelete) {
        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                statement.executeUpdate(query);
                ConnectionResult="Success";
                isSuccess=true;
                Intent intent=new Intent(Admin_MenuItem_UpdateDeleteActivity.this,Admin_MenuItemScreenActivity.class);
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