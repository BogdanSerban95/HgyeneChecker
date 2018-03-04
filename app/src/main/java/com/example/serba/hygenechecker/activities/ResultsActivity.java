package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.serba.hygenechecker.models.ShopMapInfoWindowAdapter;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements OnMapReadyCallback {
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
    private ArrayList<String> filterOptions;
    private BottomNavigationView bottomNav;

    private ConstraintLayout mainLayout;
    private View mapWrapper;
    private GoogleMap googleMap;
    private boolean isMapLoaded;
    private Button loadMoreButton;
    private boolean addToMap = false;
    private View mapProgressBar;
    private boolean mapMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_results);

        params = (SearchParams) getIntent().getSerializableExtra(MainActivity.SEARCH_PARAMS);

        setActionBar();
        setViews();

        List<Establishment> establishments = new ArrayList<>();
        filterOptions = new ArrayList<>();

        resultsAdapter = new ResultsAdapter(getApplicationContext(), R.layout.result_row, establishments);
        resultsListView.setAdapter(resultsAdapter);
        resultsListView.addFooterView(listFooter);

        requestEstablishments();

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (endOfList || isLoading)
                    return;
                if (params != null) {
                    params.nextPage();
                    addToMap = true;
                    requestEstablishments();
                }
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_list:
                        TransitionManager.beginDelayedTransition(mainLayout);
                        mapMode = false;
                        mapWrapper.setVisibility(View.GONE);
                        topBannerGroup.setVisibility(View.VISIBLE);
                        resultsListView.setVisibility(View.VISIBLE);
                        loadMoreButton.setVisibility(View.GONE);
                        break;
                    case R.id.menu_map:
                        TransitionManager.beginDelayedTransition(mainLayout);
                        mapMode = true;
                        mapWrapper.setVisibility(View.VISIBLE);
                        topBannerGroup.setVisibility(View.GONE);
                        resultsListView.setVisibility(View.GONE);
                        loadMoreButton.setVisibility(View.VISIBLE);
                        pinEstablishmentsOnMap(true);
                        break;
                }
                return true;
            }
        });

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
                    startDetailsActivity(id);
                }
            }
        });
        String[] filters = getResources().getStringArray(R.array.sort_options);
        filterOptions.addAll(Arrays.asList(filters));

        if (!params.wasLocationUsed()) {
            filterOptions.remove(filterOptions.size() - 1);
        }
        sortingSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filterOptions));
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

    private void startDetailsActivity(String id) {
        Intent intent = new Intent(getApplicationContext(), EstablishmentDetailsActivity.class);
        intent.putExtra(ESTABLISHMENT_ID, id);
        startActivity(intent);
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
        bottomNav = findViewById(R.id.bottom_navigation);
        mainLayout = findViewById(R.id.main_layout);
        mapWrapper = findViewById(R.id.map_wrapper);
        loadMoreButton = findViewById(R.id.load_more_button);
        mapProgressBar = findViewById(R.id.map_progress);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.results_map_view);
        mapFragment.getMapAsync(this);

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

    private void requestEstablishments() {
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

    private void pinEstablishmentsOnMap(boolean moveCamera) {
        if (!isMapLoaded) {
            return;
        }
        boolean cameraMoved = false;
        for (Establishment est : this.resultsAdapter.getItems()) {
            if (est.getGeocode() != null) {
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(est.getGeocode().toLatLong())
                                .title(est.getBusinessName())
                                .snippet("sadasdasdasds dadasdasd")
                );

                marker.setTag(est);
                if (!cameraMoved && moveCamera) {
                    googleMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(est.getGeocode().toLatLong(), 10)
                    );
                    cameraMoved = true;
                }
            }
        }

    }

    private void onLoadingStarted() {
        isLoading = true;
        listFooter.setVisibility(View.VISIBLE);
        bottomNav.setEnabled(false);
        if (mapMode) {
            mapProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void onLoadingDone() {
        isLoading = false;
        listFooter.setVisibility(View.GONE);
        bottomNav.setEnabled(false);
        bottomNav.setEnabled(false);
        if (mapMode) {
            mapProgressBar.setVisibility(View.GONE);
        }
        if (firstStart) {
            loadingView.setVisibility(View.GONE);
            loadingView.stopShimmerAnimation();
            resultsListView.setVisibility(View.VISIBLE);
            resultsListView.smoothScrollToPosition(0);
            firstStart = false;
            if (!resultsAdapter.isEmpty())
                topBannerGroup.setVisibility(View.VISIBLE);
        } else {
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

            if (addToMap) {
                pinEstablishmentsOnMap(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setInfoWindowAdapter(new ShopMapInfoWindowAdapter(getApplicationContext()));

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Establishment est = (Establishment) marker.getTag();
                if (est != null) {
                    startDetailsActivity(est.getFHRSID());
                }
            }
        });

        this.isMapLoaded = true;
    }
}
