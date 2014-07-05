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
    public static final String TYPE = "type";
    public static final int TYPE_ARTISTS = 0;
    public static final int TYPE_ALBUM_ARTISTS = 1;

    private int mType;
    private ListView mListView;
    private ArtistsAdapter mAdapter;

    public static ArtistsFragment newInstance(int type) {
        ArtistsFragment fragment = new ArtistsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaScanService.ACTION_MEDIA_SCAN_COMPLETED)) {
                setArtists();
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mType = getArguments().getInt(TYPE, TYPE_ARTISTS);
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
        setArtists();
        mListView.setOnItemClickListener(mOnItemClickListener);
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArtistInfo info = mAdapter.getItem(position);
            if (view.getId() == R.id.action) {
                Log.e("#### onItemClick", "position = " + position);
            } else {
                startSubPage(info.id, info.name);
            }
        }
    };

    private void startSubPage(long key, String label) {
        Intent intent = null;
        if (mType == TYPE_ARTISTS) {
            intent = new Intent(getActivity(), SongsActivity.class);
            intent.putExtra(SongsActivity.KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
            intent.putExtra(SongsActivity.KEY, key);
            intent.putExtra(SongsActivity.LABEL, label);
        } else {
            intent = new Intent(getActivity(), AlbumsActivity.class);
            intent.putExtra(AlbumsActivity.ALBUM_ARTIST, label);
        }
        startActivity(intent);
    }

    private void setArtists() {
        if (mType == TYPE_ARTISTS) {
            mAdapter.setArtists(DatabaseHelper.getArtists(getActivity()));
        } else {
            mAdapter.setArtists(DatabaseHelper.getAlbumArtists(getActivity()));
        }
    }
}
