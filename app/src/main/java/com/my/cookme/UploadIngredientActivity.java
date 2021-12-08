package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UploadIngredientActivity extends AppCompatActivity {
    
    private EditText editTextName;
    private EditText editTextCategory;
    private Button buttonAdd;
    private DatabaseReference ingredientDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ingredient);
        
        this.editTextName = findViewById(R.id.editTextIngredientName);
        this.editTextCategory = findViewById(R.id.editTextCategory);
        this.ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        this.buttonAdd = findViewById(R.id.ButtonAddIngredient);
        
        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIngredientData();
            }
        });
    }

    private void insertIngredientData() {
        String name = editTextName.getText().toString();
        String category = editTextCategory.getText().toString();
        Ingredient i = new Ingredient(name, null, category);
        ingredientDBRef.push().setValue(i);
        Toast.makeText(UploadIngredientActivity.this, "UPLOADED", Toast.LENGTH_SHORT).show();
    }
}