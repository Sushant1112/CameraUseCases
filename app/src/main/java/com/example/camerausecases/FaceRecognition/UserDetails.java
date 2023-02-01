package com.example.camerausecases.FaceRecognition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.camerausecases.R;

public class UserDetails extends AppCompatActivity {

    private TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        SharedPreferences prefs = this.getSharedPreferences(
                "userName", Context.MODE_PRIVATE);

        userName = findViewById(R.id.userName);
        userName.setText("Welcome " + prefs.getString("userName", "USER NAME"));
    }
}