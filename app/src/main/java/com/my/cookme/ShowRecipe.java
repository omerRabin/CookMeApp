package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ShowRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);


// To retrieve object in second Activity
        Object o = getIntent().getSerializableExtra("recipe");

        int x = 1;
    }
}