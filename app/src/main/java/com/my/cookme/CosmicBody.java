package com.my.cookme;
public class CosmicBody {
    private String name;
    private String category;
    private int categoryID;
    private boolean is_chosen;

    public String getName() {
        return name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public CosmicBody(String name, String category,boolean is_chosen) {
        this.name = name;
        this.is_chosen = is_chosen;
        if(category.equals("Vegtables&Fruits"))
        {
            this.categoryID = 1;
        }
        else if(category.equals("Meat")){
            this.categoryID = 2;
        }
        else if(category.equals("Dairy Products")){
            this.categoryID =3;
        }
        else if(category.equals("Spices")){
            this.categoryID =4;
        }
        else if (category.equals("Cereals and Legums")){
            this.categoryID =5;
        }
        else if(category.equals("Fish")){
            this.categoryID =6;
        }
    }
    public void setActive(boolean b){
        this.is_chosen =b;
    }
    public boolean isActive(){
        return this.is_chosen;
    }
    @Override
    public String toString(){
        return name;
    }

}