package com.example.restaurant_dinein;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.restaurant_dinein.R;


public class Restaurant_SplashActivity extends Activity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_splash);

        Button buttonGetStarted = findViewById(R.id.buttonGetStarted);

        buttonGetStarted.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Restaurant_SplashActivity.this,Restaurant_WelcomeScreenActivity.class);
                //Intent intent=new Intent(Restaurant_SplashActivity.this,Manager_MenuItemScreenActivity.class);
                startActivity(intent);
                finish();
            }
        }));
    }
}