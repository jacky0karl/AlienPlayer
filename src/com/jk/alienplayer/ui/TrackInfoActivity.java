package com.jk.alienplayer.ui;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.Mp3TagsHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.TrackTagInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class TrackInfoActivity extends Activity {
    private TrackTagInfo mTrackTagInfo;
    private String mTrackPath;
    private EditText mTitle;
    private EditText mArtist;
    private EditText mAlbum;
    private EditText mAlbumArtist;
    private EditText mTrack;
    private EditText mYear;
    private Button mSaveBtn;
    private Button mCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        init();
    }

    private void init() {
        SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
        mTrackPath = song.path;
        mTrackTagInfo = Mp3TagsHelper.readMp3Tags(mTrackPath);

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
        mYear = (EditText) findViewById(R.id.year);
        mYear.setText(mTrackTagInfo.getYear());

        mSaveBtn = (Button) findViewById(R.id.saveBtn);
        mSaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTrackTags();
                MediaScanService.startScan(TrackInfoActivity.this, mTrackPath);
                TrackInfoActivity.this.finish();
            }
        });
        mCancelBtn = (Button) findViewById(R.id.cancelBtn);
        mCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackInfoActivity.this.finish();
            }
        });
    }

    private void writeTrackTags() {
        String title = mTitle.getText().toString().trim();
        String artists = mArtist.getText().toString().trim();
        String album = mAlbum.getText().toString().trim();
        String artistAlbum = mAlbumArtist.getText().toString().trim();
        String track = mTrack.getText().toString().trim();
        String year = mYear.getText().toString().trim();
        Mp3TagsHelper.writeMp3Tags(title, artists, album, artistAlbum, track, year, mTrackPath);
    }
}
