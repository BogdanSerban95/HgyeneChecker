package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.SearchParams;

public class MainActivity extends AppCompatActivity {
    public static final String SEARCH_PARAMS = "search_parameters";

    private TextInputEditText businessNameEditText;
    private TextInputEditText businessAddressEditText;
    private TextInputLayout nameInputLayout;
    private TextInputLayout addressInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

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
            addressInputLayout.setError(getResources().getString(R.string.empty_search_fields_error));
            nameInputLayout.setError(getResources().getString(R.string.empty_search_fields_error));
            valid = false;
        } else {
            addressInputLayout.setError(null);
            nameInputLayout.setError(null);
        }
        return valid;
    }

    private void initViews() {
        businessNameEditText = findViewById(R.id.business_name_edit_text);
        businessAddressEditText = findViewById(R.id.business_address_edit_text);
        nameInputLayout = findViewById(R.id.name_input_layout);
        addressInputLayout = findViewById(R.id.address_input_layout);

    }

}
