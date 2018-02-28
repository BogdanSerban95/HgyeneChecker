package com.example.serba.hygenechecker.models;

import java.util.ArrayList;

/**
 * Created by serba on 28/02/2018.
 */

public class DataCache {
    private static DataCache instance;
    private ArrayList<BusinessType> businessTypes;
    private ArrayList<Region> regions;
    private ArrayList<LocalAuthority> authorities;

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


    public ArrayList<BusinessType> getBusinessTypes() {
        return businessTypes;
    }

    public ArrayList<Region> getRegions() {
        return regions;
    }

    public ArrayList<LocalAuthority> getAuthorities() {
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
}
