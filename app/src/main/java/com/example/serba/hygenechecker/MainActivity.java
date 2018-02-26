package com.example.serba.hygenechecker;

import android.app.SearchManager;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

public class MainActivity extends AppCompatActivity {
    boolean isIs = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ConstraintSet constraintSet1 = new ConstraintSet();
        final ConstraintSet constraintSet2 = new ConstraintSet();

        constraintSet2.clone(this, R.layout.activity_main_list);
        setContentView(R.layout.activity_main);

        final ConstraintLayout constraintLayout = findViewById(R.id.main_constraint_layout);
        constraintSet1.clone(constraintLayout);

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(constraintLayout);
                if (isIs)
                    constraintSet2.applyTo(constraintLayout);
                else
                    constraintSet1.applyTo(constraintLayout);
                isIs = !isIs;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);
//        MenuItem searchMenuItem1 = menu.findItem(R.id.search1);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
//        searchView.setOnQueryTextListener(this);
        return true;
    }
}
