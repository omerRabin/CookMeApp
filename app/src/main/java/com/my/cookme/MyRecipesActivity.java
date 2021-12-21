package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyRecipesActivity extends AppCompatActivity {

    DatabaseReference usersDBRef;
    DatabaseReference recipesDBRef;
    private List<Recipe> mUploads;

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CookMe");
        usersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recipesDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        List<String> myRecipes = new ArrayList<>();

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];


        usersDBRef.child(user).child("MyRecipes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    HashMap<String, String> objectHashMap = (HashMap<String, String>) task.getResult().getValue();

                    if (objectHashMap == null) {
                        return;
                    }

                    index = 0;
                    for (String obj : objectHashMap.values()) {
                        recipesDBRef.child(obj).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    HashMap<String, Object> recipeHashMap = (HashMap<String, Object>) task.getResult().getValue();
                                    String ownerID = (String) recipeHashMap.get("ownerID");
                                    String description = (String) recipeHashMap.get("description");
                                    ArrayList<HashMap<String, String>> ingredientsList = (ArrayList<HashMap<String, String>>) recipeHashMap.get("ingredients");
                                    ArrayList<Ingredient> ingredients = convertToIngredientsList(ingredientsList);
                                    Long likesNumber = (Long) recipeHashMap.get("likesNumber");
                                    String name = (String) recipeHashMap.get("name");
                                    String preparationMethod = (String) recipeHashMap.get("preparationMethod");
                                    String uploadDate = (String) recipeHashMap.get("uploadDate");
                                    String imageUrl = (String) recipeHashMap.get("imageUrl");
                                    Recipe recipe = new Recipe(ownerID, name, ingredients, description, preparationMethod, imageUrl);
                                    mUploads.add(recipe);
                                    index++;
                                    if (index == objectHashMap.size()) {
                                        mAdapter = new ImageAdapter(MyRecipesActivity.this, mUploads);
                                        mRecyclerView.setAdapter(mAdapter);
                                    }
                                    //showIngredients.setText(showIngredients.getText() + name + "\n");
                                }
                            }
                        });
                        Toast.makeText(MyRecipesActivity.this, obj, Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });
    }


    private ArrayList<Ingredient> convertToIngredientsList(ArrayList<HashMap<String, String>> list) {
        ArrayList<Ingredient> ingredientArrayList = new ArrayList<>();
        for (HashMap<String, String> hash : list) {
            ingredientArrayList.add(new Ingredient(hash.get("name"), hash.get("description"), hash.get("category")));
        }
        return ingredientArrayList;
    }
}