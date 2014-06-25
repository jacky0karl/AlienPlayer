package com.jk.alienplayer.ui.fragment;

import com.jk.alienplayer.R;

import com.jk.alienplayer.impl.PlayService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LyricFragment extends Fragment {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PlayService.ACTION_TRACK_CHANGE)) {

            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lyric, container, false);
        init(root);
        IntentFilter intentFilter = new IntentFilter(PlayService.ACTION_TRACK_CHANGE);
        getActivity().registerReceiver(mReceiver, intentFilter);
        return root;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void init(View root) {

    }

}
