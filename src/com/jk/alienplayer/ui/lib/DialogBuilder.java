package com.jk.alienplayer.ui.lib;


import com.jk.alienplayer.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

public class DialogBuilder {

    public static AlertDialog buildAlertDialog(Context context, int messageId,
            OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageId);
        builder.setPositiveButton(R.string.ok, listener);
        builder.setNegativeButton(R.string.cancel, listener);
        return builder.create();
    }
}
