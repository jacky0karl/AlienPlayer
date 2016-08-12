package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> {
    private LayoutInflater mInflater;
    private List<SongInfo> mTracks;
    private OnItemClickListener mItemClickListener = null;

    public SongsAdapter(Context context, OnItemClickListener l) {
        super();
        mInflater = LayoutInflater.from(context);
        mItemClickListener = l;
        mTracks = new ArrayList<>();
    }

    public void setTracks(List<SongInfo> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_double, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SongInfo info = mTracks.get(position);
        holder.name.setText(info.title);
        holder.artist.setText(info.artist);

        holder.menu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, position, info);
            }
        });

        holder.root.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, position, info);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTracks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        TextView name;
        TextView artist;
        ImageView menu;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            name = (TextView) itemView.findViewById(R.id.content);
            artist = (TextView) itemView.findViewById(R.id.artist);
            menu = (ImageView) itemView.findViewById(R.id.menu);
        }
    }
}
