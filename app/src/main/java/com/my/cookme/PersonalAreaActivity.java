package com.my.cookme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class PersonalAreaActivity extends AppCompatActivity {

    private Button buttonSensitivity;
    private Button buttonMyRecipes;
    private Button buttonFavorites;
    ArrayList<String> data = new ArrayList<>();
    AlertDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_area);
        getSupportActionBar().setTitle("CookMe");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.buttonFavorites = findViewById(R.id.buttonFavorite);
        this.buttonMyRecipes = findViewById(R.id.buttonMyRecipes);
        this.buttonSensitivity = findViewById(R.id.buttonSensitivity);

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
                    Toast.makeText(PersonalAreaActivity.this, "tzumi", Toast.LENGTH_SHORT).show();

                } else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    //Toast.makeText(choose_for_recipe.this, "yoel", Toast.LENGTH_SHORT).show();
                    HashMap<String, Object> o = (HashMap<String, Object>) (task.getResult().getValue());
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

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
                }
                Toast.makeText(PersonalAreaActivity.this,sb.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        myDialog=myBuilder.create();
        myDialog.show();
    }
}