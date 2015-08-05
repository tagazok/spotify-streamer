package com.example.android.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import kaaes.spotify.webapi.android.models.Artist;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {

    private boolean mTwoPane;
    private static final String TRACKSFRAGMENT_TAG = "TFTAG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.fragment_tracks) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_tracks, new TracksActivityFragment(), TRACKSFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Maybe needed in Stage 2 of the project ^^
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Artist artist) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            //args.putParcelable(TracksActivityFragment.TRACKS_URI, uri);

            //HashMap<String, String> params = new HashMap<String, String>();
            args.putString("id", artist.id);
            args.putString("name", artist.name);


            TracksActivityFragment fragment = new TracksActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_tracks, fragment, TRACKSFRAGMENT_TAG)
                    .commit();
        } else {

            // http://stackoverflow.com/questions/7578236/how-to-send-hashmap-value-to-another-activity-using-an-intent
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id", artist.id);
            params.put("name", artist.name);

            Intent tracksIntent = new Intent(this, TracksActivity.class);
            tracksIntent.putExtra("artist_info", params);

            startActivity(tracksIntent);
        }
    }
}
