package com.example.serba.hygenechecker.models;

/**
 * Created by serba on 28/02/2018.
 */

public class Region extends AAdvancedSearchParam {
    private String id;
    private String name;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

}
