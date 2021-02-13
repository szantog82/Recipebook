package com.example.szantog.recipebook.models;


import android.support.annotation.NonNull;

public class RecipeItem implements Comparable<RecipeItem> {

    private String _id;
    private String name;
    private String season;
    private String type;
    private boolean containsDiary;
    private String[] ingredients;
    private String description;

    public RecipeItem(String id, String name, String season, String type, boolean containsDiary, String[] ingredients, String description) {
        this._id = id;
        this.name = name;
        this.season = season;
        this.type = type;
        this.containsDiary = containsDiary;
        this.ingredients = ingredients;
        this.description = description;
    }

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public String getSeason() {
        return season;
    }

    public String getType() {
        return type;
    }

    public boolean isContainsDiary() {
        return containsDiary;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public String getIngredientsAsString() {
        String ingredientList = "- ";
        for (int k = 0; k < ingredients.length; k++) {
            if (k == ingredients.length - 1) {
                ingredientList += ingredients[k];
            } else {
                ingredientList += ingredients[k] + "\n- ";
            }
        }
        return ingredientList;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(@NonNull RecipeItem recipeItem) {
        return this.getName().compareTo(recipeItem.getName());
    }
}