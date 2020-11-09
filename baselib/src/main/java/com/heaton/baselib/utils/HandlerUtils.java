package com.heaton.baselib.utils;

import android.os.Handler;
import android.support.v4.os.HandlerCompat;

/**
 * author: jerry
 * date: 20-9-10
 * email: superliu0911@gmail.com
 * des:
 */
public class HandlerUtils {
    private static final Handler handler = new Handler();

    public static int setTimeout(long delay, Runnable runnable){
        int id = runnable.hashCode();
        setTimeout(id, delay, runnable);
        return id;
    }

    public static void setTimeout(int id, long delay, Runnable runnable){
        HandlerCompat.postDelayed(handler, runnable, id, delay);
        LogUtils.logi("HandlerUtils>>>[setTimeout]: "+id);
    }

    public static void removeTimeout(int id){
        handler.removeCallbacksAndMessages(id);
    }

    public static void removeTimeout(Runnable runnable){
        handler.removeCallbacks(runnable);
    }

    public static void clearTimeout(){
        handler.removeCallbacksAndMessages(null);
    }

}
