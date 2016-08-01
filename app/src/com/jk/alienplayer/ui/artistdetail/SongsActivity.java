package com.jk.alienplayer.ui.artistdetail;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jk.alienplayer.MainApplication;
import com.jk.alienplayer.R;
import com.jk.alienplayer.data.DatabaseHelper;
import com.jk.alienplayer.metadata.CurrentlistInfo;
import com.jk.alienplayer.ui.BaseActivity;
import com.jk.alienplayer.utils.UiUtils;
import com.jk.alienplayer.widget.Playbar;

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
            SimpleTarget target = new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    cover.setImageBitmap(bitmap);
                    cover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    setToolbarColor(bitmap);
                }
            };

            String file = DatabaseHelper.getAlbumArtwork(this, mKey);
            Glide.with(MainApplication.app).load(file).asBitmap().into(target);
            AppBarLayout appbar = (AppBarLayout) findViewById(R.id.appBarLayout);
            UiUtils.setAppBarLayoutOffset(appbar, (int) (height * 0.45));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
