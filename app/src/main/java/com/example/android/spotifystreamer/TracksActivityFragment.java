package com.example.android.spotifystreamer;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.android.spotifystreamer.MusicService;
import android.widget.TextView;

import com.example.android.spotifystreamer.data.ArtistContract;
import com.example.android.spotifystreamer.data.ArtistProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import kaaes.spotify.webapi.android.models.TracksPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment {

    static final String TRACKS_URI = "URI";
    private TrackAdapter mTrackAdapter;

    private MusicService myService;
    private boolean isBind = false;

    public TracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        Bundle arguments = getArguments();
        if (arguments == null) {
            return rootView;
        }

        String artist_id = null;

        if (arguments != null) {
            artist_id = arguments.getString("id");
            System.out.println("houlala");
        }
        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, Mconnection, Context.BIND_AUTO_CREATE);

        mTrackAdapter = new TrackAdapter(getActivity().getApplicationContext(), R.layout.list_item_track);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTrackAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Track track = (Track) mTrackAdapter.getItem(position);
                //Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);

                HashMap<String, String> params_player = new HashMap<String, String>();
                params_player.put("id", track.id);
                params_player.put("album_name", track.album.name);
                params_player.put("album_artwork", track.album.images.get(0).url);
                params_player.put("track_name", track.name);
                params_player.put("track_duration", Objects.toString(track.duration_ms, "0"));
                //params_player.put("artist_name", params.get("name"));
                params_player.put("preview_url", track.preview_url);

                //playerIntent.putExtra("infos", params_player);

                //startActivity(playerIntent);
                FragmentManager fm = getFragmentManager();

                Bundle args = new Bundle();
                args.putString("id", track.id);
                args.putString("album_name", track.album.name);
                args.putString("album_artwork", track.album.images.get(0).url);
                args.putString("track_name", track.name);
                args.putString("track_duration", Objects.toString(track.duration_ms, "0"));
                args.putString("preview_url", track.preview_url);
                args.putInt("track_position", position);



                PlayerActivityFragment playerDialog = new PlayerActivityFragment();
                playerDialog.setArguments(args);
                playerDialog.show(fm, "fragment_player");
            }
        });

        new FetchTracksTask().execute(artist_id);

        return rootView;
    }

    private ServiceConnection Mconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder localService = (MusicService.MusicBinder) service;
            myService = localService.getService();
            isBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
                myService.setList(result);
                for (Track track : result) {
                    //ContentValues values = new ContentValues();

                    //values.put(ArtistContract.TrackEntry.COLUMN_ALBUM_NAME, track.album.name);
                    //values.put(ArtistContract.TrackEntry.COLUMN_ARTIST_ID, track.artists.get(0).id);
                    //values.put(ArtistContract.TrackEntry.COLUMN_ARTIST_NAME, track.artists.get(0).name);
                    //values.put(ArtistContract.TrackEntry.COLUMN_DURATION, track.duration_ms);
                    //values.put(ArtistContract.TrackEntry.COLUMN_PREVIEW_URL, track.album.images.get(0).url);
                    //values.put(ArtistContract.TrackEntry.COLUMN_TRACK_ID, track.id);
                    //values.put(ArtistContract.TrackEntry.COLUMN_TRACK_NAME, track.name);


                    //getActivity().getApplicationContext().getContentResolver().insert(ArtistContract.TrackEntry.CONTENT_URI, values);
                    mTrackAdapter.add(track);
                }
            }
        }
    }
}
