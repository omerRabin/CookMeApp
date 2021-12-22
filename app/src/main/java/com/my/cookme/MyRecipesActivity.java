package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

public class MyRecipesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener{

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



        usersDBRef.child(user).child("MyRecipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    String recipeKey = postSnapShot.getValue(String.class);
                    assembleRecipe(recipeKey);
                }

                mAdapter = new ImageAdapter(MyRecipesActivity.this, mUploads);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(MyRecipesActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyRecipesActivity.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assembleRecipe(String recipeKey) {
        recipesDBRef.child(recipeKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Recipe recipe = task.getResult().getValue(Recipe.class);
                    mUploads.add(recipe);
                    index++;
                    mAdapter = new ImageAdapter(MyRecipesActivity.this, mUploads);
                    mRecyclerView.setAdapter(mAdapter);
                    mAdapter.setOnItemClickListener(MyRecipesActivity.this);
                }
                else {
                    Toast.makeText(MyRecipesActivity.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "normal click at  position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShowRecipeClick(int position) {
        Toast.makeText(this, "show click at  position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Toast.makeText(this, "delete click at  position: " + position, Toast.LENGTH_SHORT).show();
    }
}