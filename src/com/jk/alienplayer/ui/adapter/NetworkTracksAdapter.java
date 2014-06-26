package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.NetworkTrackInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NetworkTracksAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<NetworkTrackInfo> mTracks;

    public void setTracks(List<NetworkTrackInfo> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            notifyDataSetChanged();
        }
    }

    public NetworkTracksAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
        mTracks = new ArrayList<NetworkTrackInfo>();
    }

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @Override
    public NetworkTrackInfo getItem(int position) {
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
            view = mInflater.inflate(R.layout.search_network_track_item, null);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            viewHolder.artists = (TextView) view.findViewById(R.id.artists);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        NetworkTrackInfo info = mTracks.get(position);
        viewHolder.name.setText(info.name);
        viewHolder.artists.setText(info.artists);
        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView artists;
    }
}
