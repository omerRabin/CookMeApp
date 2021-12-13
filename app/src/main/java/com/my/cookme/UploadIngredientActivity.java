package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UploadIngredientActivity extends AppCompatActivity {
    
    private EditText editTextName;
    private EditText editTextCategory;
    private Button buttonAdd;
    private TextView textViewShowUpdates;

    private DatabaseReference updateDBRef;
    private DatabaseReference ingredientDBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ingredient);
        
        this.editTextName = findViewById(R.id.editTextIngredientName);
        this.editTextCategory = findViewById(R.id.editTextCategory);
        this.updateDBRef = FirebaseDatabase.getInstance().getReference().child("Update");
        this.ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        this.buttonAdd = findViewById(R.id.ButtonAddIngredient);
        
        this.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertIngredientData();
            }
        });


        showNeededUpdates();
    }

    private void insertIngredientData() {
        String name = editTextName.getText().toString();
        String category = editTextCategory.getText().toString();
        Ingredient i = new Ingredient(name, null, category);
        ingredientDBRef.push().setValue(i);
        Toast.makeText(UploadIngredientActivity.this, "UPLOADED", Toast.LENGTH_SHORT).show();
    }

    private void showNeededUpdates() {
        this.textViewShowUpdates = findViewById(R.id.showIngredientsTxt);
        updateDBRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String content = "";
                    HashMap<String, String> objectHashMap = (HashMap<String, String>) task.getResult().getValue();
                    for (String obj : objectHashMap.values()) {
                        content += obj + "\n";
                    }
                    textViewShowUpdates.setText(content);
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
}