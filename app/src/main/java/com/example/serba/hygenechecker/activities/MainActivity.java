package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.serba.hygenechecker.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ConstraintSet constraintSet1 = new ConstraintSet();
        final ConstraintSet constraintSet2 = new ConstraintSet();

        constraintSet2.clone(this, R.layout.activity_mail_adv);
        constraintSet2.setVisibility(R.id.advanced_search_group, ConstraintSet.VISIBLE);
        setContentView(R.layout.activity_main);

        final ConstraintLayout constraintLayout = findViewById(R.id.main_constraint_layout);
        constraintSet1.clone(constraintLayout);

        initViews();

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
                TransitionManager.beginDelayedTransition(constraintLayout);
                if (!isAdvancedSearch) {
                    advancedSearchButton.setText(getResources().getString(R.string.simple_search));
                    constraintSet1.setVisibility(R.id.advanced_search_group, ConstraintSet.VISIBLE);
//                    constraintSet2.applyTo(constraintLayout);
//                    advancedSearchGroup.setVisibility(View.VISIBLE);
//                    localSearchButton.setVisibility(View.GONE);
                } else {
                    advancedSearchButton.setText(getResources().getString(R.string.advanced_search));
                    constraintSet1.setVisibility(R.id.advanced_search_group, ConstraintSet.GONE);
//                    constraintSet1.applyTo(constraintLayout);
//                    advancedSearchGroup.setVisibility(View.GONE);
//                    localSearchButton.setVisibility(View.VISIBLE);
                }
                isAdvancedSearch = !isAdvancedSearch;
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
    }

}
