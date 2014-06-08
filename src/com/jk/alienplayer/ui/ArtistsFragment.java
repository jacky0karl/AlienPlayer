package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.ArtistInfo;
import com.jk.alienplayer.data.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ArtistsFragment extends Fragment {

    private ListView mListView;
    private ArtistsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_artists, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new ArtistsAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdapter.setArtists(DatabaseHelper.getArtists(getActivity()));

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = ((ArtistInfo) mAdapter.getItem(position)).name;
                startSongsPage(key);
            }
        });
    }

    private void startSongsPage(String key) {
        Intent intent = new Intent(getActivity(), SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, DatabaseHelper.TYPE_ARTIST);
        intent.putExtra(SongsActivity.KEY, key);
        startActivity(intent);
    }

}
