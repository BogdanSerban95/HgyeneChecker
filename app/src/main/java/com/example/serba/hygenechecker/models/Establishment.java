package com.example.serba.hygenechecker.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by serba on 26/02/2018.
 */
@Entity
public class Establishment {
    @PrimaryKey
    @NonNull
    private String FHRSID;
    @ColumnInfo(name = "business_name")
    private String BusinessName;
    @ColumnInfo(name = "business_type")
    private String BusinessType;
    @ColumnInfo(name = "rating_value")
    private String RatingValue;
    @ColumnInfo(name = "rating_date")
    private String RatingDate;
    @Ignore
    private String AddressLine1;
    @Ignore
    private String AddressLine2;
    @Ignore
    private String AddressLine3;
    @Ignore
    private String AddressLine4;
    @Ignore
    private String PostCode;
    @Ignore
    private String LocalAuthorityCode;
    @Ignore
    private String LocalAuthorityName;
    @Ignore
    private String LocalAuthorityWebSite;
    @Ignore
    private String LocalAuthorityEmailAddress;
    @Ignore
    private Geocode geocode;
    @Ignore
    private double Distance;
    @Ignore
    private Scores scores;
    @Ignore
    private String SchemeType;

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

    public void setFHRSID(@NonNull String FHRSID) {
        this.FHRSID = FHRSID;
    }

    public void setBusinessName(String businessName) {
        BusinessName = businessName;
    }

    public void setBusinessType(String businessType) {
        BusinessType = businessType;
    }

    public void setRatingValue(String ratingValue) {
        RatingValue = ratingValue;
    }

    public void setRatingDate(String ratingDate) {
        RatingDate = ratingDate;
    }

    public String getSchemeType() {
        return SchemeType;
    }
}
