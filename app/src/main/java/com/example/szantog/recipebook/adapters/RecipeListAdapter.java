package com.example.szantog.recipebook.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.models.RecipeItem;

import java.util.ArrayList;

public class RecipeListAdapter extends BaseAdapter {

    public interface RecipeListAdapterListener {
        void onAddToDaysClicked(int position, RecipeItem selectedItem);

        void onDeleteItemClicked(int position, RecipeItem selectedItem);
    }

    private Context context;
    private ArrayList<RecipeItem> items;
    private RecipeListAdapterListener listener;

    public RecipeListAdapter(Context context, ArrayList<RecipeItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.groupitem_layout, viewGroup, false);
        }
        ImageView img = view.findViewById(R.id.groupitem_image);
        if (items.get(pos).getName().contains("saláta"))
            img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.salad64));
        else if (items.get(pos).getName().contains("leves"))
            img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.soup64));
        else img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.meal64));
        TextView textView = view.findViewById(R.id.groupitem_text);
        textView.setText(items.get(pos).getName());

        ImageView addToDaysButton = view.findViewById(R.id.groupitem_addtodays);
        ImageView deleteItemButton = view.findViewById(R.id.groupitem_deleteitem);
        addToDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onAddToDaysClicked(pos, items.get(pos));
                }
            }
        });
        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onDeleteItemClicked(pos, items.get(pos));
                }
            }
        });
        return view;
    }

    public void setListener(RecipeListAdapterListener listener) {
        this.listener = listener;
    }
}
