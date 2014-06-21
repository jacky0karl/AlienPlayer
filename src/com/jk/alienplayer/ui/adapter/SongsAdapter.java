package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.SongInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SongsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SongInfo> mSongs;

    public void setSongs(List<SongInfo> songs) {
        if (songs != null) {
            mSongs = songs;
            notifyDataSetChanged();
        }
    }

    public SongsAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mSongs = new ArrayList<SongInfo>();
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public SongInfo getItem(int position) {
        return mSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_double, null);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        SongInfo info = mSongs.get(position);
        viewHolder.name.setText(info.title);
        viewHolder.artist.setText(info.artist);
        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView artist;
    }
}
