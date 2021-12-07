package com.my.cookme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;

public class choose_for_recipe extends AppCompatActivity {

    AutoCompleteTextView suggestion_box;
    Spinner items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_for_recipe);
        ArrayList<String> foods = new ArrayList<>();
        suggestion_box = (AutoCompleteTextView)findViewById(R.id.suggestion_box);
        items = (Spinner) findViewById(R.id.actionDownUp);

        foods.add("apple");
        foods.add("banana");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(choose_for_recipe.this, android.R.layout.simple_spinner_dropdown_item,foods);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(choose_for_recipe.this, android.R.layout.simple_spinner_dropdown_item,foods);
        suggestion_box.setAdapter(adapter);
        items.setAdapter(adapter1);

    }


    public void show_vegetables_fruits(View view){
    }

}