package com.my.cookme;

import com.google.firebase.database.Exclude;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Recipe implements Serializable {
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    //private String recipeID;
    private String ownerID;
    private String name;
    private int likesNumber;
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
        this.likesNumber = 0;
        this.imageUrl = imageUrl;
    }

    public Recipe() {

    }

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String mKey) {
        this.mKey = mKey;
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

    /*public static byte[] serialize(Recipe recipe) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(recipe);
        oos.flush();
        return bos.toByteArray();
        /*ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(recipe);
        return out.toByteArray();
    }

    public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return is.readObject();
    }*/
}
