package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class UploadRecipeActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextIngredients;
    private EditText editTextPreparationMethod;
    private Button buttonInsertData;
    private Button buttonAddIngredient;
    private ArrayList<Ingredient> ingredients_db;

    DatabaseReference recipeDBRef;
    DatabaseReference ingredientDBRef;
    DatabaseReference needToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        recipeDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        needToUpdate = FirebaseDatabase.getInstance().getReference().child("Update");

        this.editTextName = findViewById(R.id.editTextRecipeName);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextIngredients = findViewById(R.id.editTextIngredients);
        this.editTextPreparationMethod = findViewById(R.id.editTextPreparationMethod);
        this.buttonInsertData = findViewById(R.id.buttonInsertData);
        this.buttonAddIngredient = findViewById(R.id.buttonGoAdd);

        if (!isAdmin())
            this.buttonAddIngredient.setVisibility(View.GONE);

        this.buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadRecipeActivity.this, UploadIngredientActivity.class);
                startActivity(intent);
                finish();
            }
        });

        this.ingredients_db = new ArrayList<>();
        ingredientDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredients_db.clear();
                Toast.makeText(UploadRecipeActivity.this, "GOOD", Toast.LENGTH_SHORT).show();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap<String, String> object = (HashMap<String, String>) dataSnapshot.getValue();
                    Ingredient i = new Ingredient(object.get("name"), null, object.get("category"));
                    ingredients_db.add(i);
                    Toast.makeText(UploadRecipeActivity.this, i.getName(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadRecipeActivity.this, "Something went wrong! Please try again later...", Toast.LENGTH_SHORT).show();
            }
        });

        this.buttonInsertData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertRecipeData();
            }
        });
    }

    public static boolean isAdmin() {
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        return user.equals("yoel2810@gmail.com") || user.equals("omerrabin1289@gmail.com") || user.equals("amittzumi@hotmail.com");
    }

    private void insertRecipeData() {

        String recipeName = this.editTextName.getText().toString();
        String description = this.editTextDescription.getText().toString();
        String ingredients = this.editTextIngredients.getText().toString();
        String preparationMethod = this.editTextPreparationMethod.getText().toString();
        String[] lines = ingredients.split("\n");
        List<Ingredient> ingredientList = new ArrayList<>();
        Toast.makeText(UploadRecipeActivity.this, ingredients_db.size() + "", Toast.LENGTH_SHORT).show();

        ArrayList<String> missingIngredients = new ArrayList<>();


        for (String line : lines) {
            boolean flag = false;
            for (Ingredient ing : ingredients_db) {
                if (line.toLowerCase().contains(ing.getName())) {
                    ingredientList.add(new Ingredient(ing.getName(), line, ing.getCategory()));
                    flag = true;
                }
            }
            if (flag == false) {
                missingIngredients.add(line);
                ingredientList.add(new Ingredient(line, line, "Unknown"));
            }
        }

        if (missingIngredients.size() > 0)
            showPopupIngredient(missingIngredients);

        Recipe recipe = new Recipe(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                recipeName, ingredientList, description, preparationMethod);

        recipeDBRef.push().setValue(recipe);
    }

    private void showPopupIngredient(List<String> missingIngredients) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadRecipeActivity.this);
        builder.setCancelable(true);
        builder.setTitle("We have noticed some ingredients doesn't show up on our database!");
        String popupContent = missingIngredients.get(0) + "\n";
        for (int i = 1; i < missingIngredients.size(); i++) {
            popupContent += missingIngredients.get(i);
            if (i != missingIngredients.size() - 1)
                popupContent += "\n";
        }
        builder.setMessage("The following lines describe ingredients that do not exist in our database:\n\n" +
                popupContent + "\n\nPlease check if you don't have a typo.\nIf you are sure everything is ok help us to update out database. ");

        builder.setNegativeButton("cancel, let me fix", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final String content = popupContent;
        builder.setPositiveButton("ok, send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(UploadRecipeActivity.this, "A request to update our database has been sent to one of our admins", Toast.LENGTH_LONG).show();
                sendUpdate(missingIngredients);
                //relativeLayoutPopup.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    private void sendUpdate(List<String> missingIngredients) {
        //need to send an email or a msg or something to an admin
        for (String s : missingIngredients)
            needToUpdate.push().setValue(s);
    }
}
