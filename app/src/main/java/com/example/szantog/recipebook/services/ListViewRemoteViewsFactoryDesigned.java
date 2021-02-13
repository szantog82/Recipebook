package com.example.szantog.recipebook.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.WidgetProvider;
import com.example.szantog.recipebook.controllers.DatabaseHandlerWeeklyMenu;
import com.example.szantog.recipebook.models.WeeklyMenu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListViewRemoteViewsFactoryDesigned implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private Intent intent;
    private WeeklyMenu weeklyMenu;
    private DatabaseHandlerWeeklyMenu dbhwm;

    public static String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static final String DIVIDER_LISTVIEW = "__DIVIDER__--__QWERTY";

    public ListViewRemoteViewsFactoryDesigned(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        dbhwm = new DatabaseHandlerWeeklyMenu(context);
        try {
            weeklyMenu = dbhwm.getData();
        } catch (Exception e) {
            Log.e("ListViewRemViewsFactory", "Data cannot be fetched!");
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        dbhwm = new DatabaseHandlerWeeklyMenu(context);
        try {
            weeklyMenu = dbhwm.getData();
        } catch (Exception e) {
            Log.e("ListViewRemViewsFactory", "onDataSetChanged - Data cannot be fetched!");
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_listitem_designed);

        String text1 = "";
        String text2 = "";
        String text3 = "";
        String textExtra = "";
        if (weeklyMenu == null) {
            weeklyMenu = new WeeklyMenu("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", 0);
        }
        switch (i) {
            case 0:
                text1 = "H";
                text2 = weeklyMenu.getMonday();
                text3 = weeklyMenu.getMondayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
            case 1:
                text1 = "K";
                text2 = weeklyMenu.getTuesday();
                text3 = weeklyMenu.getTuesdayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
            case 2:
                text1 = "Sze";
                text2 = weeklyMenu.getWednesday();
                text3 = weeklyMenu.getWednesdayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
            case 3:
                text1 = "Cs";
                text2 = weeklyMenu.getThursday();
                text3 = weeklyMenu.getThursdayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
            case 4:
                text1 = "P";
                text2 = weeklyMenu.getFriday();
                text3 = weeklyMenu.getFridayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
            case 5:
                text1 = "Szo";
                text2 = weeklyMenu.getSaturday();
                text3 = weeklyMenu.getSaturdayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
            case 6:
                text1 = "V";
                text2 = weeklyMenu.getSunday();
                text3 = weeklyMenu.getSundayDinner();
                textExtra = text2 + DIVIDER_LISTVIEW + text3;
                break;
        }
        String dayNum = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date(System.currentTimeMillis()));
        if (dayNames[i].equals(dayNum)) {
            rv.setTextViewText(R.id.widget_listitem_designed_text1, Html.fromHtml(String.format("<b>%s</b>", text2)));
            rv.setTextViewText(R.id.widget_listitem_designed_text2, Html.fromHtml(String.format("<b>%s</b>", text3)));
            rv.setTextViewText(R.id.widget_listitem_designed_text3, Html.fromHtml(String.format("<i><b>%s</i></b>", text1)));
            rv.setInt(R.id.widget_listitem_linearlayout, "setBackgroundColor", ContextCompat.getColor(context, R.color.middleyellow));
            rv.setInt(R.id.widget_listitem_relativelayout, "setBackgroundColor", ContextCompat.getColor(context, R.color.middleyellow));
        } else {
            rv.setTextViewText(R.id.widget_listitem_designed_text1, text2);
            rv.setTextViewText(R.id.widget_listitem_designed_text2, text3);
            rv.setTextViewText(R.id.widget_listitem_designed_text3, Html.fromHtml(String.format("<i>%s</i>", text1)));
            rv.setInt(R.id.widget_listitem_linearlayout, "setBackgroundColor", ContextCompat.getColor(context, R.color.lightyellow));
            rv.setInt(R.id.widget_listitem_relativelayout, "setBackgroundColor", ContextCompat.getColor(context, R.color.lightyellow));
        }
        Intent clickIntent = new Intent(WidgetProvider.LISTITEMCLICK_ACTION);
        clickIntent.putExtra(WidgetProvider.LISTITEMCLICK_EXTRA, String.valueOf(i));
        clickIntent.putExtra(WidgetProvider.LISTITEMCLICK_SELECTOR, WidgetProvider.APPOPENER);
        rv.setOnClickFillInIntent(R.id.widget_listitem_linearlayout, clickIntent);
        Intent otherClickIntent = new Intent(WidgetProvider.LISTITEMCLICK_ACTION);
        otherClickIntent.putExtra(WidgetProvider.LISTITEMCLICK_EXTRA, textExtra);
        otherClickIntent.putExtra(WidgetProvider.LISTITEMCLICK_SELECTOR, WidgetProvider.DIALOGOPENER);
        rv.setOnClickFillInIntent(R.id.widget_listitem_img, otherClickIntent);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
