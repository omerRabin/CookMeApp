package com.my.cookme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class PersonalAreaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button buttonSensitivity;
    private Button buttonMyRecipes;
    private Button buttonFavorites;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    DatabaseReference sensitivities;
    ArrayList<String> data = new ArrayList<>();
    AlertDialog myDialog;
    ArrayList<String> sensitivities_list=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_area);
        this.buttonFavorites = findViewById(R.id.buttonFavorite);
        this.buttonMyRecipes = findViewById(R.id.buttonMyRecipes);
        this.buttonSensitivity = findViewById(R.id.buttonSensitivity);
        initializeMenu();

        this.buttonFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalAreaActivity.this, FavoriteRecipesActivity.class);
                startActivity(intent);
            }
        });

        this.buttonMyRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PersonalAreaActivity.this, MyRecipesActivity.class);
                startActivity(intent);
            }
        });
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Ingredients");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(PersonalAreaActivity.this, "error getting data", Toast.LENGTH_SHORT).show();

                } else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    HashMap<String, Object> o = (HashMap<String, Object>) (task.getResult().getValue());

                    ArrayList<Object> l1 = new ArrayList<>();
                    for (Object obj : o.values()) {
                        l1.add(obj);
                    }
                    ArrayList<HashMap<String, String>> l1_convert = new ArrayList<>();
                    for (Object x : l1) {
                        l1_convert.add((HashMap<String, String>) x);
                    }
                    for (int i = 0; i < l1_convert.size(); i++) {
                        String name = l1_convert.get(i).get("name");
                        data.add(name);
                    }

                }
            }
        });

        Button showBtn = findViewById(R.id.buttonSensitivity);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }

        });
        String user = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String user_ = user.split("@")[0];
        sensitivities=FirebaseDatabase.getInstance().getReference().child("Users").child(user_).child("Sensitivities");
    }
    private void showAlert()
    {
        final ArrayList selectedItems = new ArrayList();
        final CharSequence [] _data = data.toArray(new CharSequence[data.size()]);
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
        myBuilder.setTitle("Sensitivities").setMultiChoiceItems(_data, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if(isChecked){
                    selectedItems.add(_data[which]);
                }
                else if(selectedItems.contains(which)) {
                    selectedItems.remove(Integer.valueOf(which));
                }
            }
        });
        myBuilder.setPositiveButton("Selected Items", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder sb=new StringBuilder();
                for(Object _data:selectedItems){
                    sb.append(_data.toString()+"\n");
                    sensitivities_list.add(_data.toString());
                }
                //Toast.makeText(PersonalAreaActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
                for(int i=0;i<sensitivities_list.size();i++) {
                    sensitivities.push().setValue(sensitivities_list.get(i));
                }
            }
        });
        myDialog=myBuilder.create();
        myDialog.show();
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
        navigationView.setCheckedItem(R.id.nav_profile);
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
                Intent intent = new Intent(PersonalAreaActivity.this, DashboardActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_cookme:
                Intent intent0 = new Intent(PersonalAreaActivity.this, choose_for_recipe.class);
                startActivity(intent0);
                break;
            case R.id.nav_upload_recipe:
                Intent intent1 = new Intent(PersonalAreaActivity.this, UploadRecipeActivity.class);
                startActivity(intent1);
                break;
            case R.id.nav_login:
                Intent intent2 = new Intent(PersonalAreaActivity.this, MainActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_profile:
                break;
            case R.id.nav_logout:
                Intent intent4 = new Intent(PersonalAreaActivity.this, MainActivity.class);
                startActivity(intent4);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}