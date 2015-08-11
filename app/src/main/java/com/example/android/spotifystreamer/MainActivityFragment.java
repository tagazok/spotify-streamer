package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistAdapter mArtistAdapter;
    private int mPosition = ListView.INVALID_POSITION;
    private FetchArtistsTask fetchArtistTask;
    private static final String SELECTED_KEY = "selected_position";
    private boolean search_finished = false;
    private View rootView;

    private EditText searchEditText;
    private ListView mListView;

    public MainActivityFragment() {

    }

    public interface Callback {
        public void onItemSelected(Artist artist);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mArtistAdapter = new ArtistAdapter(getActivity().getApplicationContext(), R.layout.list_item_track);
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_artists);

        if (savedInstanceState != null) {

            //http://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
            Type type = new TypeToken<List<Artist>>() {
            }.getType();

            List<Artist> artists = new Gson().fromJson(savedInstanceState.getString("saved_artists_array"), type);
            mArtistAdapter.setList(artists);
        }
        mListView.setAdapter(mArtistAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Artist artist = (Artist) parent.getItemAtPosition(position);
                if (artist != null) {
                    ((Callback) getActivity())
                            .onItemSelected(artist);
                }
                mPosition = position;
            }
        });

        searchEditText = (EditText) rootView.findViewById(R.id.artist_editText);



        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    private class FetchArtistsTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {

            String queryString = params[0];

            if (queryString.length() == 0) {
                return new ArrayList<Artist>();
            } else {

                try {
                    SpotifyApi api = new SpotifyApi();
                    SpotifyService mSpotifyService = api.getService();
                    ArtistsPager artistsPager = mSpotifyService.searchArtists(params[0]);
                    return artistsPager.artists.items;
                } catch (RetrofitError e) {
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            if (result != null) {
                if (result.size() == 0) {
                    return;
                }

                for (Artist artist : result) {
                    mArtistAdapter.add(artist);
                }
                mArtistAdapter.notifyDataSetChanged();
            } else {
                Toast toast = Toast.makeText(getActivity(), "Error - Check your data connection", Toast.LENGTH_SHORT);
                toast.show();
            }
            search_finished = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("saved_artists_array", new Gson().toJson(mArtistAdapter.getList()));
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            return;
        }

        //http://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
        Type type = new TypeToken<List<Artist>>() {
        }.getType();

        List<Artist> artists = new Gson().fromJson(savedInstanceState.getString("saved_artists_array"), type);

        mArtistAdapter.setList(artists);
    }

    protected TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (fetchArtistTask != null) {
                fetchArtistTask.cancel(true);
            }
            if (searchEditText.length() == 0) {
                mArtistAdapter.getList().clear();
                mArtistAdapter.notifyDataSetChanged();
                return;
            }
            mArtistAdapter.getList().clear();
            mArtistAdapter.notifyDataSetChanged();
            fetchArtistTask = new FetchArtistsTask();
            fetchArtistTask.execute(searchEditText.getText().toString());
        }
    };

    // https://discussions.udacity.com/t/orientation-change-savedinstancestate-and-textchangedlistener/28591
    @Override
    public void onPause() {
        super.onPause();
        searchEditText.removeTextChangedListener(mTextWatcher);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchEditText.addTextChangedListener(mTextWatcher);
    }
}
