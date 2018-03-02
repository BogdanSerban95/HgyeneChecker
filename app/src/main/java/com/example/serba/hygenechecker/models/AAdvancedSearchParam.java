package com.example.serba.hygenechecker.models;

/**
 * Created by serba on 02/03/2018.
 */

public abstract class AAdvancedSearchParam {
    public abstract String getId();

    public abstract String getDisplayName();

    @Override
    public String toString() {
        return this.getDisplayName();
    }
}
