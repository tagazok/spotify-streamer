package com.example.android.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends Fragment {

    private HashMap<String, String> params = null;
    private View rootView = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();
    private TextView currentDuration = null;

    public PlayerActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        params = (HashMap<String, String>) intent.getSerializableExtra("infos");

        final ImageButton button_play = (ImageButton) rootView.findViewById(R.id.button_play);
        final ImageButton button_pause = (ImageButton) rootView.findViewById(R.id.button_pause);

        if (intent != null && params != null) {
            ((TextView) rootView.findViewById(R.id.artist_name)).setText(params.get("artist_name"));
            ((TextView) rootView.findViewById(R.id.album_name)).setText(params.get("album_name"));
            ((TextView) rootView.findViewById(R.id.track_name)).setText(params.get("track_name"));
            currentDuration = ((TextView) rootView.findViewById((R.id.current_duration)));
            Long duration = Long.parseLong(params.get("track_duration"));
            String d = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(duration) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

            ((TextView) rootView.findViewById(R.id.track_duration)).setText(d);
            Picasso.with(getActivity().getApplicationContext())
                    .load(params.get("album_artwork"))
                    .into((ImageView) rootView.findViewById(R.id.album_image));

            String url = params.get("preview_url"); // your URL here

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
            } catch (IOException io) {

            }
            mediaPlayer.start();
            button_play.setVisibility(View.GONE);
            button_pause.setVisibility(View.VISIBLE);

            final SeekBar progress = ((SeekBar) rootView.findViewById(R.id.duration_seekbar));

            progress.setMax(mediaPlayer.getDuration());
            final int total = mediaPlayer.getDuration();
            progress.setMax(total);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int currentPosition = 0;
                    while (mediaPlayer != null && currentPosition < total) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            return;
                        } catch (Exception e) {
                            return;
                        }
                        currentPosition = mediaPlayer.getCurrentPosition();
                        progress.setProgress(currentPosition);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int position = mediaPlayer.getCurrentPosition();
                                currentDuration.setText(String.format("%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(position) -
                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
                                        TimeUnit.MILLISECONDS.toSeconds(position) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))));
                            }
                        });
                    }
                }
                // http://stackoverflow.com/questions/11140285/how-to-use-runonuithread
            }).start();

            progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(seekBar.getProgress());
                    }
                }
            });


            button_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mediaPlayer.start();
                        button_play.setVisibility(View.GONE);
                        button_pause.setVisibility(View.VISIBLE);
                    } catch (IllegalArgumentException io) {
                        System.out.println(io.getMessage());
                    }
                }
            });

            button_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mediaPlayer.pause();
                        button_pause.setVisibility(View.GONE);
                        button_play.setVisibility(View.VISIBLE);
                    } catch (IllegalArgumentException io) {
                        System.out.println(io.getMessage());
                    }
                }
            });
        }
        return rootView;
    }
}
