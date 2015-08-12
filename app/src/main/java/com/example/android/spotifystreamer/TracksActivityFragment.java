package com.example.android.spotifystreamer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment {

    private TrackAdapter mTrackAdapter;

    private MusicService myService;
    private boolean isBind = false;

    public TracksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);

        mTrackAdapter = new TrackAdapter(getActivity().getApplicationContext(), R.layout.list_item_track);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);

        if (savedInstanceState != null) {
            Type type = new TypeToken<List<Track>>() {
            }.getType();

            List<Artist> tracks = new Gson().fromJson(savedInstanceState.getString("saved_artists_array"), type);
            mTrackAdapter.setList(tracks);
        } else {

            Bundle arguments = getArguments();
            if (arguments == null) {
                return rootView;
            }

            String artist_id = null;

            if (arguments != null) {
                artist_id = arguments.getString("id");
            }

            new FetchTracksTask().execute(artist_id);
        }

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, Mconnection, Context.BIND_AUTO_CREATE);

        listView.setAdapter(mTrackAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (getActivity().findViewById(R.id.fragment_artists) != null) {

                    FragmentManager fm = getFragmentManager();

                    Bundle args = new Bundle();
                    args.putInt("track_position", position);

                    PlayerActivityFragment playerDialog = new PlayerActivityFragment();
                    playerDialog.setArguments(args);
                    playerDialog.show(fm, "fragment_player");
                } else {
                    Intent playerIntent = new Intent(getActivity(), PlayerActivity.class);
                    playerIntent.putExtra("track_position", position);

                    startActivity(playerIntent);
                }
            }
        });

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
                return new ArrayList<Track>();
            }

            try {
                SpotifyApi api = new SpotifyApi();
                SpotifyService mSpotifyService = api.getService();
                Map<String, Object> options = new HashMap<String, Object>();
                options.put(SpotifyService.COUNTRY, "FR");

                Tracks tracks = mSpotifyService.getArtistTopTrack(params[0], options);

                return tracks.tracks;
            } catch (
                    RetrofitError e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Track> result) {
            if (result != null) {
                mTrackAdapter.clear();
                myService.setList(result);
                for (Track track : result) {
                    mTrackAdapter.add(track);
                }
            } else {
                Toast toast = Toast.makeText(getActivity(), "Error - Check your data connection", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("saved_artists_array", new Gson().toJson(mTrackAdapter.getList()));
    }
}
