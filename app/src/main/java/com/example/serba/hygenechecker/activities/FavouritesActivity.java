package com.example.serba.hygenechecker.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.serba.hygenechecker.R;
import com.example.serba.hygenechecker.models.DatabaseInstance;
import com.example.serba.hygenechecker.models.Establishment;
import com.example.serba.hygenechecker.models.ResultsAdapter;

import java.util.Arrays;

public class FavouritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setTitle(getResources().getString(R.string.favourites));
        setActionBar();

        final ResultsAdapter adapter = new ResultsAdapter(
                this,
                R.layout.result_row,
                Arrays.asList(DatabaseInstance.getInstance()
                        .getDb(this)
                        .establishmentDao()
                        .retrieveEstablishments()
                )
        );
        adapter.setShowFavourite(false);
        adapter.setShowDistance(false);
        ListView favListView = findViewById(R.id.favourites_list_view);

        favListView.setAdapter(adapter);
        favListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Establishment est = adapter.getItem(i);
                if (est != null) {
                    Intent intent = new Intent(getApplicationContext(), EstablishmentDetailsActivity.class);
                    intent.putExtra(ResultsActivity.ESTABLISHMENT_ID, est.getFHRSID());
                    startActivity(intent);
                }
            }
        });
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
