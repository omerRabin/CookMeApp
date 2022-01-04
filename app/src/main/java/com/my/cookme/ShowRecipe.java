package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import com.my.cookme.Adapters.ImageAdapter;

import java.util.List;

public class ShowRecipe extends AppCompatActivity {

    private TextView textViewRecipeTitle;
    private EditText editTextDescription;
    private EditText editTextIngredients;
    private EditText editTextPrep;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        initializeXmlElements();
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");
        showRecipe(recipe);

    }

    private void initializeXmlElements() {
        this.textViewRecipeTitle = findViewById(R.id.ingredientTitle);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextIngredients = findViewById(R.id.editTextIngredients);
        this.editTextPrep = findViewById(R.id.editTextPreparationMethod);
        this.imageView = findViewById(R.id.imageView2);
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
        //Picasso.with(this).load(recipe.getImageUrl()).fit().centerCrop().into(this.imageView);
    }
}