package com.example.szantog.recipebook;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;

import com.example.szantog.recipebook.adapters.WeeklyMenuListAdapter;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.04.26..
 */

public class DebugActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = new ListView(this);

        setContentView(listView);

        ArrayList<WeeklyMenuItem> items = new ArrayList<>();

        items.add(new WeeklyMenuItem("valamilyen ebéd nyavalya a kaja", "vacsira meg más lesz"));
        items.add(new WeeklyMenuItem("valamilyen ebéd nyavalya a kaja2", "vacsira meg más lesz2"));

        final ArrayList<String> keywords = new ArrayList<>();
        keywords.add("blabla");

        final WeeklyMenuListAdapter weeklyMenuListAdapter = new WeeklyMenuListAdapter(this, items, keywords);
        weeklyMenuListAdapter.setListener(new WeeklyMenuListAdapter.WeeklyMenuListAdapterListener() {
            @Override
            public void onClicked(WeeklyMenuItem newItem, int position, Boolean i) {
                Log.e("pos", String.valueOf(position));
                keywords.add(newItem.getLunch());
                keywords.add(newItem.getDinner());
                weeklyMenuListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSimilaritySearched(WeeklyMenuItem newItem, int position) {

            }
        });


        listView.setAdapter(weeklyMenuListAdapter);

    }
}
