package com.example.android.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by olivier on 10/06/15.
 */
public class ArtistAdapter extends ArrayAdapter {

    private List list = new ArrayList();

    //http://stackoverflow.com/questions/14286460/how-to-clear-arrayadapter-with-custom-listview
    public ArtistAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class DataHandler {
        ImageView img;
        TextView name;
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
            row = inflater.inflate(R.layout.list_item_artist, parent, false);
            handler = new DataHandler();
            handler.img = (ImageView)row.findViewById(R.id.list_item_artist_image);
            handler.name = (TextView)row.findViewById(R.id.list_item_artist_textview);
            row.setTag(handler);
        } else {
            handler = (DataHandler)row.getTag();
        }

        Artist artist;
        artist = (Artist)this.getItem(position);

        if (!artist.images.isEmpty()) {
            Picasso.with(this.getContext())
                    .load(artist.images.get(0).url)
                    .into(handler.img);
        }
        handler.name.setText(artist.name);

        return row;
    }

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
}
