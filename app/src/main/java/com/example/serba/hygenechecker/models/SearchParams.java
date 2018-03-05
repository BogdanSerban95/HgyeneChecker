package com.example.serba.hygenechecker.models;

import android.util.Log;

import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by serba on 26/02/2018.
 */

public class SearchParams implements Serializable {
    private static String[] sortingOptions;
    private static String[] fhisRatingKeys;

    private String name;
    private String address;
    private String longitude;
    private String latitude;
    private String maxDistanceLimit;
    private String businessTypeId;
    private String ratingKey;
    private String localAuthorityId;
    private String countryId;
    private Integer pageSize;
    private Integer pageNumber;
    private String schemeTypeKey;
    private String sortOptionKey = "rating";

    public SearchParams() {
        this.resetPages();

        sortingOptions = new String[4];
        sortingOptions[0] = "rating";
        sortingOptions[1] = "desc_rating";
        sortingOptions[2] = "relevance";
        sortingOptions[3] = "distance";

        fhisRatingKeys = new String[5];
        fhisRatingKeys[0] = "Pass";
        fhisRatingKeys[1] = "ImprovementRequired";
        fhisRatingKeys[2] = "AwaitingPublication";
        fhisRatingKeys[3] = "AwaitingInspection";
        fhisRatingKeys[4] = "Exempt";
    }

    public String getName() {
        return name;
    }

    public void setName(String businessName) {
        this.name = businessName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setMaxDistanceLimit(String maxDistanceLimit) {
        this.maxDistanceLimit = maxDistanceLimit;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public void setRatingKey(String ratingKey) {
        this.ratingKey = ratingKey;
    }

    public void setFhisRatingKey(int pos) {
        this.ratingKey = fhisRatingKeys[pos];
    }

    public void setLocalAuthorityId(String localAuthorityId) {
        this.localAuthorityId = localAuthorityId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public void nextPage() {
        this.pageNumber++;
    }

    public Map<String, String> toHashMap() {
        HashMap<String, String> output = new HashMap<String, String>();
        Gson gson = new Gson();
        String jsonThis = gson.toJson(this);
        output = gson.fromJson(jsonThis, output.getClass());
        Log.e("hash", output.toString());
        return output;
    }

    public String toQueryString() {
        StringBuilder result = new StringBuilder();
        Map<String, String> paramsMap = this.toHashMap();
        if (paramsMap.keySet().size() == 0)
            return "";
        else
            result.append("?");
        for (String key : paramsMap.keySet()) {
            result.append(key)
                    .append("=")
                    .append(
                            String.valueOf(paramsMap.get(key))
                                    .replace(" ", "%20")
                                    .replace(".0", "")
                    )
                    .append("&");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setSortOptionKey(int pos) {
        if (pos == -1) {
            this.sortOptionKey = null;
            return;
        }

        sortOptionKey = sortingOptions[pos];
    }

    public void resetPages() {
        this.pageNumber = 1;
        this.pageSize = 25;
    }

    public boolean wasLocationUsed() {
        return latitude != null && longitude != null;
    }

    public void setSchemeTypeKey(String schemeTypeKey) {
        this.schemeTypeKey = schemeTypeKey;
    }

    public boolean isFHIS() {
        return this.schemeTypeKey != null;
    }
}
