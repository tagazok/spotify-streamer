package com.example.android.spotifystreamer;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import kaaes.spotify.webapi.android.models.Track;


/**
 * A placeholder fragment containing a simple view.
 */
public class PlayerActivityFragment extends DialogFragment {

    private HashMap<String, String> params = null;
    private View rootView = null;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler mHandler = new Handler();
    private TextView currentDuration = null;
    private int track_position = 0;

    private ImageButton button_previous;
    private ImageButton button_next;

    private MusicService myService;
    private boolean isBind = false;

    public PlayerActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);

        Bundle arguments = getArguments();

        Intent intent = new Intent(getActivity(), MusicService.class);
        getActivity().bindService(intent, Mconnection, Context.BIND_AUTO_CREATE);
        //final Intent intent = getActivity().getIntent();

        //params = (HashMap<String, String>) intent.getSerializableExtra("infos");

        final ImageButton button_play = (ImageButton) rootView.findViewById(R.id.button_play);
        final ImageButton button_pause = (ImageButton) rootView.findViewById(R.id.button_pause);

        button_previous = (ImageButton) rootView.findViewById(R.id.button_previous);
        button_next = (ImageButton) rootView.findViewById(R.id.button_next);

        ImageButton button_share = (ImageButton) rootView.findViewById(R.id.button_share);

        track_position = arguments.getInt("track_position");

        button_play.setVisibility(View.GONE);
        button_pause.setVisibility(View.VISIBLE);

        final SeekBar progress = ((SeekBar) rootView.findViewById(R.id.duration_seekbar));

        progress.setMax(mediaPlayer.getDuration());
        final int total = mediaPlayer.getDuration();
        progress.setMax(total);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    int currentPosition = 0;
//                    while (mediaPlayer != null && currentPosition < total) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            return;
//                        } catch (Exception e) {
//                            return;
//                        }
//                        currentPosition = mediaPlayer.getCurrentPosition();
//                        progress.setProgress(currentPosition);
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                int position = mediaPlayer.getCurrentPosition();
//                                currentDuration.setText(String.format("%02d:%02d",
//                                        TimeUnit.MILLISECONDS.toMinutes(position) -
//                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(position)),
//                                        TimeUnit.MILLISECONDS.toSeconds(position) -
//                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position))));
//                            }
//                        });
//                    }
//                }
//                // http://stackoverflow.com/questions/11140285/how-to-use-runonuithread
//            }).start();

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

        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = myService.getList().get(track_position).artists.get(0).name + " - " + myService.getList().get(track_position).name + " - " + myService.getList().get(track_position).preview_url;
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    myService.resumeSong();
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
                    myService.pauseSong();
                    button_pause.setVisibility(View.GONE);
                    button_play.setVisibility(View.VISIBLE);
                } catch (IllegalArgumentException io) {
                    System.out.println(io.getMessage());
                }
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    track_position++;
                    updateUI(myService.getList().get(track_position));
                    myService.setSong(track_position);
                    myService.playSong();
                } catch (IllegalArgumentException io) {
                    System.out.println(io.getMessage());
                }
            }
        });

        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    track_position--;
                    updateUI(myService.getList().get(track_position));
                    myService.setSong(track_position);
                    myService.playSong();
                } catch (IllegalArgumentException io) {
                    System.out.println(io.getMessage());
                }
            }
        });

        return rootView;
    }

    private void updateUI(Track track) {
        ((TextView) rootView.findViewById(R.id.artist_name)).setText(track.artists.get(0).name);
        ((TextView) rootView.findViewById(R.id.album_name)).setText(track.album.name);
        ((TextView) rootView.findViewById(R.id.track_name)).setText(track.name);
        currentDuration = ((TextView) rootView.findViewById((R.id.current_duration)));

        String d = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(track.duration_ms) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(track.duration_ms)),
                TimeUnit.MILLISECONDS.toSeconds(track.duration_ms) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.duration_ms)));

        ((TextView) rootView.findViewById(R.id.track_duration)).setText(d);
        Picasso.with(getActivity().getApplicationContext())
                .load(track.album.images.get(0).url)
                .into((ImageView) rootView.findViewById(R.id.album_image));

        if (track_position == 0) {
            button_previous.setEnabled(false);
        } else {
            button_previous.setEnabled(true);
        }

        if (track_position == myService.getList().size() - 1) {
            button_next.setEnabled(false);
        } else {
            button_next.setEnabled(true);
        }
    }

    private ServiceConnection Mconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder localService = (MusicService.MusicBinder) service;
            myService = localService.getService();
            isBind = true;
            updateUI(myService.getList().get(track_position));
            myService.setSong(track_position);
            myService.playSong();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBind = false;
        }
    };
}
