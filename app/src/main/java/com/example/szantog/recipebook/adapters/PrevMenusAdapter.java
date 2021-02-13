package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.szantog.recipebook.models.WeeklyMenu;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrevMenusAdapter extends BaseExpandableListAdapter {

    private Context context;
    private WeeklyMenu[] menus;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy. MM. dd");

    public PrevMenusAdapter(Context context, WeeklyMenu[] menus) {
        this.context = context;
        this.menus = menus;
    }

    @Override
    public int getGroupCount() {
        return menus.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return 7;
    }

    @Override
    public Object getGroup(int i) {
        return menus[i].getTime();
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
        view = inflater.inflate(android.R.layout.simple_list_item_2, null);
        view.setBackgroundColor(Color.parseColor("#BBBBAA"));
        TextView textView1 = view.findViewById(android.R.id.text1);
        textView1.setText(String.valueOf(pos + 1) + ". bejegyzés");
        TextView textView2 = view.findViewById(android.R.id.text2);
        textView2.setText(simpleDateFormat.format(new Date(menus[pos].getTime())));
        return view;
    }

    @Override
    public View getChildView(int groupPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(android.R.layout.simple_list_item_1, null);
        String text;
        switch (childPos) {
            case 0:
                text = menus[groupPos].getMonday();
                break;
            case 1:
                text = menus[groupPos].getTuesday();
                break;
            case 2:
                text = menus[groupPos].getWednesday();
                break;
            case 3:
                text = menus[groupPos].getThursday();
                break;
            case 4:
                text = menus[groupPos].getFriday();
                break;
            case 5:
                text = menus[groupPos].getSaturday();
                break;
            case 6:
                text = menus[groupPos].getSunday();
                break;
            default:
                text = "";
        }
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(text);
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}
