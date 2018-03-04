package com.example.serba.hygenechecker.models;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.serba.hygenechecker.R;

import java.util.List;

/**
 * Created by serba on 26/02/2018.
 */

public class ResultsAdapter extends ArrayAdapter<Establishment> {
    private Context context;
    private int resId;
    private List<Establishment> items;

    public ResultsAdapter(@NonNull Context context, int resource, @NonNull List<Establishment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.items = objects;
    }

    public List<Establishment> getItems() {
        return this.items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        EstablishmentHolder holder = null;

        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            row = layoutInflater.inflate(this.resId, parent, false);
            holder = new EstablishmentHolder();
            holder.nameTextView = row.findViewById(R.id.row_name_text_view);
            holder.typeTextView = row.findViewById(R.id.row_type_text_view);
            holder.ratingTextView = row.findViewById(R.id.row_rating_text_view);
            holder.distanceTextView = row.findViewById(R.id.distance_text_view);
            row.setTag(holder);
        } else {
            holder = (EstablishmentHolder) row.getTag();
        }

        Establishment currentItem = items.get(position);
        holder.nameTextView.setText(currentItem.getBusinessName());
        holder.typeTextView.setText(currentItem.getBusinessType());
        holder.ratingTextView.setText(String.valueOf(currentItem.getRatingValue()));
        String distanceString = String.format("%.2f", currentItem.getDistance()) + " " + context.getResources().getString(R.string.miles);
        holder.distanceTextView.setText(distanceString);
        return row;
    }

    private class EstablishmentHolder {
        public TextView nameTextView;
        public TextView typeTextView;
        public TextView ratingTextView;
        public TextView distanceTextView;
    }

}
