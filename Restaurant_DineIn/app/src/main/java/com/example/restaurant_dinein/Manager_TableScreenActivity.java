package com.example.restaurant_dinein;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.restaurant_dinein.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Manager_TableScreenActivity extends AppCompatActivity {
    SimpleAdapter simpleAdapter;
    ListView listView;
    Connection connect;
    String ConnectionResult="";
    Boolean isSuccess=false;
    String query = "", spinnerselectedTableStatus="";
    Spinner spinner_TableStatus;
    final Context context=this;
    String reserved_by = "", finalSelectedMobileNo="", finalSelectedTime="";

    static String ManagerName = "";
    Date currentTime, selectedTime, endTime;

    DrawerLayout drawerLayout;
    ImageView imageView_Add;
    TextView toolbar_Text;
    EditText inputMobile;

    final Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private int mYear, mMonth, mDay, mHour, mMinute;

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_view_table);

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        Intent intent = new Intent();
        ManagerName = getIntent().getStringExtra("staff_name");

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setChildInsets(R.layout.activity_manager_nav_main_drawer, true);
        imageView_Add = findViewById(R.id.ic_click_add);
        imageView_Add.setImageResource(R.drawable.ic_refresh);
        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Tables");

        listView = (ListView) findViewById(R.id.simpleListView);

        spinner_TableStatus = (Spinner) findViewById(R.id.spinner_TableStatus);
        fillSpinner();

        spinner_TableStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerselectedTableStatus = spinner_TableStatus.getSelectedItem().toString().trim();

                List<Map<String,String>> MyDataList = null;
                Manager_Table_List MyData = new Manager_Table_List();
                MyDataList = MyData.getList(spinnerselectedTableStatus);

                String[] Fromw = {"table_Id", "capacity", "table_Status", "future_booking"};
                int[] Tow = {R.id.textViewTableId, R.id.textViewNoOfPeople, R.id.textViewTableStatus, R.id.textViewFutureBooking};
                simpleAdapter = new SimpleAdapter(Manager_TableScreenActivity.this, MyDataList, R.layout.manager_table_listlayouttemplate, Fromw, Tow);
                listView.setAdapter(simpleAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //List<Map<String, String>> finalMyDataList = MyDataList;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();
                System.out.println("selectedItem table: " + selectedItem);
                String selectedTableNo="", selectedTableFutureBookingStatus="";

                String[] arrOfStr = selectedItem.replace("{","").replace("}","").split(",",8);
                selectedTableFutureBookingStatus = arrOfStr[1].replace("future_booking=","").trim();
                selectedTableNo = arrOfStr[0].replace("table_Id=Table No.: ","").trim();
                System.out.println("selectedItem table: " + selectedTableFutureBookingStatus);

                if(selectedTableFutureBookingStatus.equals("(*) No Future Booking")) {
                    String finalSelectedTableNo = selectedTableNo;
                    String finalSelectedTableStatus = selectedTableFutureBookingStatus;

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog_reserve_table);
                    TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
                    inputMobile = (EditText) dialog.findViewById(R.id.inputMobile);

                    Button btnTimePicker=(Button) dialog.findViewById(R.id.btn_time);
                    EditText txtTime=(EditText) dialog.findViewById(R.id.in_time);

                    btnTimePicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mHour = c.get(Calendar.HOUR_OF_DAY);
                            mMinute = c.get(Calendar.MINUTE);

                            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    finalSelectedTime = hourOfDay + ":" + minute;
                                    txtTime.setText("" + finalSelectedTime);
                                    }
                                }, mHour, mMinute, false);
                            timePickerDialog.show();
                        }
                    });

                    text_menuitem_dialog.setText("Do you want to reserve table no. " + finalSelectedTableNo + "?");
                    Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
                    button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View v) {
                            finalSelectedMobileNo = inputMobile.getText().toString();
                            String str_currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                            SimpleDateFormat timeParser = new SimpleDateFormat("HH:mm", Locale.US);
                            try {
                                currentTime = timeParser.parse(str_currentTime);
                                selectedTime = timeParser.parse(finalSelectedTime);
                                endTime = timeParser.parse("23:30");
                                System.out.println("time: " + currentTime + " - " + selectedTime + " - " + endTime);
                            } catch (ParseException e) {
                            }
                            
                            if(inputMobile.getText().toString().trim().equals("")) {
                                Toast.makeText(getApplicationContext(), "Enter required details", Toast.LENGTH_SHORT).show();
                            }
                            else if(inputMobile.getText().toString().startsWith("0") || inputMobile.getText().toString().startsWith("1")
                                    || inputMobile.getText().toString().startsWith("2") || inputMobile.getText().toString().startsWith("3")
                                    || inputMobile.getText().toString().startsWith("4") || inputMobile.getText().toString().startsWith("5")
                                    || inputMobile.getText().toString().startsWith("6") || inputMobile.getText().toString().trim().length()<10) {
                                Toast.makeText(getApplicationContext(), "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
                                inputMobile.setText("");
                            }
                            else if((currentTime.compareTo(selectedTime) == 0) || (selectedTime.before(currentTime))
                                        || selectedTime.after(endTime)) {
                                Toast.makeText(getApplicationContext(), "Select valid time", Toast.LENGTH_SHORT).show();
                                txtTime.setText("");
                            }
                            else {
                                updateData(finalSelectedTableNo, finalSelectedTableStatus, finalSelectedMobileNo, finalSelectedTime);
                                dialog.cancel();
                            }
                        }
                    });
                    Button button_menuitem_no = (Button) dialog.findViewById(R.id.button_menuitem_no);
                    button_menuitem_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.create();
                    dialog.show();
                } else {
                    String finalSelectedTableNo = selectedTableNo;
                    String finalSelectedTableStatus = "";
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog);
                    TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
                    text_menuitem_dialog.setText("Table no. " + finalSelectedTableNo + " is already reserved for customer. Do you want to cancel the booking?");
                    Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
                    button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(View v) {
                            updateData(finalSelectedTableNo, finalSelectedTableStatus, finalSelectedMobileNo, finalSelectedTime);
                            dialog.cancel();
                        }
                    });
                    Button button_menuitem_no = (Button) dialog.findViewById(R.id.button_menuitem_no);
                    button_menuitem_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.cancel();
                        }
                    });
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.create();
                    dialog.show();
                }
            }
        });

        imageView_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Manager_MainScreenActivity.redirectActivity(Manager_TableScreenActivity.this, Manager_TableScreenActivity.class);
            }
        });
    }

    public void updateData(String selectedTableNo, String selectedTableStatus, String selectedMobileNo, String selectedTime) {
        try {
            if(selectedTableStatus.equals("(*) No Future Booking")) {
                query = "INSERT INTO [ADMINISTRATOR].[FutureTableBookings] "
                        + " (table_Id, mobileNo, time_slot) "
                        + " VALUES ('" + selectedTableNo + "', '" + selectedMobileNo
                        + "', cast(cast(getdate() as date) as varchar) + ' ' + cast('" + selectedTime + "' as varchar))";
            }
            else {
                query = "DELETE FROM [ADMINISTRATOR].[FutureTableBookings] "
                        + " WHERE table_Id = '" + selectedTableNo + "';";
            }
            if (connect != null) {
                Statement statement = connect.createStatement();
                statement.executeUpdate(query);
                ConnectionResult="Success";
                isSuccess=true;
                Intent intent=new Intent(Manager_TableScreenActivity.this,Manager_TableScreenActivity.class);
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

    public void fillSpinner() {
        try {
            if (connect != null) {
                query = "SELECT DISTINCT table_Status FROM [ADMINISTRATOR].[Tables];";
                PreparedStatement statement = connect.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                ArrayList<String> data = new ArrayList<String>();
                String all = "All Table Status";
                data.add(all);
                while (resultSet.next()) {
                    String id = resultSet.getString("table_Status");
                    data.add(id);
                }
                ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, data);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner_TableStatus.setAdapter(arrayAdapter);
                ConnectionResult="Success";
                isSuccess=true;
            }
            else {
                ConnectionResult="Failed";
            }
        } catch(Exception ex) {
            Toast.makeText(Manager_TableScreenActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    //Menu
    public void ClickMenu (View view) {
        Manager_MainScreenActivity.openDrawer(drawerLayout);
    }

    //Logo
    public void ClickLogo (View view) {
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Tables
    public void ClickTables (View view) {
        recreate();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }

    //Menu Items
    public void ClickMenuItems (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_MenuItemScreenActivity.class);
    }

    //Orders
    public void ClickOrders (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_OrderScreenActivity.class);
    }

    //Feedback
    public void ClickFeedback (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_FeedbackScreenActivity.class);
    }

    //Change Password
    public void ClickChangePassword(View view) {
        Manager_MainScreenActivity.redirectActivity(this, Password_ChangeScreenActivity.class);
    }

    //Home
    public void ClickHome (View view) {
        Manager_MainScreenActivity.redirectActivity(this, Manager_MainScreenActivity.class);
    }

    //Logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickLogout (View view) {
        Manager_MainScreenActivity.logout(this);
    }

    //exit
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void ClickExit (View view) {
        Manager_MainScreenActivity.exit(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Manager_MainScreenActivity.closeDrawer(drawerLayout);
    }
}

