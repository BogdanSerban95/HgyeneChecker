package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.Establishment;
import com.example.serba.hygenechecker.models.RequestWrapper;
import com.example.serba.hygenechecker.models.ResultsAdapter;
import com.example.serba.hygenechecker.models.SearchParams;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    public static final String ESTABLISHMENT_ID = "establishment_id";
    private ResultsAdapter resultsAdapter;
    private SearchParams params;
    private boolean isLoading = false;
    private boolean endOfList = false;
    private boolean firstStart = true;

    private View listFooter;
    private ListView resultsListView;
    private ShimmerFrameLayout loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        resultsListView = findViewById(R.id.results_list_view);
        loadingView = findViewById(R.id.loading_view);
        loadingView.startShimmerAnimation();

        listFooter = LayoutInflater.from(this).inflate(R.layout.list_footer, resultsListView, false);
        List<Establishment> establishments = new ArrayList<>();
        params = (SearchParams) getIntent().getSerializableExtra(MainActivity.SEARCH_PARAMS);

        resultsAdapter = new ResultsAdapter(getApplicationContext(), R.layout.result_row, establishments);
        resultsListView.setAdapter(resultsAdapter);
        resultsListView.addFooterView(listFooter);


        requestEstablishments();

        resultsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (endOfList || isLoading)
                    return;
                if (i + i1 >= i2) {
                    if (params != null) {
                        params.nextPage();
                        Log.e("Infinite scroll", "Page: " + params.getPageNumber());
                        requestEstablishments();
                    }
                }
            }
        });

        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Establishment currentItem = resultsAdapter.getItem(i);
                if (currentItem != null) {
                    String id = currentItem.getFHRSID();
                    Intent intent = new Intent(getApplicationContext(), EstablishmentDetailsActivity.class);
                    intent.putExtra(ESTABLISHMENT_ID, id);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void requestEstablishments() {
        onLoadingStarted();
        RequestWrapper.getInstance(this).addJsonObjectRequest(Request.Method.GET, "Establishments", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                onLoadingDone();
                handleResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onLoadingDone();
                error.printStackTrace();
            }
        });
    }

    private void onLoadingStarted() {
        isLoading = true;
        listFooter.setVisibility(View.VISIBLE);
    }

    private void onLoadingDone() {
        isLoading = false;
        listFooter.setVisibility(View.GONE);
        if (firstStart) {
            loadingView.setVisibility(View.GONE);
            loadingView.stopShimmerAnimation();
            resultsListView.setVisibility(View.VISIBLE);
            firstStart = false;
        }
    }

    private void handleResponse(JSONObject response) {
        try {
            Gson gson = new Gson();
            JSONArray establishmentsArray = response.getJSONArray("establishments");
            if (establishmentsArray.length() < params.getPageSize()) {
                endOfList = true;
                resultsListView.removeFooterView(listFooter);
            }

            if (establishmentsArray.length() == 0) {
                findViewById(R.id.no_results_message).setVisibility(View.VISIBLE);
            }

            for (int i = 0; i < establishmentsArray.length(); i++) {
                JSONObject establishmentJson = establishmentsArray.getJSONObject(i);
                if (!establishmentJson.get("RatingValue").equals("Exempt")) {
                    Establishment establishment = gson.fromJson(establishmentJson.toString(), Establishment.class);
                    resultsAdapter.add(establishment);
                }
            }
            resultsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
