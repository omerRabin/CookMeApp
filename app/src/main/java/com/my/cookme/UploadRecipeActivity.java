package com.my.cookme;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.MediaRouteButton;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.navigation.NavigationView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class UploadRecipeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextName;
    private EditText editTextDescription;
    private LinearLayout layoutList;
    private Button buttonAdd;
    private EditText editTextPreparationMethod;
    private Button buttonInsertData;
    private Button buttonAddIngredient;
    private ArrayList<Ingredient> ingredients_db;
    private Button buttonChooseImage;
    private ImageView imageView;
    private Uri imageUri;
    private EditText editTextIngredients;
    public static boolean isAdmin = false;


    DatabaseReference recipeDBRef;
    DatabaseReference ingredientDBRef;
    DatabaseReference needToUpdate;
    DatabaseReference usersDBRef;
    DatabaseReference adminsDbRef;

    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recipe);

        initialize();
        editTextIngredients = editTextDescription;

        this.imageUri = null;

        this.buttonAdd.setOnClickListener(this);

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
                    Ingredient i = dataSnapshot.getValue(Ingredient.class);
                    ingredients_db.add(i); // adding them to a list
                }
                buttonAdd.setEnabled(true);
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

    private void initialize() {
        initializeXmlElements();
        initializeMenu();
        initializeDatabaseObjects();
    }

    private void initializeDatabaseObjects() {
        recipeDBRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        ingredientDBRef = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        needToUpdate = FirebaseDatabase.getInstance().getReference().child("Update");
        usersDBRef = FirebaseDatabase.getInstance().getReference().child("Users");
        adminsDbRef = FirebaseDatabase.getInstance().getReference().child("Admins");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        //databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
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
        navigationView.setCheckedItem(R.id.nav_upload_recipe);
    }

    private void initializeXmlElements() {
        this.editTextName = findViewById(R.id.editTextRecipeName);
        this.editTextDescription = findViewById(R.id.editTextDescription);
        this.editTextPreparationMethod = findViewById(R.id.editTextPreparationMethod);
        this.buttonInsertData = findViewById(R.id.buttonInsertData);
        this.buttonAddIngredient = findViewById(R.id.buttonGoAdd);
        this.buttonChooseImage = findViewById(R.id.button_choose_image);
        this.imageView = findViewById(R.id.imageView);
        this.layoutList = findViewById(R.id.layout_list);
        this.buttonAdd = findViewById(R.id.button_add);
    }


    ActivityResultLauncher<String> mgetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        //imageView.setImageURI(result);
                        imageUri = result;
                        Picasso.with(UploadRecipeActivity.this).load(result).into(imageView);
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

                    String recipeName = editTextName.getText().toString();
                    String description = editTextDescription.getText().toString();
                    String ingredients = editTextIngredients.getText().toString();
                    String preparationMethod = editTextPreparationMethod.getText().toString();
                    String[] lines = ingredients.split("\n"); // every line is a different ingredient
                    List<Ingredient> ingredientList = getIngredientsValues();

                    if (ingredientList == null) {
                        Toast.makeText(UploadRecipeActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayList<String> missingIngredients = new ArrayList<>();

                    fileReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            String url = task.getResult().toString();
                            Recipe recipe = new Recipe(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                    recipeName, ingredientList, description, preparationMethod, url); // creating a new recipe


                            for (Ingredient ingredient : ingredientList) {
                                if (ingredient.getName().equals("other"))
                                    missingIngredients.add(ingredient.getDescription());
                            }
                            pushRecipe(recipe);
                            pushMissingIngredients(missingIngredients);

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


    private void showPopup(String content, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadRecipeActivity.this); // creating a popup dialog
        builder.setCancelable(false);
        builder.setTitle(title);

        builder.setMessage(content);

        builder.setPositiveButton("ok, I understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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

    private void pushMissingIngredients(List<String> list) {
        for (String ingredient : list) {
            needToUpdate.push().setValue(ingredient);
        }
    }

    private void sendUpdate(List<String> missingIngredients) {
        //need to send an email or a msg or something to an admin
        for (String s : missingIngredients)
            needToUpdate.push().setValue(s);
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

        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent0 = new Intent(UploadRecipeActivity.this, DashboardActivity.class);
                startActivity(intent0);
                break;
            case R.id.nav_cookme:
                Intent intent = new Intent(UploadRecipeActivity.this, choose_for_recipe.class);
                startActivity(intent);
                break;
            case R.id.nav_upload_recipe:
                break;
            case R.id.nav_login:
            case R.id.nav_logout:
                Intent intent2 = new Intent(UploadRecipeActivity.this, MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_profile:
                Intent intent3 = new Intent(UploadRecipeActivity.this, PersonalAreaActivity.class);
                startActivity(intent3);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        addView();
    }

    private void addView() {
        View ingredientView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ingredientView.findViewById(R.id.spinner_team);
        ImageView imageClose = (ImageView) ingredientView.findViewById(R.id.image_remove);
        layoutList.addView(ingredientView);
        ArrayList<String> arrayList = new ArrayList<>();
        for (Ingredient i : ingredients_db) {
            arrayList.add(i.getName());
        }
        arrayList.add("other");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayList);
        autoCompleteTextView.setAdapter(arrayAdapter);
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(ingredientView);
            }
        });

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (arrayAdapter.getCount() < 1) {
                        autoCompleteTextView.setError("No such ingredient");
                        showPopup(autoCompleteTextView.getText().toString() + " does not exist in our database." +
                                "\nPlease make sure that you typed everything right." +
                                "\nIf your ingredient does not exist in our database, choose \"other\" as ingredient and write your ingredient in the \"description\"." +
                                "\n a request will be sent to our admin to add the ingredient to our database.", "Ingredient missing");
                        autoCompleteTextView.setText("");
                    } else {
                        if (((String) arrayAdapter.getItem(0)).equals(autoCompleteTextView.getText().toString()) == false) {
                            autoCompleteTextView.setError("No such ingredient");
                            showPopup(autoCompleteTextView.getText().toString() + " does not exist in our database." +
                                    "\nPlease make sure that you typed everything right." +
                                    "\nIf your ingredient does not exist in our database, choose \"other\" as ingredient and write your ingredient in the \"description\"." +
                                    "\n a request will be sent to our admin to add the ingredient to our database.", "Ingredient missing");
                            autoCompleteTextView.setText("");
                        }
                    }
                }
            }
        });
    }

    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private ArrayList<Ingredient> getIngredientsValues() {
        ArrayList<Ingredient> stringArrayList = new ArrayList<>();
        for (int i = 0; i < layoutList.getChildCount(); i++) {
            View ingredientView = layoutList.getChildAt(i);
            EditText editTextName = (EditText) ingredientView.findViewById(R.id.edit_ingredient_name);
            AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) ingredientView.findViewById(R.id.spinner_team);

            Ingredient ingredient = new Ingredient(null, null, null);

            if (!autoCompleteTextView.getText().toString().equals("")) {
                ingredient.setName(autoCompleteTextView.getText().toString());
            } else {
                Toast.makeText(this, "We are sorry, something went wrong with the ingredients", Toast.LENGTH_SHORT).show();
                return null;
            }

            if (!editTextName.getText().toString().equals("")) {
                ingredient.setDescription(editTextName.getText().toString());
            }

            String category = findCategoryByIngredientName(ingredient.getName());
            if (category == null) {
                Toast.makeText(this, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show();
                return null;
            } else {
                ingredient.setCategory(category);
            }
            stringArrayList.add(ingredient);
        }
        return stringArrayList;
    }

    private String findCategoryByIngredientName(String name) {
        if (name.equals("other"))
            return "other";

        for (Ingredient ing : ingredients_db) {
            if (ing.getName().equals(name))
                return ing.getCategory();
        }
        return null;
    }
}