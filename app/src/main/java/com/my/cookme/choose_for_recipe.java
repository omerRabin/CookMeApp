package com.my.cookme;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class choose_for_recipe extends AppCompatActivity {

    ListView myListView;
    Spinner mySpinner;
    ArrayAdapter<CosmicBody> adapter;
    String[] categories = {"Vegetables&Fruits","Meat","Dairy Products", "Spices", "Cereals and Legums","Fish" };

    private void initializeViews() {
        mySpinner = findViewById(R.id.mySpinner);
        mySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,categories));
        myListView = findViewById(R.id.myListView);
        myListView.setAdapter(new ArrayAdapter<>(choose_for_recipe.this, android.R.layout.simple_list_item_1,getCosmicBodies()));

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position <categories.length) {
                    getSelectedCategoryData(position);
                }
                else{
                    Toast.makeText( choose_for_recipe.this, "selected category doesnt exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ArrayList<CosmicBody> getCosmicBodies() {
        ArrayList<CosmicBody> data = new ArrayList<>();
        data.clear(); // here i need to put the all ingredients from data that i need to pull from fire base
        data.add(new CosmicBody("Dairy Products", 1));
        return data;
    }
    private void getSelectedCategoryData(int categoryID) {
        ArrayList<CosmicBody> cosmicBodies = new ArrayList<>();
        if(categoryID ==0)
        {
            adapter = new ArrayAdapter<>(choose_for_recipe.this, android.R.layout.simple_list_item_1, getCosmicBodies());
        }
        else
        {
            for(CosmicBody cosmicBody : getCosmicBodies()) {
                if(cosmicBody.getCategoryID() == categoryID) {
                    cosmicBodies.add(cosmicBody);
                }
            }
            adapter = new ArrayAdapter<>(choose_for_recipe.this, android.R.layout.simple_list_item_1,cosmicBodies);
        }
        myListView.setAdapter(adapter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_for_recipe);

        initializeViews();
    }
}

class CosmicBody {
    private String name;
    private int categoryID;

    public String getName() {
        return name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public CosmicBody(String name, int categoryID) {
        this.name = name;
        this.categoryID = categoryID;
    }

    @Override
    public String toString(){
        return name;
    }

}