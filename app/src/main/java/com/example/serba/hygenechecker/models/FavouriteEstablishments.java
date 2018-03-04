package com.example.serba.hygenechecker.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by serba on 04/03/2018.
 */

@Database(entities = Establishment.class, version = 2, exportSchema = false)
public abstract class FavouriteEstablishments extends RoomDatabase {
    public abstract IEstablishmentDao establishmentDao();
}
