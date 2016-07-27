package com.jk.alienplayer.ui.artistdetail;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.utils.UiUtils;
import com.jk.alienplayer.widget.Playbar;
import com.squareup.picasso.Picasso;

public class SongsActivity extends BaseActivity {
    public static final String KEY_TYPE = "key_type";
    public static final String KEY = "key";
    public static final String LABEL = "label";

    private int mKeyType;
    private long mKey;
    private String mLabel;
    private Playbar mPlaybar;

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

        SongsFragment f = new SongsFragment();
        Bundle arg = new Bundle();
        arg.putInt(SongsFragment.KEY_TYPE, mKeyType);
        arg.putLong(SongsFragment.KEY, mKey);
        arg.putString(SongsFragment.LABEL, mLabel);
        f.setArguments(arg);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, f).commit();
    }

    private void setupAppbar() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(android.R.color.transparent));
                findViewById(android.R.id.content).setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }

            int height = UiUtils.getScreenWidth(this);
            ImageView cover = (ImageView) findViewById(R.id.cover);
            cover.setMinimumHeight(height);
            cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String file = DatabaseHelper.getAlbumArtwork(this, mKey);
            Picasso.with(this).load(file).into(cover);

            AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appBarLayout);
            UiUtils.setAppBarLayoutOffset(appbar, (int) (height * 0.45));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
