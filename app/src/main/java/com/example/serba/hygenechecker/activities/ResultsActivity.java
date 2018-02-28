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
import android.widget.Spinner;
import android.widget.TextView;

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
    private TextView resultsCountTextView;
    private Spinner sortingSpinner;
    private View topBannerGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        params = (SearchParams) getIntent().getSerializableExtra(MainActivity.SEARCH_PARAMS);

        setActionBar();
        setViews();

        List<Establishment> establishments = new ArrayList<>();

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

        sortingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                params.resetPages();
                params.setSortOptionKey(i);
                resultsAdapter.clear();
                endOfList = false;
                firstStart = true;
                resultsListView.setVisibility(View.GONE);
                loadingView.setVisibility(View.VISIBLE);
                loadingView.startShimmerAnimation();
                resultsCountTextView.setText("0");
                requestEstablishments();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setViews() {
        sortingSpinner = findViewById(R.id.sort_spinner);
        topBannerGroup = findViewById(R.id.results_top_banner);
        resultsListView = findViewById(R.id.results_list_view);
        resultsCountTextView = findViewById(R.id.results_count_tv);
        loadingView = findViewById(R.id.loading_view);

        loadingView.startShimmerAnimation();
        listFooter = LayoutInflater.from(this).inflate(R.layout.list_footer, resultsListView, false);
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void requestEstablishments() {
        onLoadingStarted();
        RequestWrapper.getInstance(this).addJsonObjectRequest(Request.Method.GET, "Establishments", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                handleResponse(response);
                onLoadingDone();
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
            resultsListView.smoothScrollToPosition(0);
            firstStart = false;
            if (!resultsAdapter.isEmpty())
                topBannerGroup.setVisibility(View.VISIBLE);
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
            resultsCountTextView.setText(String.valueOf(resultsAdapter.getCount()));
            resultsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
