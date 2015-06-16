package com.example.android.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment {

    private TrackAdapter mTrackAdapter;

    public TracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);
        HashMap<String, String> params = (HashMap<String, String>)intent.getSerializableExtra("artist_info");
        String artist_id = params.get("id");

        if (intent != null && intent.getStringExtra("artist") != null) {
            artist_id = intent.getStringExtra("artist");
        }

        mTrackAdapter = new TrackAdapter(getActivity().getApplicationContext(), R.layout.list_item_track);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTrackAdapter);

        new FetchTracksTask().execute(artist_id);

        return rootView;
    }

    public class FetchTracksTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService mSpotifyService = api.getService();
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(SpotifyService.COUNTRY, "FR");

            Tracks tracks = mSpotifyService.getArtistTopTrack(params[0], options);


            return tracks.tracks;
        }

        @Override
        protected void onPostExecute(List<Track> result) {
            if (result != null) {
                mTrackAdapter.clear();
                for (Track track : result) {
                    mTrackAdapter.add(track);
                }
            }
        }
    }
}
