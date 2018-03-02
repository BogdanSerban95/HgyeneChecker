package com.example.serba.hygenechecker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private boolean isAdvancedSearch = false;

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
    private SearchParams searchParameters;


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
                double progress = i / 10.0;
                radiusTextView.setText(String.valueOf(progress));
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
                    searchParameters.setLocalAuthorityId(null);
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
                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                if (!getParametersFromViews())
                    return;

                intent.putExtra(SEARCH_PARAMS, searchParameters);
                startActivity(intent);
            }
        });
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

        searchParameters.setAddress(businessAddressEditText.getText().toString());
        searchParameters.setName(businessNameEditText.getText().toString());
        if (businessTypeCheckBox.isChecked()) {
            searchParameters.setBusinessTypeId(((AAdvancedSearchParam) typeSpinner.getSelectedItem()).getId());
        }
        if (ratingCheckBox.isChecked()) {
            searchParameters.setRatingKey(String.valueOf((int) ratingBar.getRating()));
        }
        if (searchRadiusCheckBox.isChecked()) {
            searchParameters.setMaxDistanceLimit(radiusTextView.getText().toString());
        }
        if (regionCheckBox.isChecked()) {
            if (regionSpinner.getSelectedItemPosition() != 0)
                searchParameters.setLocalAuthorityId(((AAdvancedSearchParam) authoritySpinner.getSelectedItem()).getId());
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
