package com.my.cookme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.MediaRouteButton;
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
    private String uniqueKey;
    public static boolean isAdmin=false;
    DatabaseReference recipeDBRef;
    DatabaseReference ingredientDBRef;
    DatabaseReference needToUpdate;
    DatabaseReference usersDBRef;
    DatabaseReference adminsDbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Button buttonAddIngredient;
        recipeDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        needToUpdate = FirebaseDatabase.getInstance().getReference().child("Update");
        usersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        adminsDbRef = FirebaseDatabase.getInstance().getReference().child("Admins");

        this.editTextName = findViewById(R.id.editTextRecipeName);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextIngredients = findViewById(R.id.editTextIngredients);
        this.editTextPreparationMethod = findViewById(R.id.editTextPreparationMethod);
        this.buttonInsertData = findViewById(R.id.buttonInsertData);
        this.buttonAddIngredient = findViewById(R.id.buttonGoAdd);
        this.uniqueKey = null;
        //this.buttonAddIngredient.setVisibility(View.INVISIBLE);
        //if (!isAdmin())

        buttonAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadRecipeActivity.this, UploadIngredientActivity.class);
                startActivity(intent);
            }
        });

        this.ingredients_db = new ArrayList<>();
        ingredientDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredients_db.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap<String, String> object = (HashMap<String, String>) dataSnapshot.getValue();
                    Ingredient i = new Ingredient(object.get("name"), null, object.get("category"));
                    ingredients_db.add(i);
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
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        // Try to do what yoel tried
        adminsDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //counter=0;
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    ArrayList<String> objectHashMap = (ArrayList<String>) task.getResult().getValue();
                    boolean flag=true;
                    for(int i=1;i<objectHashMap.size();i++){
                        Toast.makeText(UploadRecipeActivity.this, "a", Toast.LENGTH_LONG).show();
                        if(user.equals(objectHashMap.get(i))){
                            flag=false;
                        }
                    }
                    if(flag){
                        buttonAddIngredient.setVisibility(View.GONE);
                    }
                }
            }

        });

    }

    private void insertRecipeData() {

        String recipeName = this.editTextName.getText().toString();
        String description = this.editTextDescription.getText().toString();
        String ingredients = this.editTextIngredients.getText().toString();
        String preparationMethod = this.editTextPreparationMethod.getText().toString();
        String[] lines = ingredients.split("\n");
        List<Ingredient> ingredientList = new ArrayList<>();

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

        recipeDBRef.push().setValue(recipe, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                uniqueKey = databaseReference.getKey();
                //Toast.makeText(UploadRecipeActivity.this, uniqueKey, Toast.LENGTH_LONG).show();
                String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                if (uniqueKey != null)
                    usersDBRef.child(user).child("MyRecipes").push().setValue(uniqueKey);
            }
        });
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