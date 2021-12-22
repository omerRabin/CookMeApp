package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ShowRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        Object recipe = getIntent().getByteArrayExtra("recipe");
        int x = 1;
    }
}