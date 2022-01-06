package com.my.cookme;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.my.cookme.Adapters.IngredientsAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class choose_for_recipe extends AppCompatActivity
        implements IngredientsAdapter.OnIngredientClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private IngredientsAdapter rAdapter;
    private RecyclerView recyclerView;
    private AutoCompleteTextView autoCompleteTextView;
    private boolean firstTime;
    private DatabaseReference ingredientsDBRef;
    private Button buttonAdd;
    private Button buttonSearch;

    private HashMap<String, CheckBox> checkBoxes;

    static ArrayList<String> cart_list = new ArrayList<>();
    private ArrayList<Pair<String, String>> cart_list_ingredients;
    private ArrayList<Pair<String, String>> allIngredients;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_for_recipe);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.firstTime = true;
        this.cart_list_ingredients = new ArrayList<>();
        this.adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        this.allIngredients = new ArrayList<>();



        initializeXmlElements();
        initializeMenu();

        rAdapter = new IngredientsAdapter(choose_for_recipe.this);
        rAdapter.setOnIngredientClickListener(this);

        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAutoComplete();
            }
        });

        this.ingredientsDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");


        autoCompleteTextView.setAdapter(adapter);




        ingredientsDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allIngredients.clear();
                adapter.clear();
                Toast.makeText(choose_for_recipe.this, "UPDATED", Toast.LENGTH_SHORT).show();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    String ingredientValue = postSnapShot.getValue(Ingredient.class).getName();
                    String ingredientKey = postSnapShot.getKey();
                    Pair<String, String> pair = new Pair<>(ingredientKey, ingredientValue);
                    Ingredient ingredient = postSnapShot.getValue(Ingredient.class);
                    allIngredients.add(new Pair<>(ingredientKey, ingredientValue));
                    adapter.add(ingredient.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(choose_for_recipe.this, "We are sorry, something went wrong", Toast.LENGTH_SHORT).show();
            }
        });


        this.buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(choose_for_recipe.this, search_recipes.class);
                startActivity(intent);
            }
        });

    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkBoxVegetables:
                Toast.makeText(this, "veg", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkBoxMeat:
                Toast.makeText(this, "meat", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkBoxDairy:
                Toast.makeText(this, "dairy", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkBoxSpices:
                Toast.makeText(this, "spices", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkBoxCereals:
                Toast.makeText(this, "cereals", Toast.LENGTH_SHORT).show();
                break;
            case R.id.checkBoxFish:
                Toast.makeText(this, "fish", Toast.LENGTH_SHORT).show();
                break;
        }
    }



    private void initializeXmlElements() {
        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);
        this.recyclerView = findViewById(R.id.recycler_view_ingredients);
        this.autoCompleteTextView = findViewById(R.id.autoComplete);
        this.buttonAdd = findViewById(R.id.buttonAdd);
        this.buttonSearch = findViewById(R.id.buttonSearch);
        checkBoxes = new HashMap<>();
        checkBoxes.put("Vegtables&Fruits", findViewById(R.id.checkBoxVegetables));
        checkBoxes.put("Meat", findViewById(R.id.checkBoxMeat));
        checkBoxes.put("Dairy Products", findViewById(R.id.checkBoxDairy));
        checkBoxes.put("Spices", findViewById(R.id.checkBoxSpices));
        checkBoxes.put("Cereals and Legums", findViewById(R.id.checkBoxCereals));
        checkBoxes.put("Fish", findViewById(R.id.checkBoxFish));
        setAutoComplete();
    }

    private void setAutoComplete() {

        this.autoCompleteTextView.setAdapter(adapter);


        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    checkAutoComplete();
                }
            }
        });


    }

    private void checkAutoComplete() {
        if (adapter.getCount() < 1) {
            autoCompleteTextView.setError("No such ingredient");
            autoCompleteTextView.setText("");
        } else {
            if (((String) adapter.getItem(0)).equals(autoCompleteTextView.getText().toString()) == false) {
                autoCompleteTextView.setError("No such ingredient");
                autoCompleteTextView.setText("");
            }
            else {
                for (int i = 0; i < allIngredients.size(); i++) {
                    if (allIngredients.get(i).second.equals(this.autoCompleteTextView.getText().toString())) {
                        cart_list_ingredients.add(allIngredients.get(i));
                        cart_list.add(allIngredients.get(i).second);
                        Toast.makeText(this, allIngredients.get(i).second, Toast.LENGTH_SHORT).show();
                    }
                    if (firstTime) {
                        setAdapter(rAdapter);
                        firstTime = false;
                    }
                    else
                        rAdapter.setIngredients(allIngredients);
                }
                rAdapter.setIngredients(cart_list_ingredients);
            }
        }
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
        navigationView.setCheckedItem(R.id.nav_cookme);
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
                Intent intent0 = new Intent(choose_for_recipe.this, DashboardActivity.class);
                startActivity(intent0);
                break;
            case R.id.nav_cookme:
                break;
            case R.id.nav_upload_recipe:
                Intent intent1 = new Intent(choose_for_recipe.this, UploadRecipeActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_login:
                Intent intent2 = new Intent(choose_for_recipe.this, MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_profile:
                Intent intent3 = new Intent(choose_for_recipe.this, PersonalAreaActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_logout:
                Intent intent4 = new Intent(choose_for_recipe.this, MainActivity.class);
                startActivity(intent4);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setAdapter(IngredientsAdapter rAdapter) {
        rAdapter.setIngredients(this.cart_list_ingredients);
        recyclerView.setAdapter(rAdapter);
        recyclerView.setLayoutManager(new FlexboxLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        cart_list.remove(position);
        cart_list_ingredients.remove(position);
        setAdapter(rAdapter);
    }

}