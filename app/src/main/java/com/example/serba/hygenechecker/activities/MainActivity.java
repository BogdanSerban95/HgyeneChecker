package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        loadSpinners();

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
                    localSearchButton.setVisibility(View.VISIBLE);
                }
                isAdvancedSearch = !isAdvancedSearch;
            }
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Region selectedItem = (Region) regionSpinner.getSelectedItem();
                ArrayAdapter adapter = (ArrayAdapter) authoritySpinner.getAdapter();
                adapter.clear();
                adapter.addAll(DataCache.getInstance().getAuthoritiesForRegion(selectedItem.getDisplayName()));
                authoritySpinner.setSelection(0);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                SearchParams searchParameters = getParametersFromViews();

                if (searchParameters == null)
                    return;

                intent.putExtra(SEARCH_PARAMS, searchParameters);
                startActivity(intent);
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

    private SearchParams getParametersFromViews() {
        if (!areViewsValid())
            return null;

        SearchParams parameters = new SearchParams();
        parameters.setAddress(businessAddressEditText.getText().toString());
        parameters.setName(businessNameEditText.getText().toString());

        return parameters;
    }

    private boolean areViewsValid() {
        boolean valid = true;
        if (businessAddressEditText.getText().toString().isEmpty() && businessNameEditText.getText().toString().isEmpty()) {
            valid = false;
        } else {
        }
        return valid;
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
    }

}
