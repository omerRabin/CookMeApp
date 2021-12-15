package com.my.cookme;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class choose_for_recipe extends AppCompatActivity {
    static int one_time = 0;
    static int choose_state = -1;
    ArrayList<CosmicBody> data = new ArrayList<>();
    ListView myListView;
    Spinner mySpinner;
    ImageButton cart;
    Button choose;
    Button remove;
    ArrayAdapter<CosmicBody> adapter;
    static ArrayList<String> cart_list = new ArrayList<>();
    String[] categories = {"Cetgories", "Vegtables&Fruits", "Meat", "Dairy Products", "Spices", "Cereals and Legums", "Fish"};


    private void initializeViews() {
        mySpinner = findViewById(R.id.mySpinner);
        mySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories));
        myListView = findViewById(R.id.myListView);
        ArrayAdapter<CosmicBody> a = new ArrayAdapter<>(choose_for_recipe.this, android.R.layout.simple_list_item_multiple_choice, getCosmicBodies());
        int size = a.getCount();
        myListView.setAdapter(a);
        data.clear();
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position > 0 && position < categories.length) { // here i will create and insert into a list of ingredients
                    Toast.makeText(choose_for_recipe.this, "selected category exist!", Toast.LENGTH_SHORT).show();
                    getSelectedCategoryData(position);
                    Toast.makeText(choose_for_recipe.this, "selected category exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(choose_for_recipe.this, "selected category doesnt exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (choose_state == 1) { // add to cart
                    CosmicBody x = (CosmicBody) parent.getItemAtPosition(position);
                    if (!cart_list.contains(x.getName())) {
                        cart_list.add(x.getName());
                        Toast.makeText(choose_for_recipe.this, "add to cart", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(choose_for_recipe.this, "ingredient was already selected", Toast.LENGTH_SHORT).show();
                    }

                } else if (choose_state == 0) { // remove from cart
                    Toast.makeText(choose_for_recipe.this, "remove from cart", Toast.LENGTH_SHORT).show();
                    CosmicBody y = (CosmicBody) parent.getItemAtPosition(position);
                    String val = y.getName();
                    cart_list.remove(val);
                }
            }
        });

        choose = findViewById(R.id.add_button);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_state = 1;
            }
        });

        remove = findViewById(R.id.remove_button);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_state = 0;
            }
        });

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(choose_for_recipe.this);
                builder.setCancelable(true);
                String pop_up_content = "Selected Ingredients :" + String.join(", ", choose_for_recipe.cart_list);
                builder.setTitle("Your Cart");
                builder.setMessage(pop_up_content);
                builder.setNegativeButton("exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    private ArrayList<CosmicBody> getCosmicBodies() {
        if (one_time == 0) {
            Log.d("TAG", "Before attaching the listener!");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Ingredients");
            reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                        Toast.makeText(choose_for_recipe.this, "tzumi", Toast.LENGTH_SHORT).show();

                    } else {

                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        //Toast.makeText(choose_for_recipe.this, "yoel", Toast.LENGTH_SHORT).show();
                        HashMap<String, Object> o = (HashMap<String, Object>) (task.getResult().getValue());
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        Object first = o.get("-MqQpbvASutrrPdMWgCr");
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        HashMap<String, String> second = (HashMap<String, String>) (first);
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));

                        ArrayList<Object> l1 = new ArrayList<>();
                        for (Object obj : o.values()) {
                            l1.add(obj);
                        }
                        ArrayList<HashMap<String, String>> l1_convert = new ArrayList<>();
                        for (Object x : l1) {
                            l1_convert.add((HashMap<String, String>) x);
                        }

                        HashMap<String, Boolean> h = new HashMap<>();

                        for (int i = 0; i < l1_convert.size(); i++) {
                            String name = l1_convert.get(i).get("name");
                            String category = l1_convert.get(i).get("category");

                            // if(h.get(name) == null){
                            //  h.put(name,true);
                            //}

                            //else if(h.get(name)){
                            //     continue;
                            //}
                            data.add(new CosmicBody(name, category));
                            //Toast.makeText(choose_for_recipe.this, "omer", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
        one_time++;
        return data;
    }

    private void getSelectedCategoryData(int categoryID) {
        ArrayList<CosmicBody> cosmicBodies = new ArrayList<>();
        cosmicBodies.clear();
        if (categoryID == 0) {
            adapter = new ArrayAdapter<>(choose_for_recipe.this, android.R.layout.simple_list_item_1, getCosmicBodies());
        } else {
            for (CosmicBody cosmicBody : getCosmicBodies()) {
                if (cosmicBody.getCategoryID() == categoryID) {
                    Toast.makeText(choose_for_recipe.this, "selected category exist!", Toast.LENGTH_SHORT).show();
                    cosmicBodies.add(cosmicBody);
                }
            }

            adapter = new ArrayAdapter<>(choose_for_recipe.this, android.R.layout.simple_list_item_1, cosmicBodies);
        }
        myListView.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_for_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeViews();
    }
}
