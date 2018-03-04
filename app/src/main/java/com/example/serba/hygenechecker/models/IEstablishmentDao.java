package com.example.serba.hygenechecker.models;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.ArrayList;

/**
 * Created by serba on 04/03/2018.
 */
@Dao
public interface IEstablishmentDao {
    @Insert
    void insertEstablishment(Establishment establishment);

    @Delete
    void deleteEstablishment(Establishment establishment);

    @Query("SELECT * FROM establishment")
    Establishment[] retrieveEstablishments();

    @Query("SELECT FHRSID FROM establishment")
    String[] getFavouriteEstablishmentsIds();
}
