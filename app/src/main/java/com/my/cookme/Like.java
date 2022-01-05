package com.my.cookme;

import java.io.Serializable;

public class Like implements Serializable {

    private String owner;
    private String recipeKey;

    public Like(String owner, String recipeKey) {
        this.owner = owner;
        this.recipeKey = recipeKey;
    }

    public Like() {
        this.owner = null;
        this.recipeKey = null;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRecipeKey() {
        return recipeKey;
    }

    public void setRecipeKey(String recipeKey) {
        this.recipeKey = recipeKey;
    }
}
