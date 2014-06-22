package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.PlaylistInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaylistsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<PlaylistInfo> mPlaylists;
    private OnItemClickListener mItemClickListener = null;

    public void setPlaylists(List<PlaylistInfo> playlist) {
        if (playlist != null) {
            mPlaylists = playlist;
            notifyDataSetChanged();
        }
    }

    public PlaylistsAdapter(Context context, OnItemClickListener listener) {
        super();
        mInflater = LayoutInflater.from(context);
        mItemClickListener = listener;
        mPlaylists = new ArrayList<PlaylistInfo>();
    }

    @Override
    public int getCount() {
        return mPlaylists.size();
    }

    @Override
    public PlaylistInfo getItem(int position) {
        return mPlaylists.get(position);
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
            view = mInflater.inflate(R.layout.list_item, null);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            viewHolder.action = (ImageView) view.findViewById(R.id.action);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        PlaylistInfo info = mPlaylists.get(position);
        viewHolder.name.setText(info.name);
        viewHolder.action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(null, v, position, getItemId(position));
            }
        });
        return view;
    }

    private static class ViewHolder {
        TextView name;
        ImageView action;
    }

}
