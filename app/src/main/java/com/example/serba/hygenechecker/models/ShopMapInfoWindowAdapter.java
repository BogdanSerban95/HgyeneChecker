package com.example.serba.hygenechecker.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.serba.hygenechecker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class ShopMapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public ShopMapInfoWindowAdapter(Context ctx) {
        this.context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(context).inflate(R.layout.info_window, null);
        TextView shopNameTextView = view.findViewById(R.id.info_shop_name);
        TextView shopTypeTextView = view.findViewById(R.id.info_shop_category);
        TextView shopDistanceTextView = view.findViewById(R.id.info_shop_distance);
        View shopDistanceGroup = view.findViewById(R.id.info_distance_group);
        RatingBar shopRatingBar = view.findViewById(R.id.info_shop_rating);

        Establishment currentItem = (Establishment) marker.getTag();
        if (currentItem != null) {
            shopNameTextView.setText(currentItem.getBusinessName());
            shopTypeTextView.setText(currentItem.getBusinessType());
            try {
                if (currentItem.getRatingValue().equals("0")) {
                    shopRatingBar.setVisibility(View.GONE);
                    view.findViewById(R.id.info_zero_stars).setVisibility(View.VISIBLE);
                } else {
                    shopRatingBar.setNumStars(Integer.parseInt(currentItem.getRatingValue()));
                }
            } catch (Exception ex) {
                shopRatingBar.setVisibility(View.GONE);
            }
            if (currentItem.getDistance() != 0) {
                shopDistanceGroup.setVisibility(View.VISIBLE);
                shopDistanceTextView.setText(String.format("%.2f", currentItem.getDistance()));
            }
        }

        return view;
    }
}
