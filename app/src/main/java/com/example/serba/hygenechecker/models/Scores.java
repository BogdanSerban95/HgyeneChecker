package com.example.serba.hygenechecker.models;

/**
 * Created by serba on 27/02/2018.
 */

public class Scores {
    private int Hygiene;
    private int Structural;
    private int ConfidenceInManagement;

    public Scores() {
    }

    public int getHygiene() {
        return Hygiene;
    }

    public void setHygiene(int hygiene) {
        Hygiene = hygiene;
    }

    public int getStructural() {
        return Structural;
    }

    public void setStructural(int structural) {
        Structural = structural;
    }

    public int getConfidenceInManagement() {
        return ConfidenceInManagement;
    }

    public void setConfidenceInManagement(int confidenceInManagement) {
        ConfidenceInManagement = confidenceInManagement;
    }
}
