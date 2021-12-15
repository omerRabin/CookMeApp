package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class FavoriteRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CookMe");
    }
}