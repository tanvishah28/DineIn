package com.example.restaurant_dinein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.restaurant_dinein.R;
import java.util.Calendar;

public class Restaurant_WelcomeScreenActivity extends AppCompatActivity {
    String staff_member="";
    Animation blink_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_mainscreen);

        blink_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String message = "";
        if (timeOfDay >= 0 && timeOfDay < 12) {
            message = "Hello, Good Morning!";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            message = "Hello, Good Afternoon!";
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            message = "Hello, Good Evening!";
        }

        TextView tv = findViewById(R.id.textview_greet);
        tv.setText(message);

        View image_Admin = findViewById(R.id.admin_image);
        image_Admin.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staff_member="Admin";
                Intent intent=new Intent(Restaurant_WelcomeScreenActivity.this,Restaurant_LoginActivity.class);
                intent.putExtra("staff_member", staff_member);
                startActivity(intent);
            }
        }));

        TextView tv_welcome_message = (TextView) findViewById(R.id.welcome_message);
        tv_welcome_message.startAnimation(blink_anim);

        View image_Chef = findViewById(R.id.chef_image);
        image_Chef.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staff_member="Chef";
                Intent intent=new Intent(Restaurant_WelcomeScreenActivity.this,Restaurant_LoginActivity.class);
                intent.putExtra("staff_member", staff_member);
                startActivity(intent);
            }
        }));

        View image_manager = findViewById(R.id.manager_image);
        image_manager.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                staff_member="Manager";
                Intent intent=new Intent(Restaurant_WelcomeScreenActivity.this,Restaurant_LoginActivity.class);
                intent.putExtra("staff_member", staff_member);
                startActivity(intent);
            }
        }));
    }
}
