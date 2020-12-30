package com.heaton.baselib.utils;

import android.content.Context;
import android.widget.Toast;

import com.heaton.baselib.CoreBase;


/**
 * Created by LiuLei on 2017/11/27.
 */
public class ToastUtil {

    private static Toast mToast;

    public static void show(String msg) {
        show(CoreBase.getContext(), msg);
    }

    public static void show(int msg) {
        show(CoreBase.getContext(), msg);
    }

    public static void show(Context context, int msg) {
        show(context, context.getString(msg));
    }

    public static void show(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }
}
