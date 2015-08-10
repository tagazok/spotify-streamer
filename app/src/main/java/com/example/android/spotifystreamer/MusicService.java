package com.example.android.spotifystreamer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.R;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by olivier on 04/08/15.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {


    private MediaPlayer player;
    private List<Track> tracks;
    private int songPosn;

    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;

    private final IBinder mBinder = new MusicBinder();
    private final int PLAYER_NOTIFICATION = 1;
    @Override
    public void onCreate() {
        super.onCreate();
        songPosn=0;
        player = new MediaPlayer();
        mManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);

        mSession = new MediaSession(getApplicationContext(), "simple player session");
        mController =new MediaController(getApplicationContext(), mSession.getSessionToken());

        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                buildNotification(generateAction(android.R.drawable.ic_media_pause, "Pause", "action_pause"));
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.e("MediaPlayerService", "onPause");
                buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", "action_play"));
            }
        });


        initMusicPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void initMusicPlayer(){
//        player.setWakeMode(getApplicationContext(),
//                PowerManager.PARTIAL_WAKE_LOCK);
//        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(List<Track> tracks){
        this.tracks = tracks;
    }

    public List<Track> getList() {
        return this.tracks;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        handleIntent( intent );
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        player.start();
        buildNotification(null);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(PLAYER_NOTIFICATION);
    }

    public void playSong() {
        player.reset();

        Track track = this.tracks.get(this.songPosn);

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(track.preview_url);
            player.prepare();
        } catch (IOException io) {

        }
    }

    public void pauseSong() {
        player.pause();
    }

    public void resumeSong() {
        player.start();
    }

    public void setSong(int index) {
        this.songPosn = index;
    }

    private void buildNotification( Notification.Action action ) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent( getApplicationContext(), MusicService.class );
        intent.setAction("action_stop");
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        final Track track = this.tracks.get(songPosn);
        Notification.Builder builder = new Notification.Builder( this )
                .setSmallIcon(R.drawable.ic_media_play)
                .setContentTitle(track.name)
                .setContentText(track.artists.get(0).name)
                .setDeleteIntent( pendingIntent )
                .setStyle(style);

        Bitmap contactPic = null;
        try {
            contactPic = new AsyncTask<Void, Void, Bitmap>() {
                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return Picasso.with(getApplicationContext()).load(track.album.images.get(0).url)
                                .resize(200, 200)
                                .error(R.drawable.ic_media_play)
                                .get();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (contactPic != null) {
            builder.setLargeIcon(contactPic);
        } else {
            builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_media_play));
        }

        //builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Previous", "action_previous"));
        //builder.addAction( generateAction( android.R.drawable.ic_media_pause, "Play", "action_play" ) );
        //builder.addAction( generateAction( android.R.drawable.ic_media_next, "Next", "action_next" ) );
        //style.setShowActionsInCompactView(0,1,2);

        NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        notificationManager.notify( PLAYER_NOTIFICATION, builder.build() );
    }

    // http://www.binpress.com/tutorial/using-android-media-style-notifications-with-media-session-controls/165
    private Notification.Action generateAction( int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), MusicService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( "action_play" ) ) {
            mController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( "action_pause" ) ) {
            mController.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( "action_previous" ) ) {
            mController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase( "action_next" ) ) {
            mController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( "action_stop" ) ) {
            mController.getTransportControls().stop();
        }
    }

    public MediaPlayer getPlayer() {
        return player;
    }
}
