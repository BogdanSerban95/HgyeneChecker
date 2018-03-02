package com.example.serba.hygenechecker.models;

import java.util.ArrayList;

/**
 * Created by serba on 28/02/2018.
 */

public class DataCache {
    private static DataCache instance;
    private ArrayList<AAdvancedSearchParam> businessTypes;
    private ArrayList<AAdvancedSearchParam> regions;
    private ArrayList<AAdvancedSearchParam> authorities;

    private DataCache() {
        businessTypes = new ArrayList<>();
        regions = new ArrayList<>();
        authorities = new ArrayList<>();
    }

    public static synchronized DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }


    public ArrayList<AAdvancedSearchParam> getBusinessTypes() {
        return businessTypes;
    }

    public ArrayList<AAdvancedSearchParam> getRegions() {
        return regions;
    }

    public ArrayList<AAdvancedSearchParam> getAuthorities() {
        return authorities;
    }

    public void addAuthority(LocalAuthority authority) {
        if (authority != null) {
            this.authorities.add(authority);
        }
    }

    public void addRegion(Region region) {
        if (region != null) {
            this.regions.add(region);
        }
    }

    public void addBusinessType(BusinessType businessType) {
        if (businessType != null) {
            this.businessTypes.add(businessType);
        }
    }

    public ArrayList<AAdvancedSearchParam> getAuthoritiesForRegion(String regionName) {
        ArrayList<AAdvancedSearchParam> results = new ArrayList<>();
        for (AAdvancedSearchParam authority : this.authorities) {
            if (((LocalAuthority) authority).getRegionName().equals(regionName)) {
                results.add(authority);
            }
        }
        return results;
    }
}
