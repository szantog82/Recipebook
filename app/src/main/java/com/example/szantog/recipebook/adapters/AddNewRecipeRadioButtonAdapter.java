package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

public class AddNewRecipeRadioButtonAdapter extends BaseAdapter {

    public interface OnRadioButtonClickListener {
        void onRadioButtonClicked(int selection);
    }

    private Context context;
    private String[] typesArray;
    private OnRadioButtonClickListener listener;

    public AddNewRecipeRadioButtonAdapter(Context context, String[] typesArray) {
        this.context = context;
        this.typesArray = typesArray;
    }

    @Override
    public int getCount() {
        return typesArray.length;
    }

    @Override
    public Object getItem(int i) {
        return typesArray[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        if (view == null) {
            view = new RadioButton(context);
        }
        RadioButton button = (RadioButton) view;
        button.setText(typesArray[i]);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton currentButton = (RadioButton) view;
                for (int j = 0; j < viewGroup.getChildCount(); j++) {
                    RadioButton btn = (RadioButton) viewGroup.getChildAt(j);
                    btn.setChecked(false);
                }
                currentButton.setChecked(true);
                if (listener != null) {
                    listener.onRadioButtonClicked(i);
                }
            }
        });
        return view;
    }

    public void setOnRadioButtonClickListener(OnRadioButtonClickListener listener) {
        this.listener = listener;
    }
}
