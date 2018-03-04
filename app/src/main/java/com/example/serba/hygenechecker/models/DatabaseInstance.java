package com.example.serba.hygenechecker.models;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by serba on 04/03/2018.
 */

public class DatabaseInstance {
    private static DatabaseInstance instance;
    private FavouriteEstablishments db;

    private DatabaseInstance() {

    }

    public static synchronized DatabaseInstance getInstance() {
        if (instance == null) {
            instance = new DatabaseInstance();
        }
        return instance;
    }

    public FavouriteEstablishments getDb(Context context) {
        if (this.db == null) {
            db = Room.databaseBuilder(context, FavouriteEstablishments.class, "favourites")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return this.db;
    }
}
