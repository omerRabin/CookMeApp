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

public class FavoriteRecipesActivity extends AppCompatActivity
        implements ImageAdapter.OnItemClickListener,
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
    private ValueEventListener mDBListener;

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

        mAdapter = new ImageAdapter(FavoriteRecipesActivity.this, mUploads);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(FavoriteRecipesActivity.this);

        List<String> myRecipes = new ArrayList<>();

        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];


        mDBListener = usersDBRef.child(user).child("likedRecipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    Like like = postSnapShot.getValue(Like.class);
                    recipeKeyKey = postSnapShot.getKey();

                    assembleRecipe(like.getRecipeKey());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FavoriteRecipesActivity.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assembleRecipe(String recipeKey) {
        recipesDBRef.child(recipeKey).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Recipe recipe = task.getResult().getValue(Recipe.class);
                    recipe.setKey(recipeKey);
                    mUploads.add(recipe);
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(FavoriteRecipesActivity.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(FavoriteRecipesActivity.this, ShowRecipe.class);
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


        if (this.recipeKeyKey == null) {
            Toast.makeText(this, "We are sorry something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }

        Recipe selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.getKey();


        String user = MainActivity.getUser();
        usersDBRef.child(user).child("likedRecipes").child(recipeKeyKey).removeValue();
        //selectedItem.removeLike(MainActivity.getEmail());
        Toast.makeText(FavoriteRecipesActivity.this, "Item has been removed from your favorites", Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        usersDBRef.child(user).child("MyRecipes").removeEventListener(mDBListener);
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
        //drawerLayout.closeDrawer(GravityCompat.START);
        //} else {
        super.onBackPressed();
        //}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent0 = new Intent(FavoriteRecipesActivity.this, DashboardActivity.class);
                startActivity(intent0);
                break;
            case R.id.nav_cookme:
                Intent intent = new Intent(FavoriteRecipesActivity.this, choose_for_recipe.class);
                startActivity(intent);
                break;
            case R.id.nav_upload_recipe:
                Intent intent1 = new Intent(FavoriteRecipesActivity.this, UploadRecipeActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_login:
            case R.id.nav_logout:
                Intent intent2 = new Intent(FavoriteRecipesActivity.this, MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_profile:
                Intent intent3 = new Intent(FavoriteRecipesActivity.this, PersonalAreaActivity.class);
                startActivity(intent3);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}