package com.jk.alienplayer.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.metadata.FileDownloadingInfo.Status;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileDownloadListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<FileDownloadingInfo> mInfos;
    private OnItemClickListener mItemClickListener = null;

    public void setInfos(List<FileDownloadingInfo> infos) {
        if (infos != null) {
            mInfos = infos;
            notifyDataSetChanged();
        }
    }

    public FileDownloadListAdapter(Context context, OnItemClickListener listener) {
        mItemClickListener = listener;
        mInflater = LayoutInflater.from(context);
        mInfos = new ArrayList<FileDownloadingInfo>();
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public FileDownloadingInfo getItem(int position) {
        return mInfos.get(position);
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
            view = mInflater.inflate(R.layout.item_file_download, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            viewHolder.size = (TextView) view.findViewById(R.id.size);
            viewHolder.progress = (TextView) view.findViewById(R.id.progress);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            viewHolder.action = (ImageView) view.findViewById(R.id.action);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        FileDownloadingInfo info = mInfos.get(position);
        viewHolder.title.setText(info.trackInfo.getName());
        viewHolder.artist.setText(info.trackInfo.getShowingArtists());
        viewHolder.size.setText(calculateSize(info));
        viewHolder.progressBar.setMax(info.size);
        viewHolder.progressBar.setProgress(info.progress);
        if (info.status == Status.COMPLETED) {
            viewHolder.progress.setText(R.string.done);
        } else if (info.status == Status.FAILED) {
            viewHolder.progress.setText(R.string.failed);
        } else if (info.status == Status.CANCELED) {
            viewHolder.progress.setText(R.string.aborted);
        } else {
            viewHolder.progress.setText(calculateProgress(info));
        }

        viewHolder.action.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(null, v, position, getItemId(position));
            }
        });
        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView artist;
        TextView size;
        TextView progress;
        ProgressBar progressBar;
        ImageView action;
    }

    private String calculateSize(FileDownloadingInfo info) {
        float size = (float) info.size / 1024 / 1024;
        DecimalFormat df = new DecimalFormat("##0.00");
        return df.format(size) + "MB";
    }

    private String calculateProgress(FileDownloadingInfo info) {
        if (info.size == 0) {
            return "0%";
        }

        int progress = (info.progress * 100) / info.size;
        return progress + "%";
    }
}
