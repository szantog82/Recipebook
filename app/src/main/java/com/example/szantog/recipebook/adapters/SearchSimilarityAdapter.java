package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.RecipeTools;
import com.example.szantog.recipebook.models.RecipeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class SearchSimilarityAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RecipeItem> bestRecipes;
    private Boolean noHitsFound = false;

    public SearchSimilarityAdapter(Context context, String searchText, ArrayList<RecipeItem> recipes) {
        this.context = context;

        ArrayList<RecipeItem> selectedRecipes = new ArrayList<>();
        int count = recipes.size();
        if (count > 0) {
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
                noHitsFound = true;
                String[] empty = new String[1];
                empty[0] = "";
                selectedRecipes.add(new RecipeItem("", "Nincs találat!", "", "", false, empty, ""));
            }
            this.bestRecipes = selectedRecipes;
        } else {
            this.bestRecipes = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return bestRecipes.size();
    }

    @Override
    public Object getItem(int i) {

        if (noHitsFound) {
            return null;
        } else {
            return bestRecipes.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.groupitem_layout, null);
        }
        ImageView img = view.findViewById(R.id.groupitem_image);
        if (bestRecipes.get(pos).getName().contains("saláta"))
            img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.salad64));
        else if (bestRecipes.get(pos).getName().contains("leves"))
            img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.soup64));
        else img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.meal64));
        TextView textView = view.findViewById(R.id.groupitem_text);
        textView.setText(bestRecipes.get(pos).getName());
        return view;
    }
}
