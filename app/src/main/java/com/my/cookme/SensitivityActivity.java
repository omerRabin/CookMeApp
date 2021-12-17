package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SensitivityActivity extends AppCompatActivity {

    private EditText sen;
    private Button addSensitivity;
    private Button removeSensitivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitivity);
        getSupportActionBar().setTitle("CookMe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.sen = findViewById(R.id.editTextIngredientSensitive);
        this.addSensitivity = findViewById(R.id.buttonAddSensitivity);
        this.removeSensitivity = findViewById(R.id.buttonRemoveSensitivity);

        this.addSensitivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}