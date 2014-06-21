package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.SearchResult;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchResultsAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<SearchResult> mResults;

    public void setResults(List<SearchResult> results) {
        if (results != null) {
            mResults = results;
            notifyDataSetChanged();
        }
    }

    public SearchResultsAdapter(Context context) {
        super();
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.search_item, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.content = (TextView) view.findViewById(R.id.content);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        SearchResult result = mResults.get(position);
        viewHolder.content.setText(result.data.getDisplayName());
        if (position == 0) {
            showTitle(viewHolder.title, result.type);
        } else if (mResults.get(position - 1).type != result.type) {
            showTitle(viewHolder.title, result.type);
        } else {
            viewHolder.title.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView content;
    }

    private void showTitle(TextView view, int type) {
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
