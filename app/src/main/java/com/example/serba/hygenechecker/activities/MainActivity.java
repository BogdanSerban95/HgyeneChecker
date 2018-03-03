package com.example.serba.hygenechecker.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.AAdvancedSearchParam;
import com.example.serba.hygenechecker.models.DataCache;
import com.example.serba.hygenechecker.models.Region;
import com.example.serba.hygenechecker.models.SearchParams;

public class MainActivity extends AppCompatActivity {
    public static final String SEARCH_PARAMS = "search_parameters";
    private static final int FINE_LOCATION_PERMISSION = 5512;

    private boolean isAdvancedSearch = false;
    private SearchParams searchParameters;
    private LocationListener locationListener;
    private Location currentLocation;

    private LocationManager locationManager;
    private EditText businessNameEditText;
    private EditText businessAddressEditText;
    private TextView radiusTextView;
    private SeekBar radiusSeekBar;
    private Button advancedSearchButton;
    private Button localSearchButton;
    private View advancedSearchGroup;
    private Spinner typeSpinner;
    private Spinner regionSpinner;
    private Spinner authoritySpinner;
    private RatingBar ratingBar;
    private CheckBox businessTypeCheckBox;
    private CheckBox ratingCheckBox;
    private CheckBox regionCheckBox;
    private CheckBox searchRadiusCheckBox;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        loadSpinners();

        setCheckboxListeners();

        searchParameters = new SearchParams();

        radiusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusTextView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        advancedSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isAdvancedSearch) {
                    advancedSearchButton.setText(getResources().getString(R.string.simple_search));
                    advancedSearchGroup.setVisibility(View.VISIBLE);
                    localSearchButton.setVisibility(View.GONE);
                } else {
                    advancedSearchButton.setText(getResources().getString(R.string.advanced_search));
                    advancedSearchGroup.setVisibility(View.GONE);
                    findViewById(R.id.auth_label).setVisibility(View.GONE);
                    authoritySpinner.setVisibility(View.GONE);
                    regionSpinner.setSelection(0);
//                    searchParameters.setLocalAuthorityId(null);
                    localSearchButton.setVisibility(View.VISIBLE);
                }
                isAdvancedSearch = !isAdvancedSearch;
            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    findViewById(R.id.auth_label).setVisibility(View.GONE);
                    authoritySpinner.setVisibility(View.GONE);
                    authoritySpinner.setSelection(0);
                    searchParameters.setLocalAuthorityId(null);
                } else {
                    authoritySpinner.setVisibility(View.VISIBLE);
                    findViewById(R.id.auth_label).setVisibility(View.VISIBLE);
                    Region selectedItem = (Region) regionSpinner.getSelectedItem();
                    ArrayAdapter adapter = (ArrayAdapter) authoritySpinner.getAdapter();
                    adapter.clear();
                    adapter.addAll(DataCache.getInstance().getAuthoritiesForRegion(selectedItem.getDisplayName()));
                    adapter.notifyDataSetChanged();
                }
                authoritySpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });

        localSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runLocationBasedSearch();
            }
        });
    }

    private void startSearchActivity() {
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        if (!getParametersFromViews())
            return;
        intent.putExtra(SEARCH_PARAMS, searchParameters);
        searchParameters = new SearchParams();
        startActivity(intent);
    }

    private void runLocationBasedSearch() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(getApplicationContext())
                    .setMessage(getResources().getString(R.string.location_permission))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestLocationPermissions();
                        }
                    })
                    .create()
                    .show();
        } else {
            retrieveCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrieveCurrentLocation();
                }
                return;
        }
    }

    private void retrieveCurrentLocation() {
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }

        if (!isGpsEnabled()) {
            openGpsSettings();
            return;
        }

        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
            return;
        }
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // If the last known location is not older than 15 minutes
        if (currentLocation != null && System.currentTimeMillis() - currentLocation.getTime() < 900000) {
            startLocationBasedSearch();
            return;
        }
        runLocationListener();
    }

    @SuppressLint("MissingPermission")
    private void runLocationListener() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.waiting_location));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (locationListener == null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    progressDialog.dismiss();
                    progressDialog = null;
                    currentLocation = location;
                    locationManager.removeUpdates(locationListener);
                    startLocationBasedSearch();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void startLocationBasedSearch() {
        if (!isAdvancedSearch) {
            searchParameters = new SearchParams();
            searchParameters.setMaxDistanceLimit("2");
        }
        searchParameters.setLatitude(String.valueOf(currentLocation.getLatitude()));
        searchParameters.setLongitude(String.valueOf(currentLocation.getLongitude()));
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        intent.putExtra(SEARCH_PARAMS, searchParameters);
        searchParameters = new SearchParams();
        startActivity(intent);
    }

    private void openGpsSettings() {
        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(R.string.gps_enable_message))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private boolean isGpsEnabled() {
        boolean gpsEnabled = false;
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {

        }
        return gpsEnabled;
    }

    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION);
    }

    private void setCheckboxListeners() {
        businessTypeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                typeSpinner.setEnabled(b);
                searchParameters.setBusinessTypeId(null);
            }
        });
        ratingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ratingBar.setEnabled(b);
                searchParameters.setRatingKey(null);
            }
        });
        regionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                regionSpinner.setEnabled(b);
                authoritySpinner.setEnabled(b);
                searchParameters.setCountryId(null);
                searchParameters.setLocalAuthorityId(null);
            }
        });
        searchRadiusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                radiusSeekBar.setEnabled(b);
                searchParameters.setBusinessTypeId(null);
            }
        });
    }

    private void loadSpinners() {
        typeSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DataCache.getInstance().getBusinessTypes()));
        regionSpinner.setAdapter(new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DataCache.getInstance().getRegions()));
        authoritySpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                DataCache
                        .getInstance()
                        .getAuthoritiesForRegion(
                                ((AAdvancedSearchParam) regionSpinner.getSelectedItem())
                                        .getDisplayName()
                        ))
        );
    }

    private boolean getParametersFromViews() {
        if (!areViewsValid())
            return false;
        searchParameters = new SearchParams();
        searchParameters.setAddress(businessAddressEditText.getText().toString());
        searchParameters.setName(businessNameEditText.getText().toString());

        if (isAdvancedSearch) {
            if (businessTypeCheckBox.isChecked()) {
                searchParameters.setBusinessTypeId(((AAdvancedSearchParam) typeSpinner.getSelectedItem()).getId());
            }
            if (ratingCheckBox.isChecked()) {
                searchParameters.setRatingKey(String.valueOf((int) ratingBar.getRating()));
            }
            if (regionCheckBox.isChecked()) {
                if (regionSpinner.getSelectedItemPosition() != 0) {
                    searchParameters.setLocalAuthorityId(((AAdvancedSearchParam) authoritySpinner.getSelectedItem()).getId());
                }
            }
            if (searchRadiusCheckBox.isChecked()) {
                searchParameters.setMaxDistanceLimit(radiusTextView.getText().toString());
                runLocationBasedSearch();
                return false;
            }
        }

        return true;
    }

    private boolean areViewsValid() {
        boolean valid = true;
        String message = null;

        if (isAdvancedSearch) {
            if (
                    !searchRadiusCheckBox.isChecked() &&
                            !regionCheckBox.isChecked() &&
                            !ratingCheckBox.isChecked() &&
                            !businessTypeCheckBox.isChecked() &&
                            noString(businessNameEditText.getText().toString()) &&
                            noString(businessAddressEditText.getText().toString())
                    ) {
                valid = false;
                message = getResources().getString(R.string.no_search_filter);
            }
        } else {
            if (businessAddressEditText.getText().toString().isEmpty() && businessNameEditText.getText().toString().isEmpty()) {
                valid = false;
                message = getResources().getString(R.string.empty_search_fields_error);
            }
        }
        if (!valid) {
            new AlertDialog.Builder(this)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setMessage(message)
                    .show();
        }
        return valid;
    }

    private boolean noString(String s) {
        if (s.isEmpty() && s.length() == 0)
            return true;
        return false;
    }

    private void initViews() {
        businessNameEditText = findViewById(R.id.business_name_edit_text);
        businessAddressEditText = findViewById(R.id.business_address_edit_text);
        radiusTextView = findViewById(R.id.radius_label);
        radiusSeekBar = findViewById(R.id.radius_seek_bar);
        advancedSearchButton = findViewById(R.id.advanced_search_button);
        localSearchButton = findViewById(R.id.local_search_button);
        advancedSearchGroup = findViewById(R.id.advanced_search_group);
        typeSpinner = findViewById(R.id.business_type_spinner);
        regionSpinner = findViewById(R.id.region_spinner);
        authoritySpinner = findViewById(R.id.authority_spinner);
        ratingBar = findViewById(R.id.rating_bar);
        businessTypeCheckBox = findViewById(R.id.business_type_cb);
        ratingCheckBox = findViewById(R.id.rating_cb);
        regionCheckBox = findViewById(R.id.region_auth_cb);
        searchRadiusCheckBox = findViewById(R.id.search_radius_cb);

    }

}
