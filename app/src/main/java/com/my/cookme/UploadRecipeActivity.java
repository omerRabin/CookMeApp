package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class UploadRecipeActivity extends AppCompatActivity {

    EditText etName;
    EditText etIng;
    EditText etDescription;
    Button btnInsertData;
    Button btnAddIngredient;
    LinearLayout ll;

    DatabaseReference recipeDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        etName = findViewById(R.id.editTextRecipeName);
        //etIng = findViewById(R.id.editTextIng);
        etDescription = findViewById(R.id.editTextDescription);
        btnInsertData = findViewById(R.id.buttonInsertData);
        btnAddIngredient = findViewById(R.id.addIngBtn);
        ll = (LinearLayout) findViewById(R.id.ingredientsLayouts);

        recipeDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");

        btnInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertRecipeData();
            }
        });

        btnAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout linearLayout = new LinearLayout(UploadRecipeActivity.this);
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                EditText amount = new EditText(UploadRecipeActivity.this);
                amount.setText("");
                amount.setHint("Amount");

                EditText ingredient = new EditText(UploadRecipeActivity.this);
                ingredient.setText("");
                ingredient.setHint("Ingredient");

                EditText description = new EditText(UploadRecipeActivity.this);
                description.setText("");
                description.setHint("Description");

                linearLayout.addView(amount);
                linearLayout.addView(ingredient);
                linearLayout.addView(description);
                ll.addView(linearLayout);
                linearLayout.invalidate();
                ll.invalidate();
            }
        });
    }

    private void insertRecipeData() {
        String name = etName.getText().toString();
        String ings = etIng.getText().toString();
        String description = etDescription.getText().toString();
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("Banana", 2, "units", "fruits", null));

        Recipe recipe = new Recipe(FirebaseAuth.getInstance().getCurrentUser().getEmail(), name, ingredientList, description);
        recipeDBRef.push().setValue(recipe);
        Toast.makeText(UploadRecipeActivity.this, "Data inserted", Toast.LENGTH_SHORT).show();
    }
}