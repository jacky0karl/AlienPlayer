package com.jk.alienplayer.ui.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.jk.alienplayer.R;
import com.jk.alienplayer.metadata.FileDownloadingInfo;
import com.jk.alienplayer.metadata.FileDownloadingInfo.Status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FileDownloadListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<FileDownloadingInfo> mInfos;

    public void setInfos(List<FileDownloadingInfo> infos) {
        if (infos != null) {
            mInfos = infos;
            notifyDataSetChanged();
        }
    }

    public FileDownloadListAdapter(Context context) {
        super();
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_file_download, null);
            viewHolder.title = (TextView) view.findViewById(R.id.title);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            viewHolder.size = (TextView) view.findViewById(R.id.size);
            viewHolder.progress = (TextView) view.findViewById(R.id.progress);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        FileDownloadingInfo info = mInfos.get(position);
        viewHolder.title.setText(info.trackInfo.name);
        viewHolder.artist.setText(info.trackInfo.artists);
        viewHolder.size.setText(calculateSize(info));
        viewHolder.progressBar.setMax(info.size);
        viewHolder.progressBar.setProgress(info.progress);
        if (info.status == Status.COMPLETED) {
            viewHolder.progress.setText(R.string.done);
        } else if (info.status == Status.FAILED) {
            viewHolder.progress.setText(R.string.failed);
        } else {
            viewHolder.progress.setText(calculateProgress(info));
        }
        return view;
    }

    static class ViewHolder {
        TextView title;
        TextView artist;
        TextView size;
        TextView progress;
        ProgressBar progressBar;
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
