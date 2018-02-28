package com.example.serba.hygenechecker.models;

import java.util.Date;

/**
 * Created by serba on 26/02/2018.
 */

public class Establishment {
    private String FHRSID;
    private String BusinessName;
    private String BusinessType;
    private String RatingValue;
    private String AddressLine1;
    private String AddressLine2;
    private String AddressLine3;
    private String AddressLine4;
    private String PostCode;
    private String RatingDate;
    private String LocalAuthorityCode;
    private String LocalAuthorityName;
    private String LocalAuthorityWebSite;
    private String LocalAuthorityEmailAddress;
    private Geocode geocode;
    private double Distance;
    private Scores scores;

    public Establishment() {

    }

    public String getFHRSID() {
        return FHRSID;
    }

    public String getBusinessName() {
        return BusinessName;
    }

    public String getBusinessType() {
        return BusinessType;
    }


    public String getRatingValue() {
        return RatingValue;
    }

    public String getPostCode() {
        return PostCode;
    }

    public String getRatingDate() {
        return RatingDate;
    }

    public String getLocalAuthorityCode() {
        return LocalAuthorityCode;
    }

    public String getLocalAuthorityName() {
        return LocalAuthorityName;
    }

    public String getLocalAuthorityWebSite() {
        return LocalAuthorityWebSite;
    }

    public String getLocalAuthorityEmailAddress() {
        return LocalAuthorityEmailAddress;
    }

    public Geocode getGeocode() {
        return geocode;
    }

    public double getDistance() {
        return Distance;
    }

    public Scores getScores() {
        return scores;
    }

    public String getAddressLine1() {
        return AddressLine1;
    }

    public String getAddressLine2() {
        return AddressLine2;
    }

    public String getAddressLine3() {
        return AddressLine3;
    }

    public String getAddressLine4() {
        return AddressLine4;
    }

    public String getAddressFirstLine() {
        return this.getAddressLine1() + ", " + this.getAddressLine2();
    }

    public String getAddressSecondLine() {
        return this.getAddressLine3() + ", " + this.getAddressLine4();
    }
}
