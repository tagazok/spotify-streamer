package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import java.util.concurrent.Callable;

import javax.security.auth.callback.Callback;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistAdapter mArtistAdapter;
    private FetchArtistsTask fetchArtistTask;

    public MainActivityFragment() {

        //new FetchArtistsTask().execute("Coldplay");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        mArtistAdapter = new ArtistAdapter(getActivity().getApplicationContext(), R.layout.list_item_track);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_artists);

        listView.setAdapter(mArtistAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Artist artist = (Artist) mArtistAdapter.getItem(position);
                Intent tracksIntent = new Intent(getActivity(), TracksActivity.class);

                // http://stackoverflow.com/questions/7578236/how-to-send-hashmap-value-to-another-activity-using-an-intent
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", artist.id);
                params.put("name", artist.name);

                tracksIntent.putExtra("artist_info", params);
                startActivity(tracksIntent);
            }
        });

        final EditText searchEditText = (EditText) rootView.findViewById(R.id.artist_editText);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (fetchArtistTask != null) {
                    fetchArtistTask.cancel(true);
                }
                if (searchEditText.length() == 0) {
                    mArtistAdapter.getList().clear();
                    mArtistAdapter.notifyDataSetChanged();
                }
                mArtistAdapter.getList().clear();
                mArtistAdapter.notifyDataSetChanged();
                fetchArtistTask = new FetchArtistsTask();
                fetchArtistTask.execute(searchEditText.getText().toString());
            }
        });

        return rootView;
    }

    private class FetchArtistsTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            SpotifyApi api = new SpotifyApi();
            SpotifyService mSpotifyService = api.getService();
            ArtistsPager artistsPager = mSpotifyService.searchArtists(params[0]);

            return artistsPager.artists.items;
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            if (result != null) {
                for (Artist artist : result) {
                    mArtistAdapter.add(artist);
                }
                mArtistAdapter.notifyDataSetChanged();
            }
        }
    }

}
