package com.example.android.spotifystreamer;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        final HashMap<String, String> params = (HashMap<String, String>)intent.getSerializableExtra("infos");

        if(intent != null && params != null) {
            ((TextView) rootView.findViewById(R.id.artist_name)).setText(params.get("artist_name"));
            ((TextView) rootView.findViewById(R.id.album_name)).setText(params.get("album_name"));
            ((TextView) rootView.findViewById(R.id.track_name)).setText(params.get("track_name"));
            Long duration = Long.parseLong(params.get("track_duration"));
            String d = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

            ((TextView) rootView.findViewById(R.id.track_duration)).setText(d);
            Picasso.with(getActivity().getApplicationContext())
                    .load(params.get("album_artwork"))
                    .into((ImageView)rootView.findViewById(R.id.album_image));
        }

        return rootView;
    }
}
