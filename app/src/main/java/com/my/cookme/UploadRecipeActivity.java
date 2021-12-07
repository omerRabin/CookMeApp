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
import java.util.Locale;

public class UploadRecipeActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextIngredients;
    private EditText editTextPreparationMethod;
    private Button buttonInsertData;

    DatabaseReference recipeDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        recipeDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");

        this.editTextName = findViewById(R.id.editTextRecipeName);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextIngredients = findViewById(R.id.editTextIngredients);
        this.editTextPreparationMethod = findViewById(R.id.editTextPreparationMethod);
        this.buttonInsertData = findViewById(R.id.buttonInsertData);

        this.buttonInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRecipeData();
            }
        });
    }

    private void insertRecipeData() {
        String[] ingredients_db = {"Egg", "Apple", "Milk", "Oil", "Garlic", "Nut", "Lemon", "Butter", "Sugar", "Juice", "Corn", "Cheese", "Flour", "Yeast", "Salt", "Water", "Margarine"};
        String recipeName = this.editTextName.getText().toString();
        String description = this.editTextDescription.getText().toString();
        String ingredients = this.editTextIngredients.getText().toString();
        String preparationMethod = this.editTextPreparationMethod.getText().toString();
        String[] lines = ingredients.split("\n");
        List<Ingredient> ingredientList = new ArrayList<>();

        for (String line :
                lines) {
            for (String ing :
                    ingredients_db) {
                if (line.toLowerCase().contains(ing.toLowerCase())) {
                    ingredientList.add(new Ingredient(ing, line));
                }
            }
        }

        Recipe recipe = new Recipe(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                recipeName, ingredientList, description, preparationMethod);

        recipeDBRef.push().setValue(recipe);


    }
}