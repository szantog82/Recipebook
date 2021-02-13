package com.example.szantog.recipebook.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.szantog.recipebook.R;

public class RecipeMainDialogBuilder extends AlertDialog.Builder {

    public RecipeMainDialogBuilder(Context context, String title, String ingredients, String description) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recipe_dialogbox_layout, null);

        setView(view);

        TextView titleView = view.findViewById(R.id.recipe_dialogbox_title);
        titleView.setText(title);
        final TextView ingredientsView = view.findViewById(R.id.recipe_dialogbox_ingredients);
        ingredientsView.setText(ingredients);
        final TextView descriptionView = view.findViewById(R.id.recipe_dialogbox_text);
        descriptionView.setText(description);

        ingredientsView.setTextSize(16);
        descriptionView.setTextSize(16);

        view.setOnTouchListener(new View.OnTouchListener() {
            float x0;
            float x1;
            float y0;
            float y1;
            double initDist;
            float size;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getPointerCount() == 2 && motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    double actualDist = Math.sqrt(Math.pow(motionEvent.getX(0) - motionEvent.getX(1), 2) +
                            Math.pow(motionEvent.getY(0) - motionEvent.getY(1), 2));
                    float diff = (float) ((actualDist - initDist) / 200 * 6);
                    size = 16 + diff;
                    if (size > 24) {
                        size = 24;
                    } else if (size < 10) {
                        size = 10;
                    }
                    ingredientsView.setTextSize(size);
                    descriptionView.setTextSize(size);
                } else if (motionEvent.getPointerCount() > 1) {
                    x0 = motionEvent.getX(0);
                    x1 = motionEvent.getX(1);
                    y0 = motionEvent.getY(0);
                    y1 = motionEvent.getY(1);
                    initDist = Math.sqrt(Math.pow(x0 - x1, 2) + Math.pow(y0 - y1, 2));
                } else if (motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_UP || motionEvent.getAction() == MotionEvent.ACTION_POINTER_UP || motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    return false;
                }
                return false;
            }
        });

    }
}


