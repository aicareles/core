package com.heaton.baselib.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.heaton.baselib.R;

/**
 * description $desc$
 * created by jerry on 2019/7/11.
 */
public class DialogUtils {

    public static AlertDialog showCommonDialog(Activity activity, String title, String message, final DialogCall callBack) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onNegative();
                    }
                })
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onPositive();
                    }
                })
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showCustomDialog(Activity activity, String title, String message, int layout, final DialogCall callBack) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setView(layout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onNegative();
                    }
                })
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onPositive();
                    }
                })
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public interface DialogCall {
        void onPositive();

        void onNegative();
    }
}
