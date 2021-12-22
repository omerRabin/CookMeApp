package com.my.cookme;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.MediaRouteButton;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
//import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class UploadRecipeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextName;
    private EditText editTextDescription;
    private EditText editTextIngredients;
    private EditText editTextPreparationMethod;
    private Button buttonInsertData;
    private Button buttonAddIngredient;
    private ArrayList<Ingredient> ingredients_db;
    private Button buttonChooseImage;
    private ImageView imageView;
    private Uri imageUri;
    public static boolean isAdmin = false;


    DatabaseReference recipeDBRef;
    DatabaseReference ingredientDBRef;
    DatabaseReference needToUpdate;
    DatabaseReference usersDBRef;
    DatabaseReference adminsDbRef;

    StorageReference storageReference;
    DatabaseReference databaseReference;


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

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        this.editTextName = findViewById(R.id.editTextRecipeName);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextIngredients = findViewById(R.id.editTextIngredients);
        this.editTextPreparationMethod = findViewById(R.id.editTextPreparationMethod);
        this.buttonInsertData = findViewById(R.id.buttonInsertData);
        this.buttonAddIngredient = findViewById(R.id.buttonGoAdd);
        this.buttonChooseImage = findViewById(R.id.button_choose_image);
        this.imageView = findViewById(R.id.imageView);
        this.imageUri = null;
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
        ingredientDBRef.addValueEventListener(new ValueEventListener() { // get all the ingredients from the db
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ingredients_db.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HashMap<String, String> object = (HashMap<String, String>) dataSnapshot.getValue();
                    Ingredient i = new Ingredient(object.get("name"), null, object.get("category"));
                    ingredients_db.add(i); // adding them to a list
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
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0]; // gets the current username
        // Try to do what yoel tried
        //######################################################################################################################
        /*adminsDbRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() { // gets all the admins for the db
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                //counter=0;
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {

                    ArrayList<String> admins = (ArrayList<String>) task.getResult().getValue();
                    boolean flag = true;
                    for (int i = 1; i < admins.size(); i++) {
                        //Toast.makeText(UploadRecipeActivity.this, "a", Toast.LENGTH_LONG).show();
                        if (user.equals(admins.get(i))) {
                            flag = false; // if the user is an admin
                        }
                    }
                    if (flag) { // if the user is not an admin
                        buttonAddIngredient.setVisibility(View.GONE);
                    }
                }
            }
        });*/
        //######################################################################################################33


        this.buttonChooseImage.setOnClickListener(v -> mgetContent.launch("image/*"));
    }


    ActivityResultLauncher<String> mgetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        //imageView.setImageURI(result);
                        imageUri = result;
                        //Picasso.with(UploadRecipeActivity.this).load(result).into(imageView);
                    }
                }
            });


    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void insertRecipeData() {
        if (this.imageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()
                    //Toast.makeText(UploadRecipeActivity.this, taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(), Toast.LENGTH_LONG).show();

                    String recipeName = editTextName.getText().toString();
                    String description = editTextDescription.getText().toString();
                    String ingredients = editTextIngredients.getText().toString();
                    String preparationMethod = editTextPreparationMethod.getText().toString();
                    String[] lines = ingredients.split("\n"); // every line is a different ingredient
                    List<Ingredient> ingredientList = new ArrayList<>();

                    ArrayList<String> missingIngredients = new ArrayList<>();


                    for (String line : lines) { // checking if some ingredients do not exist in our db
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

                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            Recipe recipe = new Recipe(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    recipeName, ingredientList, description, preparationMethod, url); // creating a new recipe

                            boolean flag = false;
                            if (missingIngredients.size() > 0) {
                                showPopupIngredient(missingIngredients, recipe);
                                flag = true;
                            }

                            if (flag == false) {
                                pushRecipe(recipe);
                            }

                        }
                    });


                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UploadRecipeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }


    private void showPopupIngredient(List<String> missingIngredients, Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadRecipeActivity.this); // creating a popup dialog
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

                if (recipe != null) {
                    pushRecipe(recipe);
                }

                Toast.makeText(UploadRecipeActivity.this, "A request to update our database has been sent to one of our admins", Toast.LENGTH_LONG).show();
                sendUpdate(missingIngredients);
                //relativeLayoutPopup.setVisibility(View.VISIBLE);
            }
        });
        builder.show();
    }

    private void pushRecipe(Recipe recipe) {
        String uniqueKey = recipeDBRef.push().getKey();
        recipeDBRef.child(uniqueKey).setValue(recipe);
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
        usersDBRef.child(user).child("MyRecipes").push().setValue(uniqueKey);
    }

    private void sendUpdate(List<String> missingIngredients) {
        //need to send an email or a msg or something to an admin
        for (String s : missingIngredients)
            needToUpdate.push().setValue(s);
    }
}