package com.my.cookme;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Recipe {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private String recipeID;
    private String ownerID;
    private String name;
    private int likesNumber;
    private String uploadDate;
    private List<Ingredient> ingredients;

    public Recipe(String ownerID, String name, List<Ingredient> ingredients) {
        this.ownerID = ownerID;
        this.name = name;
        this.recipeID = "123456798";
        this.likesNumber = 0;
        Date date = new Date();
        this.uploadDate = formatter.format(date);
        this.ingredients = ingredients;
    }

    public static SimpleDateFormat getFormatter() {
        return formatter;
    }

    public static void setFormatter(SimpleDateFormat formatter) {
        Recipe.formatter = formatter;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
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

    public int getLikesNumber() {
        return likesNumber;
    }

    public void setLikesNumber(int likesNumber) {
        this.likesNumber = likesNumber;
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
}
