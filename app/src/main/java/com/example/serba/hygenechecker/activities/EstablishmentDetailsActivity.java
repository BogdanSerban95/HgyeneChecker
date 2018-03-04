package com.example.serba.hygenechecker.activities;

import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.Establishment;
import com.example.serba.hygenechecker.models.RequestWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONObject;

public class EstablishmentDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Establishment currentItem;
    private GoogleMap googleMap;
    private boolean locationAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment_details);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RequestWrapper requestWrapper = RequestWrapper.getInstance(getApplicationContext());

        String id = getIntent().getStringExtra(ResultsActivity.ESTABLISHMENT_ID);
        requestWrapper.addJsonObjectRequest(Request.Method.GET, "establishments/" + id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        Gson gson = new Gson();
                        currentItem = gson.fromJson(response.toString(), Establishment.class);
                        if (currentItem != null) {
                            setTitle(currentItem.getBusinessName());
                            fillViews(currentItem);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EstablishmentDetailsActivity.this, "Error occurred while retrieving data...", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_view);
        mapFragment.getMapAsync(this);
    }

    private void fillViews(Establishment currentItem) {
        ((TextView) findViewById(R.id.business_name_tv)).setText(currentItem.getBusinessName());
        ((TextView) findViewById(R.id.business_type_tv)).setText(currentItem.getBusinessType());
        ((TextView) findViewById(R.id.address_first_line_tv)).setText(currentItem.getAddressFirstLine());
        ((TextView) findViewById(R.id.address_second_line_tv)).setText(currentItem.getAddressSecondLine());
        ((TextView) findViewById(R.id.post_code_tv)).setText(currentItem.getPostCode());
        ((TextView) findViewById(R.id.auth_name_tv)).setText(currentItem.getLocalAuthorityName());
        ((TextView) findViewById(R.id.auth_email_tv)).setText(currentItem.getLocalAuthorityEmailAddress());
        ((TextView) findViewById(R.id.auth_website_tv)).setText(currentItem.getLocalAuthorityWebSite());
        try {
            int score = Integer.parseInt(currentItem.getRatingValue());
            ((ImageView) findViewById(R.id.rating_image_view)).setImageResource(getRatingImage(score));
        } catch (NumberFormatException ex) {

        }
        addLocationOnMap();
    }

    private int getRatingImage(int score) {
        switch (score) {
            case 0:
                return R.drawable.rating_0;
            case 1:
                return R.drawable.rating_1;
            case 2:
                return R.drawable.rating_2;
            case 3:
                return R.drawable.rating_3;
            case 4:
                return R.drawable.rating_4;
            case 5:
                return R.drawable.rating_5;
        }
        return 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        addLocationOnMap();
    }

    private void addLocationOnMap() {
        if (googleMap == null || currentItem == null || locationAdded) {
            return;
        }

        locationAdded = true;
        googleMap.addMarker(new MarkerOptions().position(currentItem.getGeocode().toLatLong()));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentItem.getGeocode().toLatLong()));
    }
}
