package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.szantog.recipebook.RecipeTools;
import com.example.szantog.recipebook.models.RecipeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SearchSimilarityAdapter2 extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<RecipeItem> bestRecipes;

    public SearchSimilarityAdapter2(Context context, String searchText, ArrayList<RecipeItem> recipes) {
        this.context = context;

        ArrayList<RecipeItem> selectedRecipes = new ArrayList<>();
        int count = recipes.size();
        Float[][] matrix = new Float[count][2];
        for (int i = 0; i < count; i++) {
            matrix[i][1] = Float.parseFloat(String.valueOf(i));
            matrix[i][0] = RecipeTools.compareStrings(searchText.toLowerCase(), recipes.get(i).getName().toLowerCase());
        }
        Arrays.sort(matrix, new Comparator<Float[]>() {
            @Override
            public int compare(Float[] a, Float[] b) {
                return b[0].compareTo(a[0]);
            }
        });
        if (matrix[0][1] > 3.0) {
            selectedRecipes.add(recipes.get((int) (float) matrix[0][1]));
        }
        if (matrix[1][1] > 3.0) {
            selectedRecipes.add(recipes.get((int) (float) matrix[1][1]));
        }
        if (matrix[2][1] > 3.0) {
            selectedRecipes.add(recipes.get((int) (float) matrix[2][1]));
        }
        if (selectedRecipes.size() == 0) {
            String[] empty = new String[1];
            empty[0] = "";
            selectedRecipes.add(new RecipeItem("", "Nincs találat!", "", "", false, empty, ""));
        }
        this.bestRecipes = selectedRecipes;
    }


    @Override
    public int getGroupCount() {
        return bestRecipes.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 2;
    }

    @Override
    public Object getGroup(int i) {
        return bestRecipes.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return null;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int pos, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        view.setBackgroundColor(Color.parseColor("#BBBBAA"));
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(bestRecipes.get(pos).getName());
        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        TextView textView = view.findViewById(android.R.id.text1);
        if (childPos == 0) {
            String ingredientList = "Hozzávalók: \n- ";
            for (int k = 0; k < bestRecipes.get(groupPos).getIngredients().length; k++) {
                if (k == bestRecipes.get(groupPos).getIngredients().length - 1) {
                    ingredientList += bestRecipes.get(groupPos).getIngredients()[k];
                } else {
                    ingredientList += bestRecipes.get(groupPos).getIngredients()[k] + "\n- ";
                }
            }
            textView.setText(ingredientList);
        } else {
            textView.setText("Leírás:\n" + bestRecipes.get(groupPos).getDescription());
        }
        return view;
    }



    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
