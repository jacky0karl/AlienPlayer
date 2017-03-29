package com.jk.alienplayer.ui.artistdetail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.jk.alienplayer.MainApplication;
import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.presenter.artistdetail.SongsActivityPresenter;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.ui.playing.TrackInfoActivity;
import com.jk.alienplayer.utils.UiUtils;
import com.jk.alienplayer.widget.Playbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SongsActivity extends BaseActivity {
    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";
    public static final String LABEL = "label";

    private static final int REQUEST_UPDATE_ARTWORK = 0;
    private static final int REQUEST_ADD_SONG = 1;

    private int mKeyType;
    private long mKey;
    private String mLabel;
    private Playbar mPlaybar;
    private SongsFragment mFragment;
    private SongsActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onDestroy() {
        mPlaybar.finish();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_songlist, menu);
        if (mKeyType != CurrentlistInfo.TYPE_PLAYLIST) {
            MenuItem item = menu.findItem(R.id.action_add);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, REQUEST_ADD_SONG);
        } else if (item.getItemId() == R.id.action_info) {
            Intent intent = new Intent(this, TrackInfoActivity.class);
            if (mKeyType == CurrentlistInfo.TYPE_ARTIST) {
                intent.putExtra(TrackInfoActivity.EXTRA_MODE, TrackInfoActivity.MODE_ARTIST);
            } else if (mKeyType == CurrentlistInfo.TYPE_ALBUM) {
                intent.putExtra(TrackInfoActivity.EXTRA_MODE, TrackInfoActivity.MODE_ALBUM);
            } else {
                intent.putExtra(TrackInfoActivity.EXTRA_MODE, TrackInfoActivity.MODE_PLAYLIST);
            }
            intent.putExtra(TrackInfoActivity.EXTRA_ID, mKey);
            startActivityForResult(intent, REQUEST_UPDATE_ARTWORK);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_UPDATE_ARTWORK) {
            if (mKeyType == CurrentlistInfo.TYPE_ALBUM) {
                setupCover();
            }
        } else if (requestCode == REQUEST_ADD_SONG) {
            mPresenter.updateSongList(mKey, data);
        }
    }

    public void updateSongListSucc() {
        mFragment.updateList();
    }

    private void init() {
        mKeyType = getIntent().getIntExtra(KEY_TYPE, CurrentlistInfo.TYPE_ARTIST);
        mKey = getIntent().getLongExtra(KEY, -1);
        mLabel = getIntent().getStringExtra(LABEL);
        if (mKeyType == CurrentlistInfo.TYPE_ALBUM) {
            setContentView(R.layout.activity_album_detail);
            setupAppbar();
        } else {
            setContentView(R.layout.activity_songs);
        }

        mPlaybar = (Playbar) findViewById(R.id.playbar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(mLabel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPresenter = new SongsActivityPresenter(this);
        mFragment = new SongsFragment();
        Bundle arg = new Bundle();
        arg.putInt(SongsFragment.KEY_TYPE, mKeyType);
        arg.putLong(SongsFragment.KEY, mKey);
        arg.putString(SongsFragment.LABEL, mLabel);
        mFragment.setArguments(arg);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, mFragment).commit();
    }

    private void setupAppbar() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
                findViewById(android.R.id.content).setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            setupCover();
            int height = UiUtils.getScreenWidth(this);
            AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appBarLayout);
            UiUtils.setAppBarLayoutOffset(appbar, (int) (height * 0.45));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCover() {
        int height = UiUtils.getScreenWidth(this);
        ImageView cover = (ImageView) findViewById(R.id.cover);
        cover.setMinimumHeight(height);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                cover.setImageBitmap(bitmap);
                cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                setToolbarColor(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        String file = DatabaseHelper.getAlbumArtwork(mKey);
        Picasso.with(MainApplication.app).load(file).config(Bitmap.Config.RGB_565).into(target);
    }

    private void setToolbarColor(Bitmap bitmap) {
        CollapsingToolbarLayout ctl = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);
        Palette.PaletteAsyncListener listener = new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch swatch = palette.getVibrantSwatch();
                if (swatch != null) {
                    int primaryColor = swatch.getRgb();
                    ctl.setContentScrimColor(primaryColor);
                    ctl.setStatusBarScrimColor(UiUtils.generateStatusBarColor(primaryColor));
                    mPlaybar.setBackgroundColor(primaryColor);
                }
            }
        };
        new Palette.Builder(bitmap).generate(listener);
    }
}
