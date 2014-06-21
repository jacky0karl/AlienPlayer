package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.PlaylistInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PlaylistsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<PlaylistInfo> mPlaylists;

    public void setPlaylists(List<PlaylistInfo> playlist) {
        if (playlist != null) {
            mPlaylists = playlist;
            notifyDataSetChanged();
        }
    }

    public PlaylistsAdapter(Context context) {
        super();
        mInflater = LayoutInflater.from(context);
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item, null);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        PlaylistInfo info = mPlaylists.get(position);
        viewHolder.name.setText(info.name);
        return view;
    }

    static class ViewHolder {
        TextView name;
    }
}
