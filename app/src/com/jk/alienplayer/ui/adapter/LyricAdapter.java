package com.jk.alienplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LyricAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mLyric;
    private int mHighlightPos = 0;

    public void setLyric(List<String> lyric) {
        if (lyric != null) {
            mLyric = lyric;
            notifyDataSetChanged();
        }
    }

    public LyricAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLyric = new ArrayList<String>();
    }

    public void setHighlightPos(int highlightPos) {
        mHighlightPos = highlightPos;
        notifyDataSetChanged();
    }

    public int getHighlightPos() {
        return mHighlightPos;
    }

    @Override
    public int getCount() {
        return mLyric.size();
    }

    @Override
    public String getItem(int position) {
        return mLyric.get(position);
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
            view = mInflater.inflate(R.layout.list_item_lyric, null);
            viewHolder.sentence = (TextView) view.findViewById(R.id.sentence);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        String lyc = mLyric.get(position);
        viewHolder.sentence.setText(lyc);
        if (position == mHighlightPos) {
            viewHolder.sentence.setTextColor(mContext.getResources().getColor(R.color.lyric_hl));
        } else {
            viewHolder.sentence.setTextColor(mContext.getResources().getColor(R.color.lyric));
        }
        return view;
    }

    private static class ViewHolder {
        TextView sentence;
    }

}
