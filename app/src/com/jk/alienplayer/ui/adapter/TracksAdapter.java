package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class TracksAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<SongInfo> mTracks;
    private OnItemClickListener mItemClickListener = null;

    public void setTracks(List<SongInfo> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            notifyDataSetChanged();
        }
    }

    public TracksAdapter(Context context, OnItemClickListener listener) {
        super();
        mInflater = LayoutInflater.from(context);
        mItemClickListener = listener;
        mTracks = new ArrayList<>();
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
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_double, null);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            viewHolder.menu = (ImageView) view.findViewById(R.id.menu);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        SongInfo info = mTracks.get(position);
        viewHolder.name.setText(info.title);
        viewHolder.artist.setText(info.artist);
        viewHolder.menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(null, v, position, getItemId(position));
            }
        });
        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView artist;
        ImageView menu;
    }
}
