package com.example.serba.hygenechecker.models;

/**
 * Created by serba on 28/02/2018.
 */

public class LocalAuthority extends AAdvancedSearchParam {
    private String LocalAuthorityId;
    private String Name;
    private String RegionName;

    public String getRegionName() {
        return RegionName;
    }

    @Override
    public String getId() {
        return LocalAuthorityId;
    }

    @Override
    public String getDisplayName() {
        return Name;
    }
}
