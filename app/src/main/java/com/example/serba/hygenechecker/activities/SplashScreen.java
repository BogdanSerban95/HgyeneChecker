package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.BusinessType;
import com.example.serba.hygenechecker.models.DataCache;
import com.example.serba.hygenechecker.models.DatabaseInstance;
import com.example.serba.hygenechecker.models.LocalAuthority;
import com.example.serba.hygenechecker.models.Region;
import com.example.serba.hygenechecker.models.RequestWrapper;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {
    private boolean authoritiesLoaded = false;
    private boolean regionsLoaded = false;
    private boolean businessTypesLoaded = false;
    private boolean favouritesLoaded = false;

    private RequestWrapper requestWrapper;
    private DataCache dataCache;
    private Gson gson;

    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        shimmerFrameLayout = findViewById(R.id.loading_view);
        shimmerFrameLayout.startShimmerAnimation();

        requestWrapper = RequestWrapper.getInstance(getApplicationContext());
        dataCache = DataCache.getInstance();
        gson = new Gson();

        loadFavourites();
        loadBusinessTypes();
        loadRegions();
        loadAuthorities();

    }

    private void loadFavourites() {
        dataCache.setFavouritesIds(DatabaseInstance.getInstance().getDb(this).establishmentDao().getFavouriteEstablishmentsIds());
        this.favouritesLoaded = true;
        onDataLoaded();
    }

    private void loadAuthorities() {
        requestWrapper.addJsonObjectRequest(Request.Method.GET, "/authorities", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseAuthorityResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void parseAuthorityResponse(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("authorities");
            for (int i = 0; i < jsonArray.length(); i++) {
                LocalAuthority authority = gson.fromJson(jsonArray.getJSONObject(i).toString(), LocalAuthority.class);
                dataCache.addAuthority(authority);
            }
        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.authority_load_error), Toast.LENGTH_SHORT).show();
        }
        authoritiesLoaded = true;
        onDataLoaded();
    }


    private void loadRegions() {
        requestWrapper.addJsonObjectRequest(Request.Method.GET, "/regions", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseRegionsResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void parseRegionsResponse(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("regions");
            for (int i = 0; i < jsonArray.length(); i++) {
                Region region = gson.fromJson(jsonArray.getJSONObject(i).toString(), Region.class);
                dataCache.addRegion(region);
            }
        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.regions_load_error), Toast.LENGTH_SHORT).show();
        }
        regionsLoaded = true;
        onDataLoaded();
    }

    private void loadBusinessTypes() {
        requestWrapper.addJsonObjectRequest(Request.Method.GET, "/businesstypes", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                parseBusinessType(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void parseBusinessType(JSONObject response) {
        try {
            JSONArray jsonArray = response.getJSONArray("businessTypes");
            for (int i = 0; i < jsonArray.length(); i++) {
                BusinessType businessType = gson.fromJson(jsonArray.getJSONObject(i).toString(), BusinessType.class);
                dataCache.addBusinessType(businessType);
            }
        } catch (Exception ex) {
            Toast.makeText(this, getResources().getString(R.string.business_types_load_error), Toast.LENGTH_SHORT).show();
        }
        businessTypesLoaded = true;
        onDataLoaded();
    }

    private void onDataLoaded() {
        if (authoritiesLoaded && regionsLoaded && businessTypesLoaded) {
            shimmerFrameLayout.stopShimmerAnimation();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
