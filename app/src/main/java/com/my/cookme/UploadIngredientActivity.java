package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayoutManager;
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
import com.my.cookme.Adapters.IngredientsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadIngredientActivity extends AppCompatActivity
        implements IngredientsAdapter.OnIngredientClickListener {

    private AutoCompleteTextView editTextName;
    private EditText editTextCategory;
    private Button buttonAdd;
    private RecyclerView recyclerView;

    private DatabaseReference updateDBRef;
    private DatabaseReference ingredientDBRef;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private ArrayList<Pair<String, String>> addIngredientsList;
    private boolean firstTime;
    private IngredientsAdapter rAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ingredient);

        this.addIngredientsList = new ArrayList<>();

        initialize();
        rAdapter = new IngredientsAdapter(UploadIngredientActivity.this);
        rAdapter.setOnIngredientClickListener(this);


        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIngredientData();
            }
        });



        showNeededUpdates();
    }

    private void initialize() {
        initializeXmlElements();
        initializeMenu();
        initializeDatabaseObjects();
    }

    private void initializeDatabaseObjects() {
        this.updateDBRef = FirebaseDatabase.getInstance().getReference().child("Update");
        this.ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
    }

    private void initializeMenu() {
        this.drawerLayout = findViewById(R.id.drawer_layout);
        this.navigationView = findViewById(R.id.nav_view);
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initializeXmlElements() {
        this.recyclerView = findViewById(R.id.recycler_view_ingredients);
        this.editTextName = findViewById(R.id.editTextIngredientName);
        this.editTextCategory = findViewById(R.id.editTextCategory);
        this.buttonAdd = findViewById(R.id.ButtonAddIngredient);
    }


    private void insertIngredientData() {
        String name = editTextName.getText().toString();
        String category = editTextCategory.getText().toString();
        Ingredient i = new Ingredient(name, null, category);
        ingredientDBRef.push().setValue(i).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                deleteUpdateIngredient(name);
                Toast.makeText(UploadIngredientActivity.this, "UPLOADED", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showNeededUpdates() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        editTextName.setAdapter(adapter);
        firstTime = true;

        updateDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addIngredientsList.clear();
                adapter.clear();
                Toast.makeText(UploadIngredientActivity.this, "UPDATED", Toast.LENGTH_SHORT).show();
                for (DataSnapshot postSnapShot : snapshot.getChildren()) {
                    String ingredientValue = postSnapShot.getValue(String.class);
                    String ingredientKey = postSnapShot.getKey();
                    Pair<String, String> pair = new Pair<>(ingredientKey, ingredientValue);
                    addIngredientsList.add(pair);
                    adapter.add(pair.second);
                }
                if (firstTime)
                    setAdapter(rAdapter);
                else
                    rAdapter.setIngredients(addIngredientsList);

                firstTime = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadIngredientActivity.this, "We are sorry, something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAdapter(IngredientsAdapter adapter) {
        adapter.setIngredients(this.addIngredientsList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new FlexboxLayoutManager(this));
    }

    @Override
    public void onItemClick(int position) {
        deleteUpdateIngredient(addIngredientsList.get(position).second);
    }

    private ArrayList<Pair<String, String>> getIngredientsByTheSameName(String ingredient) {
        ArrayList<Pair<String, String>> temp = new ArrayList<>();
        for (int i = 0; i < addIngredientsList.size(); i++) {
            if (ingredient.equals(addIngredientsList.get(i).second)) {
                temp.add(addIngredientsList.get(i));
            }
        }
        return temp;
    }

    private void deleteUpdateIngredient(String ingredient) {
        ArrayList<Pair<String, String>> temp = getIngredientsByTheSameName(ingredient);
        for (int i = 0; i < temp.size(); i++) {
            updateDBRef.child(temp.get(i).first).removeValue();
        }
    }


}