package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadIngredientActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextCategory;
    private Button buttonAdd;
    private TextView textViewShowUpdates;
    private Button buttonDelete;

    private DatabaseReference updateDBRef;
    private DatabaseReference ingredientDBRef;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private List<String> autos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ingredient);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        this.autos = new ArrayList<>();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        this.editTextName = findViewById(R.id.editTextIngredientName);
        this.editTextCategory = findViewById(R.id.editTextCategory);
        this.updateDBRef = FirebaseDatabase.getInstance().getReference().child("Update");
        this.ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        this.buttonAdd = findViewById(R.id.ButtonAddIngredient);
        this.buttonDelete = findViewById(R.id.buttondelete);
        this.textViewShowUpdates = findViewById(R.id.showIngredientsTxt);

        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIngredientData();
            }
        });

        this.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewShowUpdates.setText("");
                deleteUpdates();
            }
        });


        showNeededUpdates();
    }

    private void deleteUpdates() {
        updateDBRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() { // deletes
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UploadIngredientActivity.this, "idk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertIngredientData() {
        String name = editTextName.getText().toString();
        String category = editTextCategory.getText().toString();
        Ingredient i = new Ingredient(name, null, category);
        ingredientDBRef.push().setValue(i);
        Toast.makeText(UploadIngredientActivity.this, "UPLOADED", Toast.LENGTH_SHORT).show();
    }

    private void showNeededUpdates() {

        updateDBRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String content = "";
                    HashMap<String, String> objectHashMap = (HashMap<String, String>) task.getResult().getValue(); // get all the missing ingredients

                    if (objectHashMap == null) {
                        textViewShowUpdates.setText("");
                        return;
                    }

                    for (String obj : objectHashMap.values()) {
                        content += obj + "\n";
                        autos.add(obj);
                    }
                    textViewShowUpdates.setText(content);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
}