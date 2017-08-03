package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.NetworkAlbumInfo;
import com.jk.alienplayer.metadata.NetworkSearchResult;
import com.jk.alienplayer.metadata.NetworkTrackInfo;
import com.jk.alienplayer.metadata.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NetworkSearchResultsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<NetworkSearchResult> mResults;

    public void setResults(List<NetworkSearchResult> results) {
        if (results != null) {
            mResults = results;
            notifyDataSetChanged();
        }
    }

    public void addResults(List<NetworkSearchResult> results) {
        if (results != null) {
            mResults.addAll(results);
            notifyDataSetChanged();
        }
    }

    public NetworkSearchResultsAdapter(Context context) {
        super();
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResults = new ArrayList<NetworkSearchResult>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public NetworkSearchResult getItem(int position) {
        return mResults.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return mResults.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        int type = getItemViewType(position);
        switch (type) {
            case NetworkSearchResult.TYPE_ARTISTS:
                return getArtistView(position, view);
            case NetworkSearchResult.TYPE_ALBUMS:
                return getAlbumView(position, view);
            case NetworkSearchResult.TYPE_TRACKS:
                return getTrackView(position, view);
            default:
                return view;
        }
    }

    private View getArtistView(int position, View view) {
        ArtistViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ArtistViewHolder();
            view = mInflater.inflate(R.layout.search_network_artist_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.avatar = (ImageView) view.findViewById(R.id.avatar);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ArtistViewHolder) view.getTag();
        }

        NetworkSearchResult result = mResults.get(position);
        viewHolder.content.setText(result.name);
        showTitle(viewHolder.title, position, result.type);
        return view;
    }

    private View getAlbumView(int position, View view) {
        AlbumViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new AlbumViewHolder();
            view = mInflater.inflate(R.layout.search_network_album_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.artwork = (ImageView) view.findViewById(R.id.artwork);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            view.setTag(viewHolder);
        } else {
            viewHolder = (AlbumViewHolder) view.getTag();
        }

        NetworkAlbumInfo result = (NetworkAlbumInfo) mResults.get(position);
        Picasso.with(mContext).load(result.avatar).placeholder(R.drawable.ic_disc)
                .config(Bitmap.Config.RGB_565).into(viewHolder.artwork);
        viewHolder.content.setText(result.name);
        viewHolder.artist.setText(result.artist);
        showTitle(viewHolder.title, position, result.type);
        return view;
    }

    private View getTrackView(int position, View view) {
        TrackViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new TrackViewHolder();
            view = mInflater.inflate(R.layout.search_network_track_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.artists = (TextView) view.findViewById(R.id.artists);
            view.setTag(viewHolder);
        } else {
            viewHolder = (TrackViewHolder) view.getTag();
        }

        NetworkTrackInfo result = (NetworkTrackInfo) mResults.get(position);
        viewHolder.content.setText(result.name);
        viewHolder.artists.setText(result.artists);
        showTitle(viewHolder.title, position, result.type);
        return view;
    }

    static class ArtistViewHolder {
        TextView title;
        ImageView avatar;
        TextView content;
    }

    static class AlbumViewHolder {
        TextView title;
        ImageView artwork;
        TextView content;
        TextView artist;
    }

    static class TrackViewHolder {
        TextView title;
        ImageView avatar;
        TextView content;
        TextView artists;
    }

    private void showTitle(TextView titleView, int position, int type) {
        if (position == 0) {
            doShowTitle(titleView, type);
        } else if (mResults.get(position - 1).type != type) {
            doShowTitle(titleView, type);
        } else {
            titleView.setVisibility(View.GONE);
        }
    }

    private void doShowTitle(TextView view, int type) {
        String title = null;
        switch (type) {
            case SearchResult.TYPE_ARTISTS:
                title = mContext.getString(R.string.artist);
                break;
            case SearchResult.TYPE_ALBUMS:
                title = mContext.getString(R.string.album);
                break;
            case SearchResult.TYPE_TRACKS:
                title = mContext.getString(R.string.track);
                break;
            default:
                break;
        }

        view.setVisibility(View.VISIBLE);
        view.setText(title);
    }
}
