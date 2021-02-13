package com.example.szantog.recipebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntentReceiverActivity extends Activity implements View.OnClickListener {

    private TextView textView;
    private SpannableString spannableString;
    private BackgroundColorSpan titleColorSpan;
    private BackgroundColorSpan ingredientsColorSpan;
    private BackgroundColorSpan descriptionColorSpan;
    private String highlightedTitle = "";
    private String highlightedIngredients = "";
    private String highlightedDescription = "";

    private final int TITLE_MODE = 101;
    private final int INGREDIENTS_MODE = 102;
    private final int DESCRIPTION_MODE = 103;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intent_receiver_layout);

        final LinearLayout buttonsLayout = findViewById(R.id.intent_receiver_buttons_layout);
        Button titleButton = findViewById(R.id.intent_receiver_title_button);
        Button ingredientsButton = findViewById(R.id.intent_receiver_ingredients_button);
        Button descriptionButton = findViewById(R.id.intent_receiver_description_button);
        titleButton.setBackgroundColor(ContextCompat.getColor(this, R.color.intentreceiver_title));
        ingredientsButton.setBackgroundColor(ContextCompat.getColor(this, R.color.intentreceiver_ingredients));
        descriptionButton.setBackgroundColor(ContextCompat.getColor(this, R.color.intentreceiver_description));
        titleButton.setOnClickListener(this);
        ingredientsButton.setOnClickListener(this);
        descriptionButton.setOnClickListener(this);

        textView = findViewById(R.id.intent_receiver_text);
        final Button sendButton = findViewById(R.id.intent_receiver_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(IntentReceiverActivity.this, WrapperActivity.class);
                sendIntent.setAction(getString(R.string.share_text));
                sendIntent.putExtra(getString(R.string.share_text_intent_title), highlightedTitle);
                sendIntent.putExtra(getString(R.string.share_text_intent_ingredients), highlightedIngredients);
                sendIntent.putExtra(getString(R.string.share_text_intent_description), highlightedDescription);
                startActivity(sendIntent);
                finish();
            }
        });

        textView.setTextIsSelectable(true);
        textView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                buttonsLayout.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                buttonsLayout.setVisibility(View.INVISIBLE);

            }
        });
        textView.setText("Jelentős változásra figyelmeztetnek \naz okostelefonoknál - Aggódhat az Apple és a Samsung");
        Intent intent = getIntent();

        if (intent != null && intent.getAction() != null
                && intent.getAction().equals(Intent.ACTION_SEND)
                && intent.getType().equals("text/plain")) {
            textView.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
        }
        spannableString = new SpannableString(textView.getText().toString());
        titleColorSpan = new BackgroundColorSpan(ContextCompat.getColor(IntentReceiverActivity.this, R.color.intentreceiver_title));
        ingredientsColorSpan = new BackgroundColorSpan(ContextCompat.getColor(IntentReceiverActivity.this, R.color.intentreceiver_ingredients));
        descriptionColorSpan = new BackgroundColorSpan(ContextCompat.getColor(IntentReceiverActivity.this, R.color.intentreceiver_description));
    }

    private void textColoring(int mode) {
        switch (mode) {
            case TITLE_MODE:
                highlightedTitle = textView.getText().toString().substring(textView.getSelectionStart(), textView.getSelectionEnd());
                spannableString.removeSpan(titleColorSpan);
                spannableString.setSpan(titleColorSpan, textView.getSelectionStart(), textView.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
                break;
            case INGREDIENTS_MODE:
                highlightedIngredients = textView.getText().toString().substring(textView.getSelectionStart(), textView.getSelectionEnd());
                spannableString.removeSpan(ingredientsColorSpan);
                spannableString.setSpan(ingredientsColorSpan, textView.getSelectionStart(), textView.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
                break;
            case DESCRIPTION_MODE:
                highlightedDescription = textView.getText().toString().substring(textView.getSelectionStart(), textView.getSelectionEnd());
                spannableString.removeSpan(descriptionColorSpan);
                spannableString.setSpan(descriptionColorSpan, textView.getSelectionStart(), textView.getSelectionEnd(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.intent_receiver_title_button:
                textColoring(TITLE_MODE);
                break;
            case R.id.intent_receiver_ingredients_button:
                textColoring(INGREDIENTS_MODE);
                break;
            case R.id.intent_receiver_description_button:
                textColoring(DESCRIPTION_MODE);
                break;
            default:
                break;
        }
    }
}
