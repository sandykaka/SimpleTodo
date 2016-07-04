package com.example.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<ItemsList> {

    private int layoutResource;

    public CustomAdapter(Context context, int layoutResource, List<ItemsList> itemsListList) {
        super(context, layoutResource, itemsListList);
        this.layoutResource = layoutResource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
        }

        ItemsList itemsList = getItem(position);

        if (itemsList != null) {
            TextView itemView = (TextView) view.findViewById(R.id.item);
            TextView priorityView = (TextView) view.findViewById(R.id.priority);

            if (itemView != null) {
                itemView.setText(itemsList.getItems());
            }

            if (priorityView != null) {
                priorityView.setText(itemsList.getPriority());
            }
        }

        return view;
    }
}