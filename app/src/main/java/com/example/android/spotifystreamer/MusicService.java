package com.example.android.spotifystreamer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by olivier on 04/08/15.
 */
public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private List<Track> tracks;
    //current position
    private int songPosn;

    private final IBinder mBinder = new MusicBinder();

    @Override
    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();

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
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

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
}
