package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.szantog.recipebook.R;

public class RecipeAddToDaysAdapter extends BaseAdapter {

    public interface OnListViewButtonClickListener {
        void onButtonClicked(ButtonItem buttonItem);
    }

    public class ButtonItem {
        private String type;
        private int index;

        public ButtonItem(String type, int index) {
            this.type = type;
            this.index = index;
        }

        public String getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }
    }

    private Context context;
    private String[] days;
    private OnListViewButtonClickListener listener;

    public static final String LUNCH = "lunch";
    public static final String DINNER = "dinner";

    public RecipeAddToDaysAdapter(Context context, String[] days) {
        this.context = context;
        this.days = days;
    }

    @Override
    public int getCount() {
        return days.length;
    }

    @Override
    public Object getItem(int i) {
        return days[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.addtodays_listitem, viewGroup, false);
        }
        TextView dayText = view.findViewById(R.id.recipe_addtodays_dialog_listview_daytext);
        Button lunch_btn = view.findViewById(R.id.recipe_addtodays_dialog_listview_lunch);
        Button dinner_btn = view.findViewById(R.id.recipe_addtodays_dialog_listview_dinner);
        dayText.setText(days[i]);
        lunch_btn.setTag(new ButtonItem(LUNCH, i));
        dinner_btn.setTag(new ButtonItem(DINNER, i));
        lunch_btn.setOnClickListener(clickListener);
        dinner_btn.setOnClickListener(clickListener);
        return view;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onButtonClicked((ButtonItem) view.getTag());
            }
        }
    };

    public void setOnListViewButtonClickListener(OnListViewButtonClickListener listener) {
        this.listener = listener;
    }

}
