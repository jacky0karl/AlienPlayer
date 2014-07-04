package com.jk.alienplayer.ui.fragment;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.ArtistInfo;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.AlbumsActivity;
import com.jk.alienplayer.ui.SongsActivity;
import com.jk.alienplayer.ui.adapter.ArtistsAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ArtistsFragment extends Fragment {

    private ListView mListView;
    private ArtistsAdapter mAdapter;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                mAdapter.setArtists(DatabaseHelper.getAlbumArtists(getActivity()));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        MediaScanService.registerScanReceiver(getActivity(), mReceiver);
        return root;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new ArtistsAdapter(getActivity(), mOnItemClickListener);
        mListView.setAdapter(mAdapter);
        mAdapter.setArtists(DatabaseHelper.getAlbumArtists(getActivity()));
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArtistInfo info = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                Log.e("#### onItemClick", "position = " + position);
            } else {
                startSongsPage(info.id, info.name);
            }
        }
    };

    private void startSongsPage(long id, String label) {
        Intent intent = new Intent(getActivity(), AlbumsActivity.class);
        intent.putExtra(AlbumsActivity.ARTIST_ID, id);
        intent.putExtra(AlbumsActivity.LABEL, label);
        startActivity(intent);
    }

}
