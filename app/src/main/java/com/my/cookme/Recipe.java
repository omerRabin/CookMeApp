package com.my.cookme;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Recipe {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    //private String recipeID;
    private String ownerID;
    private String name;
    private int likesNumber;
    private String uploadDate;
    private List<Ingredient> ingredients;
    private String description;
    private String preparationMethod;

    public Recipe(String ownerID, String name, List<Ingredient> ingredients, String description, String preparationMethod) {
        this.ownerID = ownerID;
        this.name = name;
        this.ingredients = ingredients;
        this.description = description;
        this.preparationMethod = preparationMethod;
        Date date = new Date();
        this.uploadDate = formatter.format(date);
        this.likesNumber = 0;
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
