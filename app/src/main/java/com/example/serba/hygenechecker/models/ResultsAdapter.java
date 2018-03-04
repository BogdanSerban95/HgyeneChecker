package com.example.serba.hygenechecker.models;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.serba.hygenechecker.R;

import java.text.ParseException;
import java.util.List;

/**
 * Created by serba on 26/02/2018.
 */

public class ResultsAdapter extends ArrayAdapter<Establishment> {
    private Context context;
    private int resId;
    private List<Establishment> items;
    private boolean showFavourite = true;
    private boolean showDistance = true;

    public ResultsAdapter(@NonNull Context context, int resource, @NonNull List<Establishment> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resId = resource;
        this.items = objects;
    }

    public List<Establishment> getItems() {
        return this.items;
    }

    public void setShowFavourite(boolean value) {
        this.showFavourite = value;
    }

    public void setShowDistance(boolean value) {
        this.showDistance = value;
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
            holder.ratingBar = row.findViewById(R.id.row_rating_text_view);
            holder.distanceTextView = row.findViewById(R.id.distance_text_view);
            holder.favouriteStar = row.findViewById(R.id.row_favourite_star);
            holder.distanceGroup = row.findViewById(R.id.distance_group);
            holder.dateTextView = row.findViewById(R.id.row_date_text_view);

            if (!showDistance) {
                holder.distanceGroup.setVisibility(View.GONE);
            }
            if (!showFavourite) {
                holder.favouriteStar.setVisibility(View.GONE);
            }
            row.setTag(holder);
        } else {
            holder = (EstablishmentHolder) row.getTag();
        }

        Establishment currentItem = items.get(position);
        holder.nameTextView.setText(currentItem.getBusinessName());
        holder.typeTextView.setText(currentItem.getBusinessType());
        holder.ratingBar.setText(currentItem.getRatingValue());
        String date = "";
        try {
            date = Utils.formatDateString(currentItem.getRatingDate(), "yyyy-MM-dd'T'hh:mm:ss", "dd.MM.yyyy");
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.dateTextView.setText(date);

        if (showDistance) {
            String distanceString = String.format("%.2f", currentItem.getDistance()) + " " + context.getResources().getString(R.string.miles);
            holder.distanceTextView.setText(distanceString);
        }
        if (showFavourite) {
            if (DataCache.getInstance().getFavouritesIds().indexOf(currentItem.getFHRSID()) != -1) {
                holder.favouriteStar.setImageResource(R.drawable.star_on);
            } else {
                holder.favouriteStar.setImageResource(R.drawable.star_off);
            }
            holder.favouriteStar.setTag(currentItem);
            holder.favouriteStar.setOnClickListener(listener);
        }

        return row;
    }

    ImageView.OnClickListener listener = new ImageView.OnClickListener() {
        @Override
        public void onClick(View view) {
            Establishment est = (Establishment) view.getTag();
            if (est != null) {
                if (DataCache.getInstance().getFavouritesIds().indexOf(est.getFHRSID()) == -1) {
                    DatabaseInstance.getInstance().getDb(context).establishmentDao().insertEstablishment(est);
                    DataCache.getInstance().addFavouriteId(est.getFHRSID());
                    ((ImageView) view).setImageResource(R.drawable.star_on);
                    Toast.makeText(context, context.getResources().getString(R.string.favourite_add_message), Toast.LENGTH_SHORT).show();
                } else {
                    DatabaseInstance.getInstance().getDb(context).establishmentDao().deleteEstablishment(est);
                    ((ImageView) view).setImageResource(R.drawable.star_off);
                    DataCache.getInstance().removeFavouriteId(est.getFHRSID());
                    Toast.makeText(context, context.getResources().getString(R.string.favourite_remove_message), Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    private class EstablishmentHolder {
        public TextView nameTextView;
        public TextView typeTextView;
        public TextView ratingBar;
        public TextView distanceTextView;
        public ImageView favouriteStar;
        public TextView dateTextView;
        public View distanceGroup;
    }

}
