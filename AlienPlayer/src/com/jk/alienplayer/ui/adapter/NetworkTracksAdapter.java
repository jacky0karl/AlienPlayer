package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.model.TrackBean;

import java.util.ArrayList;
import java.util.List;

public class NetworkTracksAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<TrackBean> mTracks;

    public void setTracks(List<TrackBean> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            notifyDataSetChanged();
        }
    }

    public NetworkTracksAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mTracks = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mTracks.size();
    }

    @Override
    public TrackBean getItem(int position) {
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
            viewHolder.downloadBtn = (ImageView) view.findViewById(R.id.downloadBtn);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            TrackBean bean = mTracks.get(position);
            viewHolder.name.setText(bean.getName());
            viewHolder.artists.setText(bean.getShowingArtists());
            return view;
        } catch (Exception e) {
            return new View(mContext);
        }
    }

    static class ViewHolder {
        TextView name;
        TextView artists;
        ImageView downloadBtn;
    }
}
