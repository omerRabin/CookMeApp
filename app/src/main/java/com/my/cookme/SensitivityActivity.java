package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SensitivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity);
        getSupportActionBar().setTitle("CookMe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}