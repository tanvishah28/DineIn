package com.example.customer_dinein;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class PaymentOTPActivity extends AppCompatActivity {
    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private String verificationId;
    TextView textView_OrderNo, textView_MobNo, textView_TableNo, textView_TotalPayableAmountDisplay;

    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart, imageMenu;

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String mobile ="", tableID="", order_Id="", get_total_amount_query="", total_Amount="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_otp_screen);

        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(getIntent().getStringExtra("mobile"));

        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Payment OTP Verification");
        imageView_Cart = findViewById(R.id.ic_click_cart);
        imageView_Cart.setVisibility(View.GONE);
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);

        Intent intent = new Intent();
        mobile = String.format("+91-%s", getIntent().getStringExtra("mobile"));
        tableID = getIntent().getStringExtra("tableID");
        order_Id = getIntent().getStringExtra("order_Id");
        System.out.println("Customer - Payment, " + mobile + ":" + tableID + ":" + order_Id );

        ConnectionHelper connectionHelper = new ConnectionHelper();
        connect = connectionHelper.connectionClass();

        textView_OrderNo = (TextView) findViewById(R.id.textView_OrderNo);
        textView_MobNo = (TextView) findViewById(R.id.textView_MobNo);
        textView_TableNo = (TextView) findViewById(R.id.textView_TableNo);
        textView_TotalPayableAmountDisplay = (TextView) findViewById(R.id.textView_AmtPayable);

        calculate_total_amount(mobile, tableID, order_Id);

        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);

        setupOTPInputs();

        final ProgressBar progressBar = findViewById(R.id.progressBarOTPVerification);
        final Button buttonSubmitOtp = findViewById(R.id.buttonSubmitOtp);

        verificationId = getIntent().getStringExtra("verificationId");
        buttonSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputCode1.getText().toString().trim().isEmpty()
                        || inputCode2.getText().toString().trim().isEmpty()
                        || inputCode3.getText().toString().trim().isEmpty()
                        || inputCode4.getText().toString().trim().isEmpty()
                        || inputCode5.getText().toString().trim().isEmpty()
                        || inputCode6.getText().toString().trim().isEmpty()) {
                    Toast.makeText(PaymentOTPActivity.this, "Please enter valid code", Toast.LENGTH_SHORT).show();
                    return;
                }

                String code = inputCode1.getText().toString() +
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString() +
                        inputCode4.getText().toString() +
                        inputCode5.getText().toString() +
                        inputCode6.getText().toString();

                if(verificationId != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    buttonSubmitOtp.setVisibility(View.INVISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    buttonSubmitOtp.setVisibility(View.VISIBLE);
                                    if(task.isSuccessful()) {
                                        try {
                                            if (connect != null) {
                                                String update_query = "UPDATE [CUSTOMER].[PaymentDetails] SET payment_Status ='Paid', date_updated = GETDATE()"
                                                        + " WHERE mobileNo = '" + mobile.replace("+91-", "")
                                                        + "' AND tableId = '" + tableID + "' AND order_Id = '" + order_Id
                                                        + "' AND payment_Status = 'Pending';";
                                                System.out.println("Customer: " + update_query);
                                                PreparedStatement preStmt = connect.prepareStatement(update_query);
                                                preStmt.executeUpdate();
                                                ConnectionResult = "Success";
                                                isSuccess = true;
                                            } else {
                                                ConnectionResult = "Failed";
                                            }
                                        } catch (Exception ex) {
                                            Toast.makeText(PaymentOTPActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                                        }
                                        Intent intent = new Intent(getApplicationContext(), com.example.customer_dinein.OrderPlacedTimerActivity.class);
                                        intent.putExtra("mobile", getIntent().getStringExtra("mobile"));
                                        intent.putExtra("tableID", tableID);
                                        intent.putExtra("order_Id", order_Id);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(PaymentOTPActivity.this, "Verification code entered is invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        findViewById(R.id.textResendOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91-" + getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        PaymentOTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(PaymentOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = newVerificationId;
                                Toast.makeText(PaymentOTPActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    private void setupOTPInputs() {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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
        final Dialog dialog = new Dialog(PaymentOTPActivity.this);
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
                        PaymentOTPActivity.super.onBackPressed();
                        finish();
                    }
                    else {
                        ConnectionResult="Failed";
                    }
                } catch(Exception ex) {
                    Toast.makeText(PaymentOTPActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
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
