package com.example.restaurant_dinein;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.restaurant_dinein.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Admin_MenuItem_AddActivity extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 1;

    Connection connect;
    String ConnectionResult = "", itemCategorySelectedName="";
    Boolean isSuccess = false;

    EditText editText_ItemName, editText_ItemPrice;
    Spinner spinner_ItemCategory;
    ImageView upload_MenuItemimage;
    Switch switch_menuitem_available;
    Button buttonAddItem;

    byte[] byteArray;
    String encodedImage="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_menuitemscreen);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        editText_ItemName = findViewById(R.id.editText_ItemName);
        editText_ItemPrice = findViewById(R.id.editText_ItemPrice);
        spinner_ItemCategory = findViewById(R.id.spinner_ItemCategory);

        upload_MenuItemimage = findViewById(R.id.upload_MenuItemimage);
        switch_menuitem_available = findViewById(R.id.switch_menuitem_available);
        buttonAddItem = findViewById(R.id.buttonAddItem);

        fillSpinner();

        spinner_ItemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemCategorySelectedName = spinner_ItemCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        upload_MenuItemimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadData();
            }
        });
    }

    public void fillSpinner() {
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
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Admin_MenuItem_AddActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void UploadData() {
        String query="", switchStatus="";

        if(editText_ItemName.getText().toString().trim().equals("")
                || editText_ItemPrice.getText().toString().trim().equals("")
                || editText_ItemPrice.getText().toString().trim().length() == 0) {
            Toast.makeText(Admin_MenuItem_AddActivity.this, "Enter required details", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(Admin_MenuItem_AddActivity.this, "Invalid Item Name", Toast.LENGTH_SHORT).show();
        }
        else if(encodedImage.trim().equals("")) {
            Toast.makeText(Admin_MenuItem_AddActivity.this, "Choose Image", Toast.LENGTH_SHORT).show();
        }
        else if(editText_ItemPrice.getText().toString().trim().equals("0")) {
            Toast.makeText(Admin_MenuItem_AddActivity.this, "Invalid Item Price", Toast.LENGTH_SHORT).show();
        }
        else {
            if(switch_menuitem_available.isChecked()) {
                switchStatus = switch_menuitem_available.getTextOn().toString();
            }
            else {
                switchStatus = switch_menuitem_available.getTextOff().toString();
            }
            try {
                if (connect != null) {
                    query = "INSERT INTO [ADMINISTRATOR].[FoodItems] (foodItem_Category, foodItem_Name, foodItem_Price, foodItem_Status, foodItem_Image) "
                            + "VALUES ('" + itemCategorySelectedName.toString() + "', '" + editText_ItemName.getText().toString() + "', '" + editText_ItemPrice.getText().toString()
                            + "', '" + switchStatus.toString() + "', '" + encodedImage + "');";
                    PreparedStatement preStmt = connect.prepareStatement(query);
                    preStmt.executeUpdate();
                    ConnectionResult="Success";
                    isSuccess=true;
                    Toast.makeText(Admin_MenuItem_AddActivity.this, "Menu Item added Successfully", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(Admin_MenuItem_AddActivity.this,Admin_MenuItemScreenActivity.class);
                    startActivity(intent);
                }
                else {
                    ConnectionResult="Failed";
                }
            } catch(Exception ex) {
                Toast.makeText(Admin_MenuItem_AddActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void chooseImage() {
        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_GALLERY);
        } catch(Exception e) {
            Toast.makeText(Admin_MenuItem_AddActivity.this, "No activity found to perform this task", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == PICK_FROM_GALLERY || null!=data) {
            Bitmap originBitmap = null;
            Uri selectedImage = data.getData();
            //Toast.makeText(Admin_MenuItem_AddActivity.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
            InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
                originBitmap = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                Toast.makeText(Admin_MenuItem_AddActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
            if (originBitmap != null) {
                this.upload_MenuItemimage.setImageBitmap(originBitmap);
                Bitmap bitmap_image = ((BitmapDrawable) upload_MenuItemimage.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap_image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                encodedImage = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
                Toast.makeText(Admin_MenuItem_AddActivity.this, "Conversion Done", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Admin_MenuItem_AddActivity.this, "There is an image conversion error", Toast.LENGTH_SHORT).show();
        }
    }
}