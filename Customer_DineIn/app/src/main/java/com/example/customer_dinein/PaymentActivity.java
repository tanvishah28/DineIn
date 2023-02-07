package com.example.customer_dinein;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PaymentActivity extends AppCompatActivity {

    Button button_SendOtp;
    TextView textView_OrderNo, textView_MobNo, textView_TableNo, textView_TotalPayableAmountDisplay;
    RadioButton radio_debit, radio_credit;
    TextInputLayout dropdownMonth,dropdownYear;
    EditText editText_CardNo,editText_CardName,editText_Cvv;
    ImageView image_Card;

    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart, imageMenu;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String mobile ="", tableID="", order_Id="", radioSelected = "Debit Card",
            get_total_amount_query="", total_Amount="";
    View view;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_screen);

        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Payment");
        imageView_Cart = findViewById(R.id.ic_click_cart);
        imageView_Cart.setVisibility(View.GONE);
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);

        Intent intent = new Intent();
        mobile = String.format("+91-%s", getIntent().getStringExtra("mobile"));
        tableID = getIntent().getStringExtra("tableID");
        order_Id = getIntent().getStringExtra("order_Id");

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        textView_OrderNo = (TextView) findViewById(R.id.textView_OrderNo);
        textView_MobNo = (TextView) findViewById(R.id.textView_MobNo);
        textView_TableNo = (TextView) findViewById(R.id.textView_TableNo);
        textView_TotalPayableAmountDisplay = (TextView) findViewById(R.id.textView_AmtPayable);
        button_SendOtp = (Button) findViewById(R.id.button_SendOtp);
        radio_debit = (RadioButton) findViewById(R.id.radio_debit);
        radio_credit = (RadioButton) findViewById(R.id.radio_credit);
        dropdownMonth = (TextInputLayout) findViewById(R.id.dropdownMonth);
        dropdownYear = (TextInputLayout) findViewById(R.id.dropdownYear);
        image_Card = (ImageView) findViewById(R.id.image_Card);
        editText_CardNo = (EditText) findViewById(R.id.editText_CardNo);
        editText_CardName = (EditText) findViewById(R.id.editText_CardName);
        editText_Cvv = (EditText) findViewById(R.id.editText_Cvv);

        String[] typeMonth = new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.list_item,
                        typeMonth);

        AutoCompleteTextView valueMonthFilledExposedDropdown =
                findViewById(R.id.valueMonth);
        valueMonthFilledExposedDropdown.setAdapter(adapter);

        String[] typeYear = new String[]{"2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"};

        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>(
                        this,
                        R.layout.list_item,
                        typeYear);

        AutoCompleteTextView valueYearFilledExposedDropdown =
                findViewById(R.id.valueYear);
        valueYearFilledExposedDropdown.setAdapter(adapter2);

        calculate_total_amount(mobile, tableID, order_Id);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_debit) {
                    radioSelected = "Debit Card";
                } else if (checkedId == R.id.radio_credit) {
                    radioSelected = "Credit Card";
                }
            }
        });

        ArrayList<String> listOfPattern=new ArrayList<String>();
        String cardVisa = "^4\\d*";
        listOfPattern.add(cardVisa);
        String cardAmex = "^3[47]\\d*";
        listOfPattern.add(cardAmex);
        String cardMaestro = "^(5018|5020|5038|5043|5[6-9]|6020|6304|6703|6759|676[1-3])\\d*";
        listOfPattern.add(cardMaestro);
        String cardMaster = "^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[0-1]|2720)\\d*";
        listOfPattern.add(cardMaster);
        String cardDiscover = "^(6011|65|64[4-9]|622)\\d*";
        listOfPattern.add(cardDiscover);

        editText_CardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editText_CardNo.getText().toString().matches(cardVisa)) {
                    image_Card.setImageResource(R.drawable.bt_ic_visa);
                }
                else if(editText_CardNo.getText().toString().matches(cardAmex)) {
                    image_Card.setImageResource(R.drawable.bt_ic_amex);
                }
                else if(editText_CardNo.getText().toString().matches(cardMaestro)) {
                    image_Card.setImageResource(R.drawable.bt_ic_maestro);
                }
                else if(editText_CardNo.getText().toString().matches(cardMaster)) {
                    image_Card.setImageResource(R.drawable.bt_ic_mastercard);
                }
                else if(editText_CardNo.getText().toString().matches(cardDiscover)) {
                    image_Card.setImageResource(R.drawable.bt_ic_discover);
                }
                else {
                    image_Card.setImageResource(R.drawable.bt_ic_unknown);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        button_SendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_CardNo.getText().toString().isEmpty() || editText_CardName.getText().toString().isEmpty()
                        || editText_Cvv.getText().toString().isEmpty())
                {
                    Toast.makeText(PaymentActivity.this, "Fields cannot be empty.Please enter details", Toast.LENGTH_LONG).show();
                }
                else if(editText_CardNo.getText().toString().length()<16)
                {
                    Toast.makeText(PaymentActivity.this, "Invalid Card No.", Toast.LENGTH_LONG).show();
                }
                else if((valueMonthFilledExposedDropdown.getText().toString().equals("01")||valueMonthFilledExposedDropdown.getText().toString().equals("02")||
                        valueMonthFilledExposedDropdown.getText().toString().equals("03")||valueMonthFilledExposedDropdown.getText().toString().equals("04")||
                        valueMonthFilledExposedDropdown.getText().toString().equals("05")||valueMonthFilledExposedDropdown.getText().toString().equals("06"))
                         && valueYearFilledExposedDropdown.getText().toString().equals("2021"))
                {
                    Toast.makeText(PaymentActivity.this, "Invalid expiry month and year. ", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        if (connect != null) {
                            String update_query = "UPDATE [CUSTOMER].[PaymentDetails] SET paid_Via = '" + radioSelected
                                    + "' , cardholder_Name = '" + editText_CardName.getText()
                                    + "', card_No = '" + editText_CardNo.getText()
                                    + "', exp_Month = '" + valueMonthFilledExposedDropdown.getText()
                                    + "', exp_Year = '" + valueYearFilledExposedDropdown.getText()
                                    + "', date_updated = GETDATE() "
                                    + "  WHERE mobileNo = '" + mobile.replace("+91-", "")
                                    + "' AND tableId = '" + tableID + "' AND order_Id = '" + order_Id
                                    + "' AND payment_Status = 'Pending';";
                            System.out.println("Customer: " + update_query);
                            PreparedStatement preStmt = connect.prepareStatement(update_query);
                            preStmt.executeUpdate();
                            ConnectionResult = "Success";
                            isSuccess = true;

                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    "+91-" + mobile.replace("+91-", ""),
                                    60,
                                    TimeUnit.SECONDS,
                                    PaymentActivity.this,
                                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            button_SendOtp.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            button_SendOtp.setVisibility(View.VISIBLE);
                                            Toast.makeText(PaymentActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            button_SendOtp.setVisibility(View.VISIBLE);
                                            System.out.println("Code Sent");
                                            Intent intent = new Intent(getApplicationContext(), PaymentOTPActivity.class);
                                            intent.putExtra("mobile", mobile);
                                            intent.putExtra("tableID", tableID);
                                            intent.putExtra("order_Id", order_Id);
                                            intent.putExtra("total_Amount", total_Amount);
                                            intent.putExtra("verificationId", verificationId);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                            );
                        } else {
                            ConnectionResult = "Failed";
                        }
                    } catch (Exception ex) {
                        Toast.makeText(PaymentActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    public void calculate_total_amount(String mobile, String tableID, String order_Id) {
        get_total_amount_query =  "SELECT order_Id, mobileNo, tableID,"
                + " CAST(SUM(price_Amount + tax_Amount) AS NUMERIC(9,2)) AS total_Amount "
                + " FROM [CUSTOMER].[OrderDetails] "
                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                + "' AND tableID = '" + tableID + "' AND order_Id = '" + order_Id + "'AND order_Status = 'Pending' "
                + " GROUP BY mobileNo , tableID, order_Id ;";

        try {
            if (connect != null) {
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(get_total_amount_query);
                while (resultSet.next()) {
                    order_Id = resultSet.getString(1);
                    mobile = resultSet.getString(2);
                    tableID = resultSet.getString(3);
                    total_Amount = resultSet.getString(4);
                }
                textView_OrderNo.setText("Order#: " + order_Id);
                textView_MobNo.setText("Mobile No.: +91-" + mobile);
                textView_TableNo.setText("Table No.: " + tableID);
                textView_TotalPayableAmountDisplay.setText("Amount Payable: ₹" + total_Amount);

                ConnectionResult = "Success";
                isSuccess = true;
            } else {
                ConnectionResult = "Failed";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        TextView text_menuitem_dialog = (TextView) dialog.findViewById(R.id.text_menuitem_dialog);
        text_menuitem_dialog.setText("Are you sure you want to cancel this order and exit?");
        Button button_menuitem_yes = (Button) dialog.findViewById(R.id.button_menuitem_yes);
        button_menuitem_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (connect != null) {
                        String update_query = "UPDATE [ADMINISTRATOR].[Tables] "
                                + " SET reserved_by = NULL, mobileNo = NULL, table_Status = 'Available'"
                                + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND table_Id = '" + tableID + "';";
                        PreparedStatement preStmt = connect.prepareStatement(update_query);
                        preStmt.executeUpdate();
                        String delete_query = "DELETE FROM [CUSTOMER].[AddToCart] WHERE mobileNo = '"
                                + mobile.replace("+91-", "") + "' AND tableId = '" + tableID + "';";
                        PreparedStatement preStmtDelete = connect.prepareStatement(delete_query);
                        preStmtDelete.executeUpdate();
                        String update_order_query = "UPDATE [CUSTOMER].[OrderDetails] SET order_Status = 'Cancelled', date_updated = GETDATE()"
                                + " WHERE order_Status = 'Pending' AND order_Id = '" + order_Id
                                + "' AND mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND tableId = '" + tableID + "';";
                        PreparedStatement preStmt_Order = connect.prepareStatement(update_order_query);
                        preStmt_Order.executeUpdate();
                        String update_payment_query = "UPDATE [CUSTOMER].[PaymentDetails] SET payment_Status = 'Cancelled', date_updated = GETDATE()"
                                + " WHERE payment_Status = 'Pending' AND order_Id = '" + order_Id
                                + "' AND mobileNo = '" + mobile.replace("+91-", "")
                                + "' AND tableId = '" + tableID + "';";
                        PreparedStatement preStmt_Payment = connect.prepareStatement(update_payment_query);
                        preStmt_Payment.executeUpdate();
                        ConnectionResult="Success";
                        isSuccess=true;
                        dialog.dismiss();
                        PaymentActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(PaymentActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
                finish();
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
