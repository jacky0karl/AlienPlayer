package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;

import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.metadata.SongInfo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ArtworkFragment extends Fragment {

    private ImageView mArtwork;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playing, container, false);
        init(root);
        return root;
    }

    private void init(View root) {
        int size = getActivity().getResources().getDimensionPixelSize(R.dimen.detail_artwork_size);
        SongInfo songInfo = PlayingInfoHolder.getInstance().getCurrentSong();
        Bitmap bmp = DatabaseHelper.getArtwork(getActivity(), songInfo.id, songInfo.albumId, size);

        mArtwork = (ImageView) root.findViewById(R.id.artwork);
        if (bmp == null) {
            mArtwork.setImageResource(R.drawable.disk);
        } else {
            mArtwork.setImageBitmap(bmp);
        }
    }
}
