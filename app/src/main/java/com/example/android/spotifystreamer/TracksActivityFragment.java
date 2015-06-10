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
import java.util.List;


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
        String artist_id = "";
        if (intent != null && intent.getStringExtra("artist") != null) {
            artist_id = intent.getStringExtra("artist");
        }

        mTrackAdapter = new TrackAdapter(getActivity().getApplicationContext(), R.layout.list_item_track);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_tracks);
        listView.setAdapter(mTrackAdapter);

        new FetchTracksTask().execute(artist_id);

        return rootView;
    }

    private List<Track> getTracksDataFromJson(String tracksJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_OBJ = "tracks";

        final String OWM_ALBUM = "album";
        final String OWM_NAME = "name";
        final String OWM_IMAGES = "images";

        JSONObject tracksJson = new JSONObject(tracksJsonStr);
        JSONArray tracksArray = tracksJson.getJSONArray(OWM_OBJ);

        List<Track> tracks = new ArrayList<Track>();

        for (int i = 0; i < tracksArray.length(); i++) {
            JSONObject track = tracksArray.getJSONObject(i);

            JSONArray images = track.getJSONObject(OWM_ALBUM).getJSONArray(OWM_IMAGES);
            String imgUrl = images.length() == 0 ? "" : images.getJSONObject(0).getString("url");

            tracks.add(new Track(track.getString("id"), track.getString(OWM_NAME), imgUrl, track.getJSONObject(OWM_ALBUM).getString(OWM_NAME)));
        }

        return tracks;

    }

    public class FetchTracksTask extends AsyncTask<String, Void, List<Track>> {

        @Override
        protected List<Track> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String tracksJsonStr = null;

            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.spotify.com")
                        .appendPath("v1")
                        .appendPath("artists")
                        .appendPath(params[0])
                        .appendPath("top-tracks")
                        .appendQueryParameter("country", "FR")
                        .appendQueryParameter("limit", "4");
                String myUrl = builder.build().toString();
                Log.i("URL", myUrl);
                URL url = new URL(myUrl);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                tracksJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } catch (Exception e) {
                Log.e("PlaceholderFragment", "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                List<Track> tracks = getTracksDataFromJson(tracksJsonStr);
                return tracks;
            } catch (JSONException e) {
                Log.e("LOG", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
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
