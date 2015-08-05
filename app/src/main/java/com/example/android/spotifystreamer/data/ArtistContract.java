package com.example.android.spotifystreamer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by olivier on 30/07/15.
 */
public class ArtistContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.spotifystreamer.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRACKS = "artist";

    public static final class TrackEntry implements BaseColumns {

        public static final String TABLE_NAME = "track";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRACKS).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRACKS;


        public static final String COLUMN_TRACK_ID = "track_id";
        public static final String COLUMN_TRACK_NAME = "name";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_ARTIST_NAME = "artist_name";
        public static final String COLUMN_PREVIEW_URL = "url";

        public static Uri buildTrackUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTrackWithId(String artist_id) {
            return CONTENT_URI.buildUpon().appendPath(artist_id).build();
        }
    }
}
