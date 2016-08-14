package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumInfo> mAlbums;
    private OnItemClickListener mItemClickListener = null;

    public AlbumsAdapter(Context context, OnItemClickListener l) {
        mContext = context;
        mItemClickListener = l;
        mInflater = LayoutInflater.from(context);
        mAlbums = new ArrayList<>();
    }

    public void setAlbums(List<AlbumInfo> albums) {
        if (albums != null) {
            mAlbums = albums;
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_item_album, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlbumInfo info = mAlbums.get(position);
        holder.name.setText(info.name);
        holder.artist.setText(info.artist);
        String trackCount = holder.tracks.getResources().getString(R.string.track_count);
        holder.tracks.setText(String.valueOf(info.tracks) + trackCount);
        Picasso.with(mContext).load(info.artwork).config(Bitmap.Config.RGB_565)
                .placeholder(R.drawable.disk).into(holder.artwork);

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, position, info);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View root;
        ImageView artwork;
        TextView name;
        TextView artist;
        TextView tracks;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            artwork = (ImageView) itemView.findViewById(R.id.artwork);
            name = (TextView) itemView.findViewById(R.id.content);
            artist = (TextView) itemView.findViewById(R.id.artist);
            tracks = (TextView) itemView.findViewById(R.id.tracks);
        }
    }
}
