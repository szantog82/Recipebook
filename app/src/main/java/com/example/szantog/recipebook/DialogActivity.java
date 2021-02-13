package com.example.szantog.recipebook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.example.szantog.recipebook.adapters.SearchSimilarityAdapter;
import com.example.szantog.recipebook.controllers.DatabaseHandlerRecipes;
import com.example.szantog.recipebook.dialogs.RecipeMainDialogBuilder;
import com.example.szantog.recipebook.models.RecipeItem;
import com.example.szantog.recipebook.services.ListViewRemoteViewsFactory;

public class DialogActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TEXTSTOSEARCH_KEY = "textstosearch_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Intent intent = getIntent();
        if (intent.getStringExtra(TEXTSTOSEARCH_KEY) != null) {
            String[] texts;
            if (intent.getStringExtra(TEXTSTOSEARCH_KEY).equals(ListViewRemoteViewsFactory.DIVIDER_LISTVIEW)) {
                texts = new String[1];
                texts[0] = "";
            } else {
                texts = intent.getStringExtra(TEXTSTOSEARCH_KEY).split(ListViewRemoteViewsFactory.DIVIDER_LISTVIEW);
            }

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.widget_dialog, null);

            final ViewSwitcher switcher = view.findViewById(R.id.widget_dialog_switcher);
            ImageView switcher_btn = view.findViewById(R.id.widget_dialog_switcher_btn);
            switcher_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switcher.showNext();
                }
            });

            DatabaseHandlerRecipes dbh = new DatabaseHandlerRecipes(this);
            SearchSimilarityAdapter lunchAdapter = new SearchSimilarityAdapter(this, texts[0], dbh.getAllData());
            ListView lunchList = view.findViewById(R.id.widget_dialog_listview_lunch);
            lunchList.setAdapter(lunchAdapter);
            lunchList.setOnItemClickListener(this);
            if (texts.length > 1) {
                SearchSimilarityAdapter dinnerAdapter = new SearchSimilarityAdapter(this, texts[1], dbh.getAllData());
                ListView dinnerList = view.findViewById(R.id.widget_dialog_listview_dinner);
                dinnerList.setAdapter(dinnerAdapter);
                dinnerList.setOnItemClickListener(this);
            }

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                    .setView(view);

            dialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    finish();
                }
            });
            dialogBuilder.show();
        } else {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if ((adapterView.getItemAtPosition(i)) != null) {
            RecipeItem item = (RecipeItem) adapterView.getItemAtPosition(i);
            String title = item.getName();
            String ingredients = item.getIngredientsAsString();
            String description = item.getDescription();

            RecipeMainDialogBuilder recipeMainDialogBuilder = new RecipeMainDialogBuilder(this, title, ingredients, description);
            recipeMainDialogBuilder.show();
        }
    }
}
