package com.my.cookme;


public class Ingredient {
    private String name;
    private float amount;
    private String unitsOfMeasurement;
    private String category;
    private String description;

    public Ingredient(String name, float amount, String unitsOfMeasurement, String category, String description) {
        this.name = name;
        this.amount = amount;
        this.unitsOfMeasurement = unitsOfMeasurement;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getUnitsOfMeasurement() {
        return unitsOfMeasurement;
    }

    public void setUnitsOfMeasurement(String unitsOfMeasurement) {
        this.unitsOfMeasurement = unitsOfMeasurement;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
