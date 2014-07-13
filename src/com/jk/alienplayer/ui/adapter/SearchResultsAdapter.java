package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.AlbumInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.SearchResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResultsAdapter extends BaseAdapter {
    private OnItemClickListener mItemClickListener = null;
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchResult> mResults;

    public void setResults(List<SearchResult> results) {
        if (results != null) {
            mResults = results;
            notifyDataSetChanged();
        }
    }

    public SearchResultsAdapter(Context context, OnItemClickListener listener) {
        mItemClickListener = listener;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResults = new ArrayList<SearchResult>();
    }

    @Override
    public int getCount() {
        return mResults.size();
    }

    @Override
    public SearchResult getItem(int position) {
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
        case SearchResult.TYPE_ARTISTS:
            return getArtistView(position, view);
        case SearchResult.TYPE_ALBUMS:
            return getAlbumView(position, view);
        case SearchResult.TYPE_TRACKS:
            return getTrackView(position, view);
        default:
            return view;
        }
    }

    private View getArtistView(int position, View view) {
        ArtistViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ArtistViewHolder();
            view = mInflater.inflate(R.layout.search_artist_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ArtistViewHolder) view.getTag();
        }

        SearchResult result = mResults.get(position);
        viewHolder.content.setText(result.data.getDisplayName());
        showTitle(viewHolder.title, position, result.type);
        return view;
    }

    private View getAlbumView(int position, View view) {
        AlbumViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new AlbumViewHolder();
            view = mInflater.inflate(R.layout.search_album_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            view.setTag(viewHolder);
        } else {
            viewHolder = (AlbumViewHolder) view.getTag();
        }

        SearchResult result = mResults.get(position);
        viewHolder.content.setText(result.data.getDisplayName());
        viewHolder.artist.setText(((AlbumInfo) result.data).artist);
        showTitle(viewHolder.title, position, result.type);
        return view;
    }

    private View getTrackView(final int position, View view) {
        TrackViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new TrackViewHolder();
            view = mInflater.inflate(R.layout.search_track_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            viewHolder.artists = (TextView) view.findViewById(R.id.artist);
            viewHolder.action = (ImageView) view.findViewById(R.id.action);
            view.setTag(viewHolder);
        } else {
            viewHolder = (TrackViewHolder) view.getTag();
        }

        SearchResult result = mResults.get(position);
        viewHolder.content.setText(result.data.getDisplayName());
        viewHolder.artists.setText(((SongInfo) result.data).artist);
        showTitle(viewHolder.title, position, result.type);
        viewHolder.action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(null, v, position, getItemId(position));
            }
        });
        return view;
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

    private void doShowTitle(TextView titleView, int type) {
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

        titleView.setVisibility(View.VISIBLE);
        titleView.setText(title);
    }

    static class ArtistViewHolder {
        TextView title;
        TextView content;
    }

    static class AlbumViewHolder {
        TextView title;
        TextView content;
        TextView artist;
    }

    static class TrackViewHolder {
        TextView title;
        TextView content;
        TextView artists;
        ImageView action;
    }
}
