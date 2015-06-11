package com.example.android.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by olivier on 09/06/15.
 * Got help from https://www.youtube.com/watch?v=DzpwvZ4S27g
 */
public class TrackAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public TrackAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class DataHandler {
        ImageView img;
        TextView name;
        TextView album;
    }

    @Override
    public void add(Object object) {
        super.add(object);
        this.list.add(object);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        DataHandler handler;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item_track, parent, false);
            handler = new DataHandler();
            handler.img = (ImageView)row.findViewById(R.id.list_item_track_image);
            handler.name = (TextView)row.findViewById(R.id.list_item_track_textview);
            handler.album = (TextView)row.findViewById(R.id.list_item_album_textview);
            row.setTag(handler);
        } else {
            handler = (DataHandler)row.getTag();
        }

        Track track;
        track = (Track)this.getItem(position);

        if (!track.album.images.isEmpty()) {
            Picasso.with(this.getContext())
                    .load(track.album.images.get(0).url)
                    .into(handler.img);
        }

        handler.album.setText(track.album.name);
        handler.name.setText(track.name);

        return row;

    }
}
