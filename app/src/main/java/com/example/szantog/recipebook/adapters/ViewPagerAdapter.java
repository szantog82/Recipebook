package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.fragments.ChatFragment;
import com.example.szantog.recipebook.fragments.RecipeFragment;
import com.example.szantog.recipebook.fragments.WeeklyMenuFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private RecipeFragment recipeFragment;
    private WeeklyMenuFragment weeklyMenuFragment;
    private ChatFragment chatFragment;


    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        recipeFragment = new RecipeFragment();
        weeklyMenuFragment = new WeeklyMenuFragment();
        chatFragment = new ChatFragment();
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return recipeFragment;
            case 1:
                return weeklyMenuFragment;
            case 2:
                return chatFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title;
        switch (position) {
            case 0:
                title = "Receptek";
                break;
            case 1:
                title = "Heti menü";
                break;
            case 2:
                title = "Chat";
                break;
            default:
                title = null;
        }
        return title;
    }

    public void addBundleToRecipeFragment(String title, String ingredients, String description) {
        Bundle bundle = new Bundle();
        bundle.putString(context.getString(R.string.bundle_title), title);
        bundle.putString(context.getString(R.string.bundle_ingredients), ingredients);
        bundle.putString(context.getString(R.string.bundle_description), description);
        recipeFragment.setArguments(bundle);
    }
}
