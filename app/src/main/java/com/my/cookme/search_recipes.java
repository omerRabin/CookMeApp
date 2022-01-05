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
    private ValueEventListener mDBListener;
    ArrayList<Recipe> l3 = new ArrayList<>(); // list after delete un wanted recipes


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
        ArrayList<String> l= choose_for_recipe.cart_list;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recipes");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(search_recipes.this, "error getting data", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, HashMap> o = (HashMap<String, HashMap>) (task.getResult().getValue());
                    ArrayList<HashMap> l1 = new ArrayList<>(); // list of recipes before convert to recipe object
                    for (HashMap h : o.values()) {
                        l1.add(h);
                    }
                    //List<Ingredient> ingredients, String description, String preparationMethod, String imageUrl
                    ArrayList<Recipe> l2 = new ArrayList<>(); // list of recipes after convert
                    for (int i = 0; i < l1.size(); i++) {
                        String id = (String) l1.get(i).get("ownerID");
                        String name = (String) l1.get(i).get("name");
                        ArrayList<HashMap> ingredients = (ArrayList<HashMap>)l1.get(i).get("ingredients");
                        ArrayList<Ingredient> ingredients_ = new ArrayList<Ingredient>();
                        for(int w =0 ;w < ingredients.size();w++){
                            ingredients_.add(new Ingredient((String)ingredients.get(w).get("name"),(String)ingredients.get(w).get("description"),(String)ingredients.get(w).get("category")));
                        }
                        String description = (String) l1.get(i).get("description");
                        String preparation_method = (String) l1.get(i).get("preparationMethod");
                        String image_url = (String) l1.get(i).get("imageUrl");
                        l2.add(new Recipe(id,name,ingredients_,description,preparation_method,image_url));
                    }

                    boolean isAppropriate;
                    for( int j=0;j<l2.size();j++) {
                        ArrayList<String> list_names = new ArrayList<>();
                        for (int a = 0; a < l2.get(j).getIngredients().size(); a++) {
                            list_names.add(l2.get(j).getIngredients().get(a).getName());
                        }
                        isAppropriate = true;
                        for(int k=0;k<l.size();k++){
                            if(!list_names.contains(l.get(k))){
                                isAppropriate = false;
                                break;
                            }
                        }
                        if(isAppropriate){
                            //l3.add(l2.get(j));
                            mUploads.add(l2.get(j));
                        }
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        });

/*
        mDBListener = usersDBRef.child(user).child("MyRecipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUploads.clear();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    String recipeKey = postSnapShot.getValue(String.class);
                    recipeKeyKey = postSnapShot.getKey();

                    assembleRecipe(recipeKey);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(search_recipes.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
            }

        });
        */
    }
    /*
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
                        Toast.makeText(search_recipes.this, "We are sorry, something went wrong!", Toast.LENGTH_SHORT).show();
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
    */
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(search_recipes.this, ShowRecipe.class);
        Recipe selectedItem = mUploads.get(position);
        intent.putExtra("recipe", selectedItem);
        startActivity(intent);
    }

    @Override
    public void onShowRecipeClick(int position) {
        Toast.makeText(this, "show click at  position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        if (this.recipeKeyKey == null) {
            Toast.makeText(this, "We are sorry something went wrong", Toast.LENGTH_SHORT).show();
            return;
        }
        Recipe selectedItem = mUploads.get(position);
        String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                recipesDBRef.child(selectedKey).removeValue();
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                usersDBRef.child(user).child("MyRecipes").child(recipeKeyKey).removeValue();
                Toast.makeText(search_recipes.this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
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
}
