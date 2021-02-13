package com.example.szantog.recipebook.fragments;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.WeeklyMenuItem;
import com.example.szantog.recipebook.adapters.WeeklyMenuListAdapter;
import com.example.szantog.recipebook.WidgetProvider;
import com.example.szantog.recipebook.WrapperActivity;
import com.example.szantog.recipebook.adapters.PrevMenusAdapter;
import com.example.szantog.recipebook.adapters.SearchSimilarityAdapter;
import com.example.szantog.recipebook.controllers.DatabaseHandlerRecipes;
import com.example.szantog.recipebook.controllers.DatabaseHandlerWeeklyMenu;
import com.example.szantog.recipebook.dialogs.RecipeMainDialogBuilder;
import com.example.szantog.recipebook.models.RecipeItem;
import com.example.szantog.recipebook.models.WeeklyMenu;
import com.example.szantog.recipebook.services.ChatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class WeeklyMenuFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, WeeklyMenuListAdapter.WeeklyMenuListAdapterListener, View.OnClickListener {

    private SwipeRefreshLayout swipe;

    private WeeklyMenuListAdapter weeklyMenuListAdapter;
    private ArrayList<WeeklyMenuItem> currentItems = new ArrayList<>();
    private ArrayList<String> autoKeywords = new ArrayList<>();

    private DatabaseHandlerWeeklyMenu databaseHandlerWeeklyMenu;
    private Boolean visible = false;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd. HH:mm", new Locale("hu"));
    ;
    private TextView timeStamp;

    private AlertDialog.Builder prevMenuDialogBuilder;
    private AlertDialog prevMenuDialog;
    private View prevMenuDialogView;

    private ImageView prevWeekButton;
    private ImageView nextWeekButton;
    private TextView weekLabel;
    private int actualWeek = 0;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.optionsmenu_weeklymenu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.quit) {
            Intent exitIntent = new Intent(getActivity(), ChatService.class);
            getActivity().stopService(exitIntent);
            getActivity().finish();
        } else if (item.getItemId() == R.id.delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Megerősítés");
            builder.setMessage("Biztos, hogy töröljünk minden bejegyzést?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    databaseHandlerWeeklyMenu.DeleteAllRows();
                    LoadLocalData();
                }
            });
            builder.setNegativeButton("Mégse", null);
            builder.show();
        } else if (item.getItemId() == R.id.weeklemenu_menu_previous) {
            new DownloadPrevMenus().execute("");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (visible && isVisibleToUser) {
            LoadLocalData();
        } else if (visible && !isVisibleToUser) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.weeklymenu_mainscreen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        visible = true;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        swipe = view.findViewById(R.id.weeklymenu_swiperefresh);
        swipe.setOnRefreshListener(this);

        prevWeekButton = view.findViewById(R.id.weeklymenu_prevweekbtn);
        nextWeekButton = view.findViewById(R.id.weeklymenu_nextweekbtn);
        weekLabel = view.findViewById(R.id.weeklymenu_weeklabel);
        prevWeekButton.setOnClickListener(this);
        nextWeekButton.setOnClickListener(this);

        timeStamp = view.findViewById(R.id.weeklymenu_lastupdatedtime);

        databaseHandlerWeeklyMenu = new DatabaseHandlerWeeklyMenu(this.getContext());

        weeklyMenuListAdapter = new WeeklyMenuListAdapter(getActivity(), currentItems, autoKeywords);
        ListView weeklyMenuListView = view.findViewById(R.id.weeklymenu_listview);
        weeklyMenuListView.setAdapter(weeklyMenuListAdapter);
        weeklyMenuListAdapter.setListener(this);
        weeklyMenuListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    swipe.setEnabled(true);
                } else {
                    swipe.setEnabled(false);
                }
            }
        });

        UpdateAutoComplete();

        prevMenuDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        prevMenuDialogView = inflater.inflate(R.layout.prevmenu_dialog, null);
        prevMenuDialogBuilder.setView(prevMenuDialogView);
        prevMenuDialog = prevMenuDialogBuilder.create();

        LoadLocalData();
    }

    @Override
    public void onPause() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), WidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_listview);
        super.onPause();
    }

    private void UpdateAutoComplete() {
        autoKeywords.clear();
        autoKeywords.addAll(weeklyMenuListAdapter.getEntryAsArrayList());
        weeklyMenuListAdapter.notifyDataSetChanged();
    }

    private void LoadLocalData() {
        currentItems.clear();
        try {
            WeeklyMenu weeklyMenu = databaseHandlerWeeklyMenu.getData();
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getMonday(), weeklyMenu.getMondayDinner()));
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getTuesday(), weeklyMenu.getTuesdayDinner()));
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getWednesday(), weeklyMenu.getWednesdayDinner()));
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getThursday(), weeklyMenu.getThursdayDinner()));
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getFriday(), weeklyMenu.getFridayDinner()));
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getSaturday(), weeklyMenu.getSaturdayDinner()));
            currentItems.add(new WeeklyMenuItem(weeklyMenu.getSunday(), weeklyMenu.getSundayDinner()));
            timeStamp.setText(simpleDateFormat.format(weeklyMenu.getTime()));
        } catch (CursorIndexOutOfBoundsException e) {
            currentItems.clear();
            for (int i = 0; i < 7; i++) {
                currentItems.add(new WeeklyMenuItem("", ""));
            }
            e.printStackTrace();
            Log.e("Weeklymenu", "nincsenek még adatok a gépen");
        }
        weeklyMenuListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        UpdateAutoComplete();
        new ConnectToServer().execute("");
    }

    @Override
    public void onClicked(WeeklyMenuItem newItem, int position, Boolean isEntryChanged) {
        if (isEntryChanged) {
            currentItems.set(position, newItem);
            databaseHandlerWeeklyMenu.DeleteAllRows();
            databaseHandlerWeeklyMenu.insertData(new WeeklyMenu("id", currentItems.get(0).getLunch(),
                    currentItems.get(1).getLunch(), currentItems.get(2).getLunch(), currentItems.get(3).getLunch(),
                    currentItems.get(4).getLunch(), currentItems.get(5).getLunch(), currentItems.get(6).getLunch(),
                    currentItems.get(0).getDinner(), currentItems.get(1).getDinner(), currentItems.get(2).getDinner(),
                    currentItems.get(3).getDinner(), currentItems.get(4).getDinner(), currentItems.get(5).getDinner(),
                    currentItems.get(6).getDinner(), System.currentTimeMillis()));
        }
    }

    @Override
    public void onSimilaritySearched(WeeklyMenuItem newItem, int position) {
        TextView titleText = prevMenuDialogView.findViewById(R.id.prevmenu_title);
        titleText.setText("Találatok");

        String selectedText = newItem.getLunch();
        DatabaseHandlerRecipes dbh = new DatabaseHandlerRecipes(this.getContext());

        SearchSimilarityAdapter searchSimilarityAdapter = new SearchSimilarityAdapter(getActivity(),
                selectedText, dbh.getAllData());
        ListView listView = prevMenuDialogView.findViewById(R.id.prevmenus_listview);

        listView.setAdapter(searchSimilarityAdapter);
        prevMenuDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RecipeItem item = (RecipeItem) adapterView.getItemAtPosition(i);
                String title = item.getName();
                String ingredients = item.getIngredientsAsString();
                String description = item.getDescription();

                RecipeMainDialogBuilder recipeMainDialogBuilder = new RecipeMainDialogBuilder(getContext(), title, ingredients, description);
                recipeMainDialogBuilder.show();

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.weeklymenu_prevweekbtn:

                //Load data from db
                break;
            case R.id.weeklymenu_nextweekbtn:
                if (actualWeek < 2) {
                    actualWeek++;
                }
                //Load data from db
                break;
        }
    }


    class ConnectToServer extends AsyncTask<String, String, String> {

        Boolean networkSuccess = false;
        Boolean refreshFromServer;
        Boolean equalTimeStamps = false;
        Boolean badPassword = false;
        private WeeklyMenu downloadedWeeklyMenu;

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            refreshFromServer = false;

            try {
                url = new URL(WrapperActivity.SERVERURL + "/getweeklymenu");
                HttpsURLConnection connectionDownload = (HttpsURLConnection) url.openConnection();
                connectionDownload.setRequestMethod("GET");
                connectionDownload.connect();

                InputStream is = connectionDownload.getInputStream();
                BufferedReader brDownload = new BufferedReader(new InputStreamReader(is));
                String data = brDownload.readLine();
                brDownload.close();
                is.close();
                connectionDownload.disconnect();
                Gson gson = new GsonBuilder().create();
                downloadedWeeklyMenu = gson.fromJson(data, WeeklyMenu.class);
                WeeklyMenu locallySavedMenu = null;
                try {
                    locallySavedMenu = databaseHandlerWeeklyMenu.getData();
                } catch (Exception e) {
                    locallySavedMenu = new WeeklyMenu("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", 0);
                }

                if (downloadedWeeklyMenu.getTime() > locallySavedMenu.getTime()) {
                    if (downloadedWeeklyMenu.getMondayDinner() == null) {
                        downloadedWeeklyMenu.setMondayDinner("");
                    }
                    if (downloadedWeeklyMenu.getTuesdayDinner() == null) {
                        downloadedWeeklyMenu.setTuesdayDinner("");
                    }
                    if (downloadedWeeklyMenu.getWednesdayDinner() == null) {
                        downloadedWeeklyMenu.setWednesdayDinner("");
                    }
                    if (downloadedWeeklyMenu.getThursdayDinner() == null) {
                        downloadedWeeklyMenu.setThursdayDinner("");
                    }
                    if (downloadedWeeklyMenu.getFridayDinner() == null) {
                        downloadedWeeklyMenu.setFridayDinner("");
                    }
                    if (downloadedWeeklyMenu.getSaturdayDinner() == null) {
                        downloadedWeeklyMenu.setSaturdayDinner("");
                    }
                    if (downloadedWeeklyMenu.getSundayDinner() == null) {
                        downloadedWeeklyMenu.setSundayDinner("");
                    }
                    refreshFromServer = true;
                    networkSuccess = true;
                } else if (downloadedWeeklyMenu.getTime() == locallySavedMenu.getTime()) {
                    equalTimeStamps = true;
                } else {
                    url = new URL(WrapperActivity.SERVERURL + "/setweeklymenu");
                    HttpsURLConnection connectionUpload = (HttpsURLConnection) url.openConnection();
                    connectionUpload.setRequestMethod("POST");
                    connectionUpload.setDoOutput(true);
                    JsonObject json = new JsonObject();
                    SharedPreferences sharedPrefs = getActivity().getSharedPreferences(WrapperActivity.SHAREDPREF, 0);
                    String password = sharedPrefs.getString(WrapperActivity.PASSWORD_KEY, "");
                    json.addProperty("password", password);
                    json.addProperty("monday", currentItems.get(0).getLunch());
                    json.addProperty("tuesday", currentItems.get(1).getLunch());
                    json.addProperty("wednesday", currentItems.get(2).getLunch());
                    json.addProperty("thursday", currentItems.get(3).getLunch());
                    json.addProperty("friday", currentItems.get(4).getLunch());
                    json.addProperty("saturday", currentItems.get(5).getLunch());
                    json.addProperty("sunday", currentItems.get(6).getLunch());
                    json.addProperty("mondayDinner", currentItems.get(0).getDinner());
                    json.addProperty("tuesdayDinner", currentItems.get(1).getDinner());
                    json.addProperty("wednesdayDinner", currentItems.get(2).getDinner());
                    json.addProperty("thursdayDinner", currentItems.get(3).getDinner());
                    json.addProperty("fridayDinner", currentItems.get(4).getDinner());
                    json.addProperty("saturdayDinner", currentItems.get(5).getDinner());
                    json.addProperty("sundayDinner", currentItems.get(6).getDinner());
                    json.addProperty("time", System.currentTimeMillis());
                    OutputStream out = connectionUpload.getOutputStream();
                    out.write(json.toString().getBytes("UTF-8"));
                    out.flush();
                    out.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connectionUpload.getInputStream()));
                    String response = br.readLine().toString();
                    if (response.equals("success")) {
                        networkSuccess = true;
                    } else {
                        badPassword = true;
                    }
                    br.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            swipe.setRefreshing(false);
            if (badPassword) {
                Toast.makeText(WeeklyMenuFragment.this.getContext(), "Kapcsolódás sikertelen - rossz jelszó!", Toast.LENGTH_LONG).show();
            } else if (equalTimeStamps) {
                Toast.makeText(WeeklyMenuFragment.this.getContext(), "Helyi- és szerveradatok megegyeznek.", Toast.LENGTH_LONG).show();
            } else if (refreshFromServer && networkSuccess) {
                Toast.makeText(WeeklyMenuFragment.this.getContext(), "Adatok frissítve a szerverről.", Toast.LENGTH_LONG).show();
                databaseHandlerWeeklyMenu.DeleteAllRows();
                databaseHandlerWeeklyMenu.insertData(downloadedWeeklyMenu);
                currentItems.set(0, new WeeklyMenuItem(downloadedWeeklyMenu.getMonday(), downloadedWeeklyMenu.getMondayDinner()));
                currentItems.set(1, new WeeklyMenuItem(downloadedWeeklyMenu.getTuesday(), downloadedWeeklyMenu.getTuesdayDinner()));
                currentItems.set(2, new WeeklyMenuItem(downloadedWeeklyMenu.getWednesday(), downloadedWeeklyMenu.getWednesdayDinner()));
                currentItems.set(3, new WeeklyMenuItem(downloadedWeeklyMenu.getThursday(), downloadedWeeklyMenu.getThursdayDinner()));
                currentItems.set(4, new WeeklyMenuItem(downloadedWeeklyMenu.getFriday(), downloadedWeeklyMenu.getFridayDinner()));
                currentItems.set(5, new WeeklyMenuItem(downloadedWeeklyMenu.getSaturday(), downloadedWeeklyMenu.getSaturdayDinner()));
                currentItems.set(6, new WeeklyMenuItem(downloadedWeeklyMenu.getSunday(), downloadedWeeklyMenu.getSundayDinner()));
                weeklyMenuListAdapter.notifyDataSetChanged();
                timeStamp.setText(simpleDateFormat.format(downloadedWeeklyMenu.getTime()));
            } else if (!refreshFromServer && networkSuccess) {
                Toast.makeText(WeeklyMenuFragment.this.getContext(), "Adatok feltöltve a szerverre.", Toast.LENGTH_LONG).show();
            } else if (!networkSuccess) {
                Toast.makeText(WeeklyMenuFragment.this.getContext(), "Kapcsolódás a szerverhez sikertelen!", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }

    class DownloadPrevMenus extends AsyncTask<String, String, String> {

        private Boolean downloadSuccess;
        private String data;
        private WeeklyMenu[] prevMenus;

        @Override
        protected void onPreExecute() {
            swipe.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            downloadSuccess = false;
            try {
                url = new URL(WrapperActivity.SERVERURL + "/getprevmenus");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                data = bReader.readLine();
                Gson gson = new GsonBuilder().create();
                prevMenus = gson.fromJson(data, WeeklyMenu[].class);
                connection.disconnect();
                downloadSuccess = true;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            swipe.setRefreshing(false);
            if (downloadSuccess) {
                TextView titleText = prevMenuDialogView.findViewById(R.id.prevmenu_title);
                titleText.setText("Előző menük");
                ListView listView = prevMenuDialogView.findViewById(R.id.prevmenus_listview);
                PrevMenusAdapter prevMenusAdapter = new PrevMenusAdapter(getActivity(), prevMenus);
                // listView.setAdapter(prevMenusAdapter);
                prevMenuDialog.show();
            } else {
                Toast.makeText(WeeklyMenuFragment.this.getContext(), "Korábbi menük letöltése sikertelen", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(s);
        }
    }
}
