package com.example.android.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArtistAdapter mArtistAdapter;

    public MainActivityFragment() {
        new FetchArtistsTask().execute("Coldplay");
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
                Artist artist = (Artist)mArtistAdapter.getItem(position);
                Intent tracksIntent = new Intent(getActivity(), TracksActivity.class);

                tracksIntent.putExtra("artist", artist.getId());
                startActivity(tracksIntent);
            }
        });

        return rootView;
    }

    private List<Artist> getArtistDataFromJson(String artistsJsonStr)
    throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_OBJ = "artists";

        final String OWM_ITEMS = "items";
        final String OWM_IMAGES = "images";

        JSONObject artistsJson = new JSONObject(artistsJsonStr);
        JSONArray itemsArray = artistsJson.getJSONObject(OWM_OBJ).getJSONArray(OWM_ITEMS);

        List<Artist> artists = new ArrayList<Artist>();

        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject artist = itemsArray.getJSONObject(i);
            JSONArray images = artist.getJSONArray(OWM_IMAGES);
            String imgUrl = images.length() == 0 ? "" : images.getJSONObject(0).getString("url");
            artists.add(new Artist(artist.getString("id"), artist.getString("name"), imgUrl));
        }

        return artists;
    }

    private class FetchArtistsTask extends AsyncTask<String, Void, List<Artist>> {
        @Override
        protected List<Artist> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String artistsJsonStr = null;

            try {

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("https")
                        .authority("api.spotify.com")
                        .appendPath("v1")
                        .appendPath("search")
                        .appendQueryParameter("q", params[0])
                        .appendQueryParameter("type", "artist");
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
                artistsJsonStr = buffer.toString();
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
                return getArtistDataFromJson(artistsJsonStr);
            } catch (JSONException e) {
                Log.e("LOG", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Artist> result) {
            if (result != null) {
                for (Artist artist : result) {
                    mArtistAdapter.add(artist);
                }
            }
        }
    }


}
