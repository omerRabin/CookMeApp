package com.my.cookme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;


public class PersonalAreaActivity extends AppCompatActivity {

    private Button buttonSensitivity;
    private Button buttonMyRecipes;
    private Button buttonFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_area);
        getSupportActionBar().setTitle("CookMe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.buttonFavorites = findViewById(R.id.buttonFavorite);
        this.buttonMyRecipes = findViewById(R.id.buttonMyRecipes);
        this.buttonSensitivity = findViewById(R.id.buttonSensitivity);

        this.buttonSensitivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalAreaActivity.this,SensitivityActivity.class);
                startActivity(intent);
            }
        });

        this.buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalAreaActivity.this, FavoriteRecipesActivity.class);
                startActivity(intent);
            }
        });

        this.buttonMyRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalAreaActivity.this, MyRecipesActivity.class);
                startActivity(intent);
            }
        });
    }
}