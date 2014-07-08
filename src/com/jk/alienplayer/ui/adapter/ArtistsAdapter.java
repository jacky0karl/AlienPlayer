package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.ArtistInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ArtistsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ArtistInfo> mArtists;

    public void setArtists(List<ArtistInfo> artists) {
        if (artists != null) {
            mArtists = artists;
            notifyDataSetChanged();
        }
    }

    public ArtistsAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mArtists = new ArrayList<ArtistInfo>();
    }

    @Override
    public int getCount() {
        return mArtists.size();
    }

    @Override
    public ArtistInfo getItem(int position) {
        return mArtists.get(position);
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
            view = mInflater.inflate(R.layout.list_item_no_menu, null);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ArtistInfo info = mArtists.get(position);
        viewHolder.name.setText(info.name);
        return view;
    }

    static class ViewHolder {
        TextView name;
    }
}
