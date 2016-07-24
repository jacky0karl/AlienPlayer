package com.jk.alienplayer.ui.adapter;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClick(View view, int position, T obj);
}