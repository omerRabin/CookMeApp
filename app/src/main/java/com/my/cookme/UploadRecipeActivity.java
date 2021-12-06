package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UploadRecipeActivity extends AppCompatActivity {

    EditText etName;
    EditText etIng;
    Button btnInsertData;

    DatabaseReference recipeDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        etName = findViewById(R.id.editTextRecipeName);
        etIng = findViewById(R.id.editTextIng);
        btnInsertData = findViewById(R.id.buttonInsertData);

        recipeDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");

        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecipeData();
            }
        });
    }

    private void insertRecipeData() {
        String name = etName.getText().toString();
        String ings = etIng.getText().toString();
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("Banana", 2, "units", "fruits"));

        Recipe recipe = new Recipe("yoel2810", "cake", ingredientList);
        recipeDBRef.push().setValue(recipe);
        Toast.makeText(UploadRecipeActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
    }
}