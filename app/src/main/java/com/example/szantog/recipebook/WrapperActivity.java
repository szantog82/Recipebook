package com.example.szantog.recipebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.szantog.recipebook.adapters.ViewPagerAdapter;

public class WrapperActivity extends AppCompatActivity {

    public static final String SERVERURL = "https://recipes-szg.herokuapp.com";
    public static final String PASSWORD_KEY = "password_key";
    private TabLayout tabs;
    private ViewPager pager;
    private ViewPagerAdapter adapter;

    public static final String SHAREDPREF = "sharedprefs";
    public static final String STARTWITHWEEKLY_KEY = "startwithweekly_key";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapper_layout);

        /*Intent intent2 = new Intent(this, IntentReceiverActivity.class);
        startActivity(intent2);
        finish();*/

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setTabTextColors(ContextCompat.getColor(this, R.color.middleyellow), ContextCompat.getColor(this, R.color.lightyellow));
        tabs.setBackgroundColor(ContextCompat.getColor(this, R.color.darkblue));
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        adapter = new ViewPagerAdapter(this, getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        Intent intent = getIntent();
        if (intent.getAction() != null) {
            if (intent.getAction().equals(STARTWITHWEEKLY_KEY)) {
                tabs.getTabAt(1).select();
                pager.setCurrentItem(1);
            } else if (intent.getAction().equals(getString(R.string.share_text))) {
                adapter.addBundleToRecipeFragment(intent.getStringExtra(getString(R.string.share_text_intent_title)),
                        intent.getStringExtra(getString(R.string.share_text_intent_ingredients)),
                        intent.getStringExtra(getString(R.string.share_text_intent_description)));
            }
        }
    }
}
