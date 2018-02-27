package com.example.serba.hygenechecker.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.Establishment;
import com.example.serba.hygenechecker.models.RequestWrapper;
import com.example.serba.hygenechecker.models.ResultsAdapter;
import com.example.serba.hygenechecker.models.SearchParams;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private ResultsAdapter resultsAdapter;
    private SearchParams params;
    private Request<JSONObject> resultsRequest;
    private boolean isLoading = false;
    private boolean endOfList = false;
    private boolean loaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        List<Establishment> establishments = new ArrayList<>();
        params = (SearchParams) getIntent().getSerializableExtra(MainActivity.SEARCH_PARAMS);

        resultsAdapter = new ResultsAdapter(getApplicationContext(), R.layout.result_row, establishments);
        ((ListView) findViewById(R.id.results_list_view)).setAdapter(resultsAdapter);
//        ((ListView) findViewById(R.id.results_list_view)).setOnScrollListener(new EndlessScrollListener() {
//            @Override
//            public boolean onLoadMore(int page, int totalItemsCount) {
//                params.setPage(page);
//                RequestWrapper.getInstance(getApplicationContext()).addJsonObjectRequest(Request.Method.GET, "Establishments", params, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        handleResponse(response);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                });
//                return true;
//            }
//        });

        ((ListView) findViewById(R.id.results_list_view)).setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if (endOfList || isLoading || !loaded)
                    return;
                if (i + i1 >= i2) {
                    if (params != null) {
                        isLoading = true;
                        params.nextPage();
                        Log.e("Infinite scroll", "Page: " + params.getPageNumber());
                        RequestWrapper.getInstance(getApplicationContext()).addRequest(resultsRequest);
                    }
                }
            }
        });


        resultsRequest = RequestWrapper.getInstance(this).addJsonObjectRequest(Request.Method.GET, "Establishments", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading = false;
                handleResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading = false;
                error.printStackTrace();
            }
        });

        loaded = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void handleResponse(JSONObject response) {
        try {
            Gson gson = new Gson();
            JSONArray establishmentsArray = response.getJSONArray("establishments");
            Log.e("Result", String.valueOf(establishmentsArray.length()));
            Log.e("Result", String.valueOf(params.getPageSize()));
            if (establishmentsArray.length() < params.getPageSize()) {
                endOfList = true;
                Log.e("end of list", String.valueOf(endOfList));
            }

            for (int i = 0; i < establishmentsArray.length(); i++) {
                Establishment establishment = gson.fromJson(establishmentsArray.getJSONObject(i).toString(), Establishment.class);
                resultsAdapter.add(establishment);
            }
            resultsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
