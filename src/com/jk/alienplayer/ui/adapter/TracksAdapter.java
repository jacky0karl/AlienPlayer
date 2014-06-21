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

public class TracksAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SongInfo> mTracks;

    public void setTracks(List<SongInfo> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            notifyDataSetChanged();
        }
    }

    public TracksAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mTracks = new ArrayList<SongInfo>();
    }

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @Override
    public SongInfo getItem(int position) {
        return mTracks.get(position);
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

        SongInfo info = mTracks.get(position);
        viewHolder.name.setText(info.title);
        viewHolder.artist.setText(info.artist);
        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView artist;
    }
}
