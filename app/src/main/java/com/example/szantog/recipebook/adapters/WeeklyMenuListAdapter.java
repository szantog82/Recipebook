package com.example.szantog.recipebook.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.WeeklyMenuItem;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.04.26..
 */

public class WeeklyMenuListAdapter extends BaseAdapter {

    public interface WeeklyMenuListAdapterListener {
        void onClicked(WeeklyMenuItem newItem, int position, Boolean isEntryChanged);

        void onSimilaritySearched(WeeklyMenuItem newItem, int position);
    }

    private static final String[] DAY_NAMES = {"Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap"};

    private WeeklyMenuListAdapterListener listener;
    private Context context;
    private ArrayList<WeeklyMenuItem> items;
    private ArrayList<String> keywords;


    public WeeklyMenuListAdapter(Context context, ArrayList<WeeklyMenuItem> items, ArrayList<String> keywords) {
        this.context = context;
        this.items = items;
        this.keywords = keywords;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.weeklymenu_listitem_layout, viewGroup, false);
        }
        TextView dayNameText = view.findViewById(R.id.weeklymenu_listview_dayname);
        final TextView lunchText = view.findViewById(R.id.weeklymenu_listview_lunchtext);
        final TextView dinnerText = view.findViewById(R.id.weeklymenu_listview_dinnertext);
        final AutoCompleteTextView lunchEditText = view.findViewById(R.id.weeklymenu_listview_lunchedittext);
        final AutoCompleteTextView dinnerEditText = view.findViewById(R.id.weeklymenu_listview_dinneredittext);
        ImageView searchButton = view.findViewById(R.id.weeklymenu_listview_searchsimilarity);
        ImageView editButton = view.findViewById(R.id.weeklymenu_listview_editbutton);
        ImageView saveButton = view.findViewById(R.id.weeklymenu_listview_editsave);
        ImageView cancelButton = view.findViewById(R.id.weeklymenu_listview_editcancel);
        final ViewSwitcher viewSwitcher = view.findViewById(R.id.weeklymenu_listview_viewswitcher);

        ArrayAdapter<String> keywordAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, keywords);
        lunchEditText.setAdapter(keywordAdapter);
        dinnerEditText.setAdapter(keywordAdapter);
        dayNameText.setText(DAY_NAMES[pos]);
        lunchText.setText(items.get(pos).getLunch());
        dinnerText.setText(items.get(pos).getDinner());

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onSimilaritySearched(new WeeklyMenuItem(lunchText.getText().toString(), dinnerText.getText().toString()), pos);
                }
            }
        });
        final InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                im.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                viewSwitcher.showNext();
                lunchEditText.setText(lunchText.getText().toString());
                dinnerEditText.setText(dinnerText.getText().toString());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
                if (listener != null) {
                    Boolean isEntryChanged = true;
                    if (lunchText.getText().toString().equals(lunchEditText.getText().toString()) &&
                            dinnerText.getText().toString().equals(dinnerEditText.getText().toString())) {
                        isEntryChanged = false;
                    }
                    listener.onClicked(new WeeklyMenuItem(lunchEditText.getText().toString(), dinnerEditText.getText().toString()), pos, isEntryChanged);
                }
                lunchText.setText(lunchEditText.getText().toString());
                dinnerText.setText(dinnerEditText.getText().toString());
                viewSwitcher.showNext();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                im.hideSoftInputFromWindow(view.getWindowToken(), 0);
                viewSwitcher.showNext();
            }
        });

        return view;
    }

    public void setListener(WeeklyMenuListAdapterListener listener) {
        this.listener = listener;
    }

    public ArrayList<String> getEntryAsArrayList() {
        ArrayList<String> output = new ArrayList<>();
        for (WeeklyMenuItem item : items) {
            output.add(item.getLunch());
            output.add(item.getDinner());
        }
        return output;
    }
}
