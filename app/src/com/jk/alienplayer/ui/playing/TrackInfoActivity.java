package com.jk.alienplayer.ui.playing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.Mp3TagsHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.TrackTagInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.network.NetworkSearchActivity;
import com.squareup.picasso.Picasso;

public class TrackInfoActivity extends BaseActivity {
    public static final String EXTRA_ARTWORK = "artwork";
    public static final int FETCH_ARTWORK = 0;

    private TrackTagInfo mTrackTagInfo;
    private String mArtworkUrl;
    private String mTrackPath;
    private ImageView mArtwork;
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

        mArtwork = (ImageView) findViewById(R.id.artwork);
        if (mTrackTagInfo.getArtwork() != null) {
            mArtwork.setImageBitmap(mTrackTagInfo.getArtwork());
        }
        mArtwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrackInfoActivity.this, NetworkSearchActivity.class);
                intent.putExtra(NetworkSearchActivity.TYPE, NetworkSearchActivity.TYPE_ARTWORK);
                intent.putExtra(NetworkSearchActivity.KEY, mTrackTagInfo.getAlbum());
                startActivityForResult(intent, FETCH_ARTWORK);
            }
        });

        mSaveBtn = (Button) findViewById(R.id.saveBtn);
        mSaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                writeTrackTags();
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

        Mp3TagsHelper.writeMp3Tags(new Mp3TagsHelper.OnMP3AddListener() {
            @Override
            public void onMP3Added() {
                MediaScanService.startScan(TrackInfoActivity.this, mTrackPath);
                TrackInfoActivity.this.finish();
            }

            @Override
            public void onArtworkUpdated(String artworkPath) {
                SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
                DatabaseHelper.deleteArtworkCache(TrackInfoActivity.this, song.albumId, artworkPath);
                Intent intentNext = PlayService.getPlayingCommandIntent(TrackInfoActivity.this,
                        PlayService.COMMAND_REFRESH);
                startService(intentNext);
                setResult(Activity.RESULT_OK);
            }
        }, mArtworkUrl, title, artists, album, artistAlbum, track, year, mTrackPath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == FETCH_ARTWORK && data != null) {
            mArtworkUrl = data.getStringExtra(EXTRA_ARTWORK);
            Picasso.with(this).load(mArtworkUrl).into(mArtwork);
        }
    }
}
