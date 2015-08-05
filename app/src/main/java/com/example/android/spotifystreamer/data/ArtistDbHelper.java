package com.example.android.spotifystreamer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by olivier on 30/07/15.
 */
public class ArtistDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "track.db";

    public ArtistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRACK_TABLE = "CREATE TABLE " + ArtistContract.TrackEntry.TABLE_NAME + " (" +
                ArtistContract.TrackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ArtistContract.TrackEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL," +
                ArtistContract.TrackEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                ArtistContract.TrackEntry.COLUMN_ARTIST_NAME + " TEXT NOT NULL, " +
                ArtistContract.TrackEntry.COLUMN_DURATION + " INTEGER NOT NULL," +
                ArtistContract.TrackEntry.COLUMN_PREVIEW_URL + " TEXT NOT NULL," +
                ArtistContract.TrackEntry.COLUMN_TRACK_ID + " INTEGER NOT NULL, " +
                ArtistContract.TrackEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL" +
                " );";

        db.execSQL(SQL_CREATE_TRACK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArtistContract.TrackEntry.TABLE_NAME);
    }
}
