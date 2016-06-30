package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.model.AlbumBean;
import com.jk.alienplayer.utils.ImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NetworkAlbumsAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AlbumBean> mAlbums;

    public void setAlbums(List<AlbumBean> albums) {
        if (albums != null) {
            mAlbums = albums;
            notifyDataSetChanged();
        }
    }

    public NetworkAlbumsAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mAlbums = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mAlbums.size();
    }

    @Override
    public AlbumBean getItem(int position) {
        return mAlbums.get(position);
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
            view = mInflater.inflate(R.layout.search_network_album_item, null);
            viewHolder.artwork = (ImageView) view.findViewById(R.id.artwork);
            viewHolder.name = (TextView) view.findViewById(R.id.content);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        try {
            AlbumBean info = mAlbums.get(position);
            viewHolder.name.setText(info.getName());
            viewHolder.artist.setText(info.getArtist().getName());
            ImageLoader.getInstance().displayImage(info.getPicUrl(), viewHolder.artwork,
                    ImageLoaderUtils.sOptions);
            return view;
        } catch (Exception e) {
            return new View(mContext);
        }
    }

    static class ViewHolder {
        ImageView artwork;
        TextView name;
        TextView artist;
    }
}
