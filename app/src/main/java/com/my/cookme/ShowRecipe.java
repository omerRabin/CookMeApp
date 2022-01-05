package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import com.my.cookme.Adapters.ImageAdapter;

import java.util.List;

public class ShowRecipe extends AppCompatActivity {

    private TextView textViewRecipeTitle;
    private EditText editTextDescription;
    private EditText editTextIngredients;
    private EditText editTextPrep;
    private ImageView imageView;
    private TextView likes;

    private DatabaseReference databaseReferenceRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        initializeXmlElements();
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        databaseReferenceRecipe = FirebaseDatabase.getInstance().getReference().child("Recipes").child(recipe.getKey());
        showRecipe(recipe);
    }

    private void initializeXmlElements() {
        this.textViewRecipeTitle = findViewById(R.id.ingredientTitle);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextIngredients = findViewById(R.id.editTextIngredients);
        this.editTextPrep = findViewById(R.id.editTextPreparationMethod);
        this.imageView = findViewById(R.id.imageView2);
        this.likes = findViewById(R.id.likes);
    }

    private void showRecipe(Recipe recipe) {
        this.textViewRecipeTitle.setText(recipe.getName());
        this.editTextDescription.setText(recipe.getDescription());
        List<Ingredient> ingredientList = recipe.getIngredients();
        String ingredients = "";
        for (int i = 0; i < ingredientList.size(); i++) {
            if (i == ingredientList.size() - 1)
                ingredients += ingredientList.get(i).getDescription();
            else
                ingredients += ingredientList.get(i).getDescription() + "\n";
        }
        this.editTextIngredients.setText(ingredients);
        this.editTextPrep.setText(recipe.getPreparationMethod());
        Picasso.with(this).load(Uri.parse(recipe.getImageUrl())).into(imageView);
        this.likes.setText(recipe.getLikesNumber() - 1 + " likes");
    }
}