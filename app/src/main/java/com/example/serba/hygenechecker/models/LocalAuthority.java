package com.example.serba.hygenechecker.models;

/**
 * Created by serba on 28/02/2018.
 */

public class LocalAuthority extends AAdvancedSearchParam {
    private String LocalAuthorityId;
    private String Name;
    private String RegionName;

    public LocalAuthority(String localAuthorityId, String name) {
        LocalAuthorityId = localAuthorityId;
        Name = name;
    }

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
