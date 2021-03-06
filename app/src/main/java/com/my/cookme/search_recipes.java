package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.my.cookme.Adapters.ImageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class search_recipes extends AppCompatActivity implements ImageAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference usersDBRef;
    DatabaseReference recipesDBRef;
    private List<Recipe> mUploads;
    private String recipeKeyKey;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private FirebaseStorage mStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);
        //initializeMenu();
        usersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recipesDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mStorage = FirebaseStorage.getInstance();
        this.recipeKeyKey = null;

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapter = new ImageAdapter(search_recipes.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(search_recipes.this);

        List<String> myRecipes = new ArrayList<>();

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        ArrayList<String> chosenRecipes = choose_for_recipe.cart_list;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recipes");


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    Recipe recipe = postSnapShot.getValue(Recipe.class);
                    recipe.setKey(postSnapShot.getKey());
                    if (filterRecipe(recipe, choose_for_recipe.cart_list)) {
                        mUploads.add(recipe);

                    }
                    mAdapter.notifyDataSetChanged();

                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search_recipes.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean filterRecipe(Recipe recipe, List<String> list) {
        List<Ingredient> recipeIngredientList = recipe.getIngredients();
        if (list.size() < recipeIngredientList.size())
            return false;

        for (int i = 0; i < recipeIngredientList.size(); i++) {
            if (!list.contains(recipeIngredientList.get(i).getName()))
                return false;
        }
        return true;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(search_recipes.this, ShowRecipe.class);
        Recipe selectedItem = mUploads.get(position);
        intent.putExtra("recipe", selectedItem);
        startActivity(intent);
    }

    @Override
    public void onLikeRecipeClick(int position) {
        Recipe selectedItem = mUploads.get(position);
        String username = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        selectedItem.addLike(new Like(username, selectedItem.getKey()));
    }

    @Override
    public void onDeleteClick(int position) {

        Recipe selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.getKey();

        if (MainActivity.isAdmin() == false && MainActivity.getEmail().equals(selectedItem.getOwnerID()) == false) {
            Toast.makeText(search_recipes.this, "You are not the admin or the recipe owner", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(search_recipes.this, "Unkown error", Toast.LENGTH_SHORT).show();

        /*getRecipeKeyKey(selectedItem.getKey(), selectedItem.getOwnerID());

        while (recipeKeyKey == null) {

        }



        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                recipesDBRef.child(selectedKey).removeValue();
                String user = selectedItem.getOwnerID().split("@")[0];
                usersDBRef.child(user).child("MyRecipes").child(recipeKeyKey).removeValue();
                Toast.makeText(search_recipes.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void initializeMenu() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        //if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
        // drawerLayout.closeDrawer(GravityCompat.START);
        //} else {
        super.onBackPressed();
        //}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent0 = new Intent(search_recipes.this, DashboardActivity.class);
                startActivity(intent0);
                break;
            case R.id.nav_cookme:
                Intent intent = new Intent(search_recipes.this, choose_for_recipe.class);
                startActivity(intent);
                break;
            case R.id.nav_upload_recipe:
                Intent intent1 = new Intent(search_recipes.this, UploadRecipeActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_login:
            case R.id.nav_logout:
                Intent intent2 = new Intent(search_recipes.this, MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_profile:
                Intent intent3 = new Intent(search_recipes.this, PersonalAreaActivity.class);
                startActivity(intent3);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getRecipeKeyKey(String recipeKey, String owner) {
        int x =1;
        usersDBRef.child(owner.split("@")[0]).child("MyRecipes").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful())
                    Toast.makeText(search_recipes.this, "oops, something went wrong", Toast.LENGTH_SHORT).show();
                else {
                    Object o = task.getResult().getValue();
                }
            }
        });


    }
}
