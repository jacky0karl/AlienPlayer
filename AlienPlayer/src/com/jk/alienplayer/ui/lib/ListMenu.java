package com.jk.alienplayer.ui.lib;

import com.jk.alienplayer.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListMenu extends LinearLayout {
    public static final int MEMU_ADD_TO_PLAYLIST = 0;
    public static final int MEMU_DELETE = 1;
    public static final int MEMU_REMOVE = 2;
    public static final int MEMU_VIEW = 3;
    public static final int MEMU_RETRY = 4;
    public static final int MEMU_ABORT = 5;

    public interface OnMenuItemClickListener {
        void onClick(int menuId);
    }

    private LayoutInflater mInflater;
    private LayoutParams mChildLp;
    private OnMenuItemClickListener mMenuItemClickListener = null;

    public ListMenu(Context context) {
        super(context);
        init(context);
    }

    public ListMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ListMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mInflater = LayoutInflater.from(context);
        setOrientation(VERTICAL);
        //setBackgroundColor(getResources().getColor(R.color.grey));

        int w = getResources().getDimensionPixelOffset(R.dimen.menu_width);
        int h = getResources().getDimensionPixelOffset(R.dimen.menu_height);
        mChildLp = new LayoutParams(w, h);
        mChildLp.bottomMargin = 1;
    }

    public void setMenuItemClickListener(OnMenuItemClickListener listener) {
        mMenuItemClickListener = listener;
    }

    public void addMenu(final int id, int title) {
        TextView item = (TextView) mInflater.inflate(R.layout.menu_item, null);
        item.setId(id);
        item.setText(title);
        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMenuItemClickListener != null) {
                    mMenuItemClickListener.onClick(id);
                }
            }
        });
        addView(item, mChildLp);
    }

    public void clearMenu() {
        removeAllViews();
    }
}
