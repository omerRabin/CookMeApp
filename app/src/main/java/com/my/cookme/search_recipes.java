package com.my.cookme;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class search_recipes extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> s1=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_recipes);
        recyclerView = findViewById(R.id.MyRecycler);
        ArrayList<String> l= choose_for_recipe.cart_list;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Recipes");
        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                    Toast.makeText(search_recipes.this, "tzumi", Toast.LENGTH_SHORT).show();
                }
                else{
                    HashMap<String, Object> o = (HashMap<String, Object>) (task.getResult().getValue());
                    ArrayList<Object> l1 = new ArrayList<>(); // list of recipes before convert to recipe object
                    for (Object obj : o.values()) {
                        l1.add(obj);
                    }
                    ArrayList<Recipe> l2= new ArrayList<>(); // list of recipes after convert
                    for(int i=0; i< l1.size();i++){
                        l2.add((Recipe)l1.get(i));
                    }
                    ArrayList<Recipe> l3=new ArrayList<>(); // list after delete un wanted recipes
                    for(int j=0;j<l2.size();j++){
                        for(int k=0;k<l.size();k++){
                            if(l2.get(j).getIngredients().contains(l.get(k))){
                                l3.add(l2.get(j));
                            }
                        }
                    }
                    for(int x=0; x< l3.size();x++) {
                        s1.add(l3.get(x).getName());
                    }
                    //MyAdapter myAdapter = new MyAdapter(this, s1);
                }
            }
        });

    }
}//7:58
