package com.example.szantog.recipebook.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.WrapperActivity;
import com.example.szantog.recipebook.adapters.RecipeAddToDaysAdapter;
import com.example.szantog.recipebook.adapters.RecipeListAdapter;
import com.example.szantog.recipebook.controllers.DatabaseHandlerRecipes;
import com.example.szantog.recipebook.controllers.DatabaseHandlerWeeklyMenu;
import com.example.szantog.recipebook.dialogs.AddNewRecipeDialogBuilder;
import com.example.szantog.recipebook.dialogs.RecipeMainDialogBuilder;
import com.example.szantog.recipebook.models.RecipeItem;
import com.example.szantog.recipebook.services.ChatService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class RecipeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, AdapterView.OnItemClickListener, RecipeListAdapter.RecipeListAdapterListener, AddNewRecipeDialogBuilder.OnNewRecipeAddedListener {

    public static final String SERVERURL = "https://recipes-szg.herokuapp.com";

    private SwipeRefreshLayout swipeRefreshLayout;

    private MenuItem uploadMenuItem;
    private TextView keyword_textView;
    private TextView btn_del_keyword;
    private ArrayList<RecipeItem> recipeItems = new ArrayList<>();
    private ArrayList<RecipeItem> recipes;
    private RecipeListAdapter recipeListAdapter;
    private DatabaseHandlerRecipes dbh;
    private Boolean visible = false;

    private FloatingActionButton addNewFAB;
    private AddNewRecipeDialogBuilder addNewRecipeDialogBuilder;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.optionsmenu_recipes, menu);
        uploadMenuItem = menu.findItem(R.id.menuitem_upload_recipe);

        MenuItem searchItem = menu.findItem(R.id.search_field);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateTable(query, false);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateTable(newText, false);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.setpwd) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Jelszó");
            final EditText editText = new EditText(getContext());
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(editText);
            builder.setTitle("Receptkönyv");
            builder.setMessage("Jelszó megadása");
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_POSITIVE) {
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(WrapperActivity.SHAREDPREF, 0);
                        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
                        sharedEditor.putString(WrapperActivity.PASSWORD_KEY, editText.getText().toString());
                        sharedEditor.apply();
                        Toast.makeText(getContext(), "Jelszó mentve", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            builder.setPositiveButton("OK", listener);
            builder.setNegativeButton("Mégse", listener);
            builder.create().show();
        } else if (item.getItemId() == R.id.quit) {
            Intent exitIntent = new Intent(getActivity(), ChatService.class);
            getActivity().stopService(exitIntent);
            getActivity().finish();
        } else if (item.getItemId() == R.id.menuitem_upload_recipe) {
            new ExchangeData().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (visible && isVisibleToUser) {
            updateTable("", true);
            new UploadMenuSet().execute("");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String title = getArguments().getString(getActivity().getString(R.string.bundle_title));
            String ingredients = getArguments().getString(getActivity().getString(R.string.bundle_ingredients));
            String description = getArguments().getString(getActivity().getString(R.string.bundle_description));
            addNewRecipeDialogBuilder = new AddNewRecipeDialogBuilder(getContext(), title, ingredients, description);
            addNewRecipeDialogBuilder.setNewRecipeAddedListener(this);
            addNewRecipeDialogBuilder.showDialog();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.recipes_mainscreen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbh = new DatabaseHandlerRecipes(this.getContext());
        keyword_textView = view.findViewById(R.id.keyword);
        btn_del_keyword = view.findViewById(R.id.btn_delete_keyword);
        btn_del_keyword.setOnClickListener(this);

        addNewFAB = view.findViewById(R.id.add_new_recipe_FAB);
        addNewFAB.setOnClickListener(this);

        recipes = dbh.getAllData();
        new UploadMenuSet().execute("");

        ListView listView = view.findViewById(R.id.recipes_mainscreen_list);
        recipeListAdapter = new RecipeListAdapter(getContext(), recipeItems);
        listView.setAdapter(recipeListAdapter);
        recipeListAdapter.setListener(this);
        listView.setOnItemClickListener(this);

        if (!visible) {
            updateTable("", true);
            visible = true;
        }

        swipeRefreshLayout = view.findViewById(R.id.swiperefresh_recipes);
        swipeRefreshLayout.setOnRefreshListener(this);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (i == 0) swipeRefreshLayout.setEnabled(true);
                else swipeRefreshLayout.setEnabled(false);
            }
        });
    }

    public void updateTable(String keyword, Boolean firstTime) {
        if (keyword.length() > 1) {
            keyword_textView.setText("'" + keyword + "'");
            keyword_textView.setVisibility(View.VISIBLE);
            btn_del_keyword.setVisibility(View.VISIBLE);
        } else {
            keyword_textView.setVisibility(View.GONE);
            btn_del_keyword.setVisibility(View.GONE);
        }
        String modKeyword = keyword.replace(",", " ");
        modKeyword = modKeyword.replace("   ", " ");
        modKeyword = modKeyword.replace("  ", " ");
        String[] keywordArray = modKeyword.split(" ");

        List<Integer> indexesToShow = new ArrayList<Integer>();

        int pos = 0;
        for (RecipeItem recipe : recipes) {
            int hitCount = 0;  //how many keywords found in each recipes. If = keywordArray.length => it is a hit.

            for (int i = 0; i < keywordArray.length; i++) {
                Boolean ingredFound = false;
                if (recipe.getName().toLowerCase().contains(keywordArray[i])) {
                    hitCount++;
                } else {
                    for (String ingredient : recipe.getIngredients()) {
                        if (ingredient.toLowerCase().contains(keywordArray[i])) {
                            ingredFound = true;
                        }
                    }
                }
                if (ingredFound) {
                    hitCount++;
                }
            }
            if (hitCount >= keywordArray.length) {
                indexesToShow.add(pos);
            }
            pos++;
        }

        recipeItems.clear();

        if (indexesToShow.size() < 1) {
            Log.e("RecipesUpdateTable", "No hits found.");
            recipeListAdapter.notifyDataSetChanged();
            return;
        }
        ;

        Log.e("RecipesUpdateTable", "number of recipes by the search criteria: " + String.valueOf(indexesToShow.size()));

        for (int index : indexesToShow) {
            recipeItems.add(recipes.get(index));
        }

        if (!firstTime) recipeListAdapter.notifyDataSetChanged();

    }

    @Override
    public void onRefresh() {
        new ExchangeData().execute("");
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_delete_keyword) {
            keyword_textView.setText("");
            btn_del_keyword.setVisibility(View.INVISIBLE);
            updateTable("", false);
        } else if (view.getId() == R.id.add_new_recipe_FAB) {
            addNewRecipeDialogBuilder = null;
            addNewRecipeDialogBuilder = new AddNewRecipeDialogBuilder(getContext(), "", "", "");
            addNewRecipeDialogBuilder.setNewRecipeAddedListener(this);
            addNewRecipeDialogBuilder.showDialog();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String title = recipeItems.get(i).getName();
        String ingredientList = recipeItems.get(i).getIngredientsAsString();
        String description = recipeItems.get(i).getDescription();
        RecipeMainDialogBuilder recipeMainDialogBuilder = new RecipeMainDialogBuilder(getContext(), title, ingredientList, description);
        recipeMainDialogBuilder.show();
    }

    @Override
    public void onAddToDaysClicked(int position, final RecipeItem selectedItem) {
        AlertDialog.Builder dayChooserDialogBuilder = new AlertDialog.Builder(getContext(), R.style.AddToDaysDialogTheme);
        ListView listView = new ListView(getActivity());
        listView.setPadding(5, 5, 5, 5);
        listView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.lightyellow));
        dayChooserDialogBuilder.setView(listView);
        dayChooserDialogBuilder.setTitle("Válassz napot");
        dayChooserDialogBuilder.setMessage("Lehetőségek:");
        final AlertDialog dayChooserDialog = dayChooserDialogBuilder.create();
        dayChooserDialog.show();
        final String[] days = {"Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap"};
        RecipeAddToDaysAdapter recipeAddToDaysAdapter = new RecipeAddToDaysAdapter(getContext(), days);
        listView.setAdapter(recipeAddToDaysAdapter);
        recipeAddToDaysAdapter.setOnListViewButtonClickListener(new RecipeAddToDaysAdapter.OnListViewButtonClickListener() {
            @Override
            public void onButtonClicked(RecipeAddToDaysAdapter.ButtonItem buttonItem) {
                Boolean isDinner = false;
                if (buttonItem.getType().equals(RecipeAddToDaysAdapter.DINNER)) {
                    isDinner = true;
                }
                int index = buttonItem.getIndex();
                DatabaseHandlerWeeklyMenu dbhwm = new DatabaseHandlerWeeklyMenu(getContext());
                dbhwm.updateOneEntry(selectedItem.getName(), index, isDinner);
                Toast.makeText(getContext(), selectedItem.getName() + " hozzáadva: " + days[index], Toast.LENGTH_SHORT).show();
                dayChooserDialog.dismiss();
            }
        });
    }

    @Override
    public void onDeleteItemClicked(int position, final RecipeItem selectedItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Megerősítés");
        builder.setMessage("Biztos, hogy töröljük?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbh.deleteItem(selectedItem.getId());
                recipes = dbh.getAllData();
                updateTable("", false);
            }
        });
        builder.setNegativeButton("Mégse", null);
        builder.show();
    }

    @Override
    public void onRecipeAdded(RecipeItem recipeItem) {
        dbh.insertLocalNotUploadedData(recipeItem);
        recipes = dbh.getAllData();
        updateTable("", false);
    }

    class ExchangeData extends AsyncTask<String, String, String> {

        private Boolean downloadSuccess = false;
        private Boolean uploadSuccess = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            if (dbh.hasRecipesToBeUploaded()) {
                try {
                    SharedPreferences sharedPrefs = getActivity().getSharedPreferences(WrapperActivity.SHAREDPREF, 0);
                    String password = sharedPrefs.getString(WrapperActivity.PASSWORD_KEY, "");
                    JsonObject json;

                    url = new URL(SERVERURL + "/");
                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    connection.setDoOutput(true);
                    OutputStream out = connection.getOutputStream();
                    ArrayList<RecipeItem> itemsToUpload = dbh.getRecipesToUpload();
                    for (RecipeItem newRecipeItem : itemsToUpload) {
                        json = new JsonObject();
                        json.addProperty("password", password);
                        json.addProperty("name", newRecipeItem.getName());
                        json.addProperty("season", newRecipeItem.getSeason());
                        json.addProperty("type", newRecipeItem.getType());
                        json.addProperty("containsDairy", newRecipeItem.isContainsDiary());
                        json.addProperty("ingredients", newRecipeItem.getIngredientsAsString());
                        json.addProperty("description", newRecipeItem.getDescription());
                        out.write(json.toString().getBytes("UTF-8"));
                        out.flush();
                        dbh.deleteItem(newRecipeItem.getId());
                    }
                    out.close();
                    if (connection.getResponseCode() < 400) {
                        uploadSuccess = true;
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                url = new URL(SERVERURL + "/getrecipes");
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String data = br.readLine();
                br.close();
                is.close();
                connection.disconnect();
                Gson gson = new GsonBuilder().create();
                RecipeItem[] downloadedRecipes = gson.fromJson(data, RecipeItem[].class);
                Arrays.sort(downloadedRecipes);
                recipes = new ArrayList<RecipeItem>(Arrays.asList(downloadedRecipes));

                for (RecipeItem recipe : recipes) {
                    dbh.insertDownloadedData(recipe);
                }
                downloadSuccess = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            swipeRefreshLayout.setRefreshing(false);
            recipeItems.clear();
            recipeItems.addAll(dbh.getAllData());
            recipeListAdapter.notifyDataSetChanged();
            if (downloadSuccess) {
                Toast.makeText(getActivity(), String.valueOf(recipes.size()) + "db recept letöltve", Toast.LENGTH_LONG).show();
            }
            if (!downloadSuccess && !uploadSuccess)
                Toast.makeText(getActivity(), "Kapcsolódás a szerverhez sikertelen!", Toast.LENGTH_LONG).show();
        }
    }

    class UploadMenuSet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (dbh.hasRecipesToBeUploaded()) {
                final SimpleTooltip.Builder toolTipBuilder = new SimpleTooltip.Builder(getActivity());
                toolTipBuilder.anchorView(getActivity().findViewById(R.id.menuitem_upload_recipe))
                        .text("Vannak feltöltésre váró receptek")
                        .textColor(ContextCompat.getColor(getActivity(), R.color.tooltip_textview))
                        .gravity(Gravity.START)
                        .backgroundColor(ContextCompat.getColor(getActivity(), R.color.tooltip_background))
                        .arrowColor(ContextCompat.getColor(getActivity(), R.color.tooltip_background))
                        .showArrow(true)
                        .dismissOnOutsideTouch(false)
                        .dismissOnInsideTouch(true)
                        .transparentOverlay(true)
                        .animated(true)
                        .animationDuration(500);
                final SimpleTooltip tooltip = toolTipBuilder.build();
                tooltip.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tooltip.dismiss();
                    }
                }, 4000);
            } else {
                if (uploadMenuItem != null) {
                    //uploadMenuItem.setVisible(false);
                }
            }
        }
    }
}
