package com.example.customer_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class FeedbackActivity extends AppCompatActivity {

    Connection connect;
    String ConnectionResult = "";
    Boolean isSuccess = false;
    String mobile ="", order_Id="", tableID="";

    TextView toolbar_Text, textview_name;
    ImageView imageView_Cart, imageMenu;

    TextView ambience, price, food_quantity, food_quality, service;
    RatingBar ambienceRatingBar, quantityRatingBar, qualityRatingBar, serviceRatingBar, priceRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbackscreen);

        Intent intent = new Intent();
        mobile = String.format(getIntent().getStringExtra("mobile"));
        order_Id = getIntent().getStringExtra("order_Id");
        tableID = getIntent().getStringExtra("tableID");

        toolbar_Text = findViewById(R.id.toolbar_Text);
        toolbar_Text.setText("Feedback");
        imageView_Cart = findViewById(R.id.ic_click_cart);
        imageView_Cart.setVisibility(View.GONE);
        imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setVisibility(View.GONE);

        ambience = (TextView) findViewById(R.id.ambience);
        price = (TextView) findViewById(R.id.price);
        food_quantity = (TextView) findViewById(R.id.food_quantity);
        food_quality = (TextView) findViewById(R.id.food_quality);
        service = (TextView) findViewById(R.id.service);

        qualityRatingBar = (RatingBar) findViewById(R.id.qualityRatingBar);
        quantityRatingBar = (RatingBar) findViewById(R.id.quantityRatingBar);
        serviceRatingBar = (RatingBar) findViewById(R.id.serviceRatingBar);
        priceRatingBar = (RatingBar) findViewById(R.id.priceRatingBar);
        ambienceRatingBar = (RatingBar) findViewById(R.id.ambienceRatingBar);

        Button buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        Button buttonExit = (Button) findViewById(R.id.buttonExit);
        TextView textViewThankU = findViewById(R.id.textViewThankU);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rating1 = "" + qualityRatingBar.getRating();
                String rating2 = "" + quantityRatingBar.getRating();
                String rating3 = "" + serviceRatingBar.getRating();
                String rating4 = "" + priceRatingBar.getRating();
                String rating5 = "" + ambienceRatingBar.getRating();

                if (rating1.toString().equals("0.0") || rating2.toString().equals("0.0") || rating3.toString().equals("0.0") ||
                        rating4.toString().equals("0.0") || rating5.toString().equals("0.0"))
                {
                    Toast.makeText(FeedbackActivity.this, "Rating cannot be zero.Please enter valid rating.", Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        ConnectionHelper connectionHelper = new ConnectionHelper();
                        connect = connectionHelper.connectionClass();
                        if (connect != null) {
                            String insert_query = "INSERT INTO [CUSTOMER].[FeedbackDetails]"
                                    + " (order_Id, mobileNo, quality_rating, quantity_rating, service_rating, price_rating, ambience_rating)"
                                    + " VALUES ('" + order_Id + "', '" + mobile.replace("+91-", "") + "', '"
                                    + rating1 + "', '" + rating2 + "', '" + rating3 + "', '" + rating4 + "', '" + rating5 + "');";
                            PreparedStatement preStmt = connect.prepareStatement(insert_query);
                            preStmt.executeUpdate();

                            String update_query = "UPDATE [ADMINISTRATOR].[Tables] SET mobileNo = NULL, reserved_by = NULL, "
                                    +" table_Status = 'Available' WHERE table_Id = '" + tableID +"' ;";
                            PreparedStatement preStmt1 = connect.prepareStatement(update_query);
                            preStmt1.executeUpdate();
                            ConnectionResult = "Success";
                            isSuccess = true;

                            textViewThankU.setVisibility(View.VISIBLE);
                            buttonSubmit.setVisibility(View.GONE);
                            toolbar_Text.setText("Thank You");
                            buttonExit.setVisibility(View.VISIBLE);

                            ambience.setVisibility(View.GONE);
                            price.setVisibility(View.GONE);
                            food_quality.setVisibility(View.GONE);
                            food_quantity.setVisibility(View.GONE);
                            service.setVisibility(View.GONE);

                            ambienceRatingBar.setVisibility(View.GONE);
                            priceRatingBar.setVisibility(View.GONE);
                            qualityRatingBar.setVisibility(View.GONE);
                            serviceRatingBar.setVisibility(View.GONE);
                            quantityRatingBar.setVisibility(View.GONE);
                        } else {
                            ConnectionResult = "Failed";
                        }
                    } catch (Exception ex) {
                        Toast.makeText(FeedbackActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
    }
}
