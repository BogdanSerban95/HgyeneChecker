package com.example.serba.hygenechecker.models;

/**
 * Created by serba on 26/02/2018.
 */

public class Establishment {
    private String BusinessName;
    private String BusinessType;
    private String RatingValue;

    public Establishment() {

    }

    public String getBusinessName() {
        return BusinessName;
    }

    public void setBusinessName(String businessName) {
        BusinessName = businessName;
    }

    public String getBusinessType() {
        return BusinessType;
    }

    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }

    public String getRatingValue() {
        return RatingValue;
    }

    public void setRatingValue(String ratingValue) {
        RatingValue = ratingValue;
    }
}
