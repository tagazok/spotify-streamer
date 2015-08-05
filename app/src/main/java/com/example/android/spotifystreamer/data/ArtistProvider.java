package com.example.android.spotifystreamer.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by olivier on 30/07/15.
 */
public class ArtistProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ArtistDbHelper mOpenHelper;
    static final int TRACK = 100;

    @Override
    public boolean onCreate() {
        mOpenHelper = new ArtistDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        return ArtistContract.TrackEntry.CONTENT_TYPE;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        rowsUpdated = db.update(ArtistContract.TrackEntry.TABLE_NAME, values, selection,
                selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        long _id = db.insert(ArtistContract.TrackEntry.TABLE_NAME, null, values);
        if ( _id > 0 )
            returnUri = ArtistContract.TrackEntry.buildTrackUri(_id);
        else
            throw new android.database.SQLException("Failed to insert row into " + uri);

        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int rowsDeleted;
        if ( null == selection ) selection = "1";
        rowsDeleted = db.delete(
                ArtistContract.TrackEntry.TABLE_NAME, selection, selectionArgs);
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        retCursor = mOpenHelper.getReadableDatabase().query(
                ArtistContract.TrackEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArtistContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ArtistContract.PATH_TRACKS + "/#", TRACK);

        return matcher;
    }
}
