package com.example.szantog.recipebook;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.szantog.recipebook.controllers.DatabaseHandlerWeeklyMenu;
import com.example.szantog.recipebook.services.ListViewRemoteViewsFactory;
import com.example.szantog.recipebook.services.ListViewWidgetService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WidgetProvider extends AppWidgetProvider {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
    private DatabaseHandlerWeeklyMenu dbhwm;

    public static final String EXTRA_APPWIDGET_ID = "appwidget_id";

    private final String REFRESH_KEY = "refresh_key";

    private final String CLICK_KEY = "click_key";

    public static final String LISTITEMCLICK_EXTRA = "listitemclick_extra";
    public static final String LISTITEMCLICK_ACTION = "listitemclick_action";
    public static final String LISTITEMCLICK_SELECTOR = "listitemclick_selector";
    public static final String APPOPENER = "appopener";
    public static final String DIALOGOPENER = "dialogopener";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        dbhwm = new DatabaseHandlerWeeklyMenu(context);
        final SharedPreferences sharedPrefs = context.getSharedPreferences(WrapperActivity.SHAREDPREF, 0);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(CLICK_KEY, 0);
        editor.apply();
        updateWidget(context, appWidgetManager, appWidgetIds);
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        this.onReceive(context, new Intent(REFRESH_KEY));
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        dbhwm = new DatabaseHandlerWeeklyMenu(context);
        final SharedPreferences sharedPrefs = context.getSharedPreferences(WrapperActivity.SHAREDPREF, 0);
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        Handler mHandler = new Handler();
        if (intent.getAction().equals(LISTITEMCLICK_ACTION) && intent.getStringExtra(LISTITEMCLICK_SELECTOR) != null &&
                intent.getStringExtra(LISTITEMCLICK_SELECTOR).equals(DIALOGOPENER)) {
            //Dialog opens
            Intent dialogIntent = new Intent(context, DialogActivity.class);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dialogIntent.putExtra(DialogActivity.TEXTSTOSEARCH_KEY, intent.getStringExtra(LISTITEMCLICK_EXTRA));
            context.startActivity(dialogIntent);
        } else if (intent.getAction().equals(LISTITEMCLICK_ACTION) && intent.getStringExtra(LISTITEMCLICK_SELECTOR) != null &&
                intent.getStringExtra(LISTITEMCLICK_SELECTOR).equals(APPOPENER)) {
            if (sharedPrefs.getInt(CLICK_KEY, 0) == 0) {
                editor.putInt(CLICK_KEY, 1);
                editor.apply();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (sharedPrefs.getInt(CLICK_KEY, 0) < 2) {
                            //single click
                        } else if (sharedPrefs.getInt(CLICK_KEY, 0) > 1) {
                            //double click
                            Log.e("click", "doubleclicked");
                            Intent openActivity = new Intent(context, WrapperActivity.class);
                            openActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            openActivity.setAction(WrapperActivity.STARTWITHWEEKLY_KEY);
                            context.startActivity(openActivity);
                        }
                        editor.putInt(CLICK_KEY, 0);
                        editor.apply();
                    }
                }, 300);
            } else {
                editor.putInt(CLICK_KEY, 2);
                editor.apply();
            }
        } else if (intent.getAction().equals(REFRESH_KEY)) {
            Log.e("widget", "refresh");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
            updateWidget(context, appWidgetManager, appWidgetIds);
        }
        super.onReceive(context, intent);
    }


    private RemoteViews updateListView(Context context, int appWidgetId) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent serviceIntent = new Intent(context, ListViewWidgetService.class);
        serviceIntent.putExtra(EXTRA_APPWIDGET_ID, appWidgetId);

        remoteViews.setRemoteAdapter(R.id.widget_listview, serviceIntent);

        return remoteViews;
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int widgetCount = appWidgetIds.length;

        String timestamp = "-";
        try {
            timestamp = simpleDateFormat.format(new Date(dbhwm.getData().getTime()));
        } catch (Exception e) {
            Log.e("DBHWM", "No data exists...");
        }
        for (int i = 0; i < widgetCount; i++) {
            RemoteViews views = updateListView(context, appWidgetIds[i]);
            views.setOnClickPendingIntent(R.id.widget_refresh, getPendingSelfIntent(context, REFRESH_KEY));
            views.setTextViewText(R.id.widget_time, timestamp);
            try {
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i], R.id.widget_listview);
                String dayNum = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date(System.currentTimeMillis()));
                int position = 0;
                for (int j = 0; j < 7; j++) {
                    if (ListViewRemoteViewsFactory.dayNames[j].equals(dayNum)) {
                        position = j;
                    }
                }
                views.setScrollPosition(R.id.widget_listview, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent listItemClickIntent = new Intent(context, WidgetProvider.class);
            listItemClickIntent.setAction(LISTITEMCLICK_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, listItemClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_listview, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }
}
