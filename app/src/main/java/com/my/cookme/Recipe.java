package com.my.cookme;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Recipe implements Serializable {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    //private String recipeID;
    private String ownerID;
    private String name;
    private List<Like> likes;
    private String uploadDate;
    private List<Ingredient> ingredients;
    private String description;
    private String preparationMethod;
    private String imageUrl;
    private String mKey;

    public Recipe(String ownerID, String name, List<Ingredient> ingredients, String description, String preparationMethod, String imageUrl) {
        this.ownerID = ownerID;
        this.name = name;
        this.ingredients = ingredients;
        this.description = description;
        this.preparationMethod = preparationMethod;
        Date date = new Date();
        this.uploadDate = formatter.format(date);
        this.likes = new ArrayList<>();
        this.likes.add(new Like(ownerID, "12"));
        this.imageUrl = imageUrl;
    }

    public Recipe() {
        this.likes = new ArrayList<>();
    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    @Exclude
    public int getLikesNumber() {
        return this.likes.size();
    }

    @Exclude
    public void addLike(Like like) {
        this.likes.add(like);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Recipes").child(like.getRecipeKey()).child("likes");
        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                databaseReference.setValue(likes);
                String username = FirebaseAuth.getInstance().getCurrentUser().getEmail().split("@")[0];
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("likedRecipes");
                databaseReference1.push().setValue(like);
            }
        });
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static SimpleDateFormat getFormatter() {
        return formatter;
    }

    public static void setFormatter(SimpleDateFormat formatter) {
        Recipe.formatter = formatter;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likesNumber) {
        this.likes = likesNumber;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreparationMethod() {
        return preparationMethod;
    }

    public void setPreparationMethod(String preparationMethod) {
        this.preparationMethod = preparationMethod;
    }
}
