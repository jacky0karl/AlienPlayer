package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.Mp3TagsHelper;
import com.jk.alienplayer.metadata.TrackTagInfo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class TrackInfoActivity extends Activity {
    public static final String TRACK_FILE_PATH = "track_file_path";

    private TrackTagInfo mTrackTagInfo;
    private EditText mTitle;
    private EditText mArtist;
    private EditText mAlbum;
    private EditText mAlbumArtist;
    private EditText mTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        init();
    }

    private void init() {
        String trackPath = getIntent().getStringExtra(TRACK_FILE_PATH);
        mTrackTagInfo = Mp3TagsHelper.readMp3Tags(trackPath);

        mTitle = (EditText) findViewById(R.id.title);
        mTitle.setText(mTrackTagInfo.getTitle());
        mArtist = (EditText) findViewById(R.id.artist);
        mArtist.setText(mTrackTagInfo.getArtists());
        mAlbum = (EditText) findViewById(R.id.album);
        mAlbum.setText(mTrackTagInfo.getAlbum());
        mAlbumArtist = (EditText) findViewById(R.id.album_artist);
        mAlbumArtist.setText(mTrackTagInfo.getArtistAlbum());
        mTrack = (EditText) findViewById(R.id.track);
        mTrack.setText(mTrackTagInfo.getTrack());
    }
}
