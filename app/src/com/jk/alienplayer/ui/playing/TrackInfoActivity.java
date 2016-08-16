package com.jk.alienplayer.ui.playing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.data.Mp3TagsHelper;
import com.jk.alienplayer.data.PlayingInfoHolder;
import com.jk.alienplayer.data.PlaylistHelper;
import com.jk.alienplayer.impl.MediaScanService;
import com.jk.alienplayer.impl.PlayService;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.metadata.SongInfo;
import com.jk.alienplayer.metadata.TrackTagInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.network.NetworkSearchActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TrackInfoActivity extends BaseActivity {
    public static final int MODE_SINGLE = 0;
    public static final int MODE_ARTIST = 1;
    public static final int MODE_ALBUM = 2;
    public static final int MODE_PLAYLIST = 3;

    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_ID = "id";
    public static final String EXTRA_ARTWORK = "artwork";
    public static final int FETCH_ARTWORK = 0;

    private int mMode = MODE_SINGLE;
    private long mId = -1;
    private SongInfo mSongInfo = null;
    private List<SongInfo> mSongList = null;

    private TrackTagInfo mTrackTagInfo;
    private String mArtworkUrl;
    private ImageView mArtwork;
    private EditText mArtist;
    private EditText mAlbum;
    private EditText mAlbumArtist;
    private EditText mYear;
    private EditText mTitle;
    private EditText mTrack;
    private TextView mTitleLab;
    private TextView mTrackLab;
    private ProgressDialog mLoaing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_save) {
            writeTrackTags();
        }
        return true;
    }

    private void init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mArtwork = (ImageView) findViewById(R.id.artwork);
        mTitle = (EditText) findViewById(R.id.title);
        mArtist = (EditText) findViewById(R.id.artist);
        mAlbum = (EditText) findViewById(R.id.album);
        mAlbumArtist = (EditText) findViewById(R.id.album_artist);
        mTrack = (EditText) findViewById(R.id.track);
        mYear = (EditText) findViewById(R.id.year);
        mTitleLab = (TextView) findViewById(R.id.titleLab);
        mTrackLab = (TextView) findViewById(R.id.trackLab);

        mMode = getIntent().getIntExtra(EXTRA_MODE, MODE_SINGLE);
        mId = getIntent().getLongExtra(EXTRA_ID, -1);
        switch (mMode) {
            case MODE_SINGLE:
                mSongInfo = DatabaseHelper.getTrack(this, mId);
                break;
            case MODE_ARTIST:
                mSongList = DatabaseHelper.getTracks(this, CurrentlistInfo.TYPE_ARTIST, mId);
                break;
            case MODE_ALBUM:
                mSongList = DatabaseHelper.getTracks(this, CurrentlistInfo.TYPE_ALBUM, mId);
                break;
            case MODE_PLAYLIST:
                mSongList = PlaylistHelper.getPlaylistMembers(this, mId);
                break;
        }

        if (mSongList != null && mSongList.size() > 0) {
            mSongInfo = mSongList.get(0);
        }
        fillData();
    }

    private void fillData() {
        if (mSongInfo == null) {
            finish();
            return;
        }

        mTrackTagInfo = Mp3TagsHelper.readMp3Tags(mSongInfo.path);
        mArtist.setText(mTrackTagInfo.getArtists());
        mAlbum.setText(mTrackTagInfo.getAlbum());
        mAlbumArtist.setText(mTrackTagInfo.getArtistAlbum());
        mYear.setText(mTrackTagInfo.getYear());

        if (mMode == MODE_SINGLE) {
            mTitleLab.setVisibility(View.VISIBLE);
            mTitle.setVisibility(View.VISIBLE);
            mTrackLab.setVisibility(View.VISIBLE);
            mTrack.setVisibility(View.VISIBLE);
            mTitle.setText(mTrackTagInfo.getTitle());
            mTrack.setText(mTrackTagInfo.getTrack());
        }

        if (mTrackTagInfo.getArtwork() != null) {
            mArtwork.setImageBitmap(mTrackTagInfo.getArtwork());
        }
        mArtwork.setOnClickListener(v -> {
            Intent intent = new Intent(TrackInfoActivity.this, NetworkSearchActivity.class);
            intent.putExtra(NetworkSearchActivity.TYPE, NetworkSearchActivity.TYPE_ARTWORK);
            intent.putExtra(NetworkSearchActivity.KEY, mTrackTagInfo.getAlbum());
            startActivityForResult(intent, FETCH_ARTWORK);
        });
    }

    private void writeTrackTags() {
        String title = mTitle.getText().toString().trim();
        String album = mAlbum.getText().toString().trim();
        String artistAlbum = mAlbumArtist.getText().toString().trim();
        String track = mTrack.getText().toString().trim();
        String year = mYear.getText().toString().trim();
        String artists = mArtist.getText().toString().trim();

        mLoaing = ProgressDialog.show(this, "", getString(R.string.saving), true);
        if (mMode == MODE_SINGLE) {
            Mp3TagsHelper.writeMp3Info(new Mp3TagsHelper.OnMP3AddListener() {
                @Override
                public void onMP3Added() {
                    mLoaing.dismiss();
                    MediaScanService.startScan(TrackInfoActivity.this, mSongInfo.path);
                    finish();
                }

                @Override
                public void onArtworkUpdated(String artworkPath) {
                    SongInfo song = PlayingInfoHolder.getInstance().getCurrentSong();
                    DatabaseHelper.deleteArtworkCache(TrackInfoActivity.this, song.albumId, artworkPath);
                    Intent intent = PlayService.getPlayingCommandIntent(TrackInfoActivity.this, PlayService.COMMAND_REFRESH);
                    startService(intent);
                }
            }, mArtworkUrl, title, artists, album, artistAlbum, track, year, mSongInfo.path);
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == FETCH_ARTWORK && data != null) {
            mArtworkUrl = data.getStringExtra(EXTRA_ARTWORK);
            Picasso.with(this).load(mArtworkUrl).config(Bitmap.Config.RGB_565).into(mArtwork);
        }
    }
}
