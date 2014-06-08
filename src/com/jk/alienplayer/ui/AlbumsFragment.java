package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
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

public class AlbumsFragment extends Fragment {

    private ListView mListView;
    private AlbumsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        mListView = (ListView) root.findViewById(R.id.list);
        mAdapter = new AlbumsAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mAdapter.setAlbums(DatabaseHelper.getAlbums(getActivity()));

        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long key = mAdapter.getItem(position).id;
                startSongsPage(String.valueOf(key));
            }
        });
    }

    private void startSongsPage(String key) {
        Intent intent = new Intent(getActivity(), SongsActivity.class);
        intent.putExtra(SongsActivity.KEY_TYPE, DatabaseHelper.TYPE_ALBUM);
        intent.putExtra(SongsActivity.KEY, key);
        startActivity(intent);
    }

}
