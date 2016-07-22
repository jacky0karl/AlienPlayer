package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.utils.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends BaseAdapter
{

    private LayoutInflater mInflater;
    private List<AlbumInfo> mAlbums;

    public void setAlbums(List<AlbumInfo> albums)
    {
        if (albums != null)
        {
            mAlbums = albums;
            notifyDataSetChanged();
        }
    }

    public AlbumsAdapter(Context context)
    {
        super();
        mInflater = LayoutInflater.from(context);
        mAlbums = new ArrayList<AlbumInfo>();
    }

    @Override
    public int getCount()
    {
        return mAlbums.size();
    }

    @Override
    public AlbumInfo getItem(int position)
    {
        return mAlbums.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (view == null)
        {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_album, null);
            viewHolder.artwork = (ImageView) view.findViewById(R.id.artwork);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            viewHolder.tracks = (TextView) view.findViewById(R.id.tracks);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        AlbumInfo info = mAlbums.get(position);
        viewHolder.name.setText(info.name);
        viewHolder.artist.setText(info.artist);
        String trackCount = viewHolder.tracks.getResources().getString(R.string.track_count);
        viewHolder.tracks.setText(String.valueOf(info.tracks) + trackCount);
        ImageLoader.getInstance().displayImage(info.artwork, viewHolder.artwork,
                ImageLoaderUtils.sOptions);
        return view;
    }

    static class ViewHolder
    {
        ImageView artwork;
        TextView name;
        TextView artist;
        TextView tracks;
    }
}
