package com.heaton.baselib.utils;

import android.os.Handler;

import com.heaton.baselib.callback.CallBack;

import java.util.TreeMap;

/**
 * author: jerry
 * date: 20-9-10
 * email: superliu0911@gmail.com
 * des:
 */
public class HandlerUtils {

    private static final TreeMap<Integer, Runnable> LISTENER_MAP = new TreeMap<>();
    private static final Handler handler = new Handler();

    public static int setTimeout(long delay, Runnable runnable){
        int id = runnable.hashCode();
        setTimeout(id, delay, runnable);
        return id;
    }

    public static void setTimeout(int id, long delay, Runnable runnable){
        handler.postDelayed(runnable, delay);
        LISTENER_MAP.put(id, runnable);
        expiredAutoRemove(id, delay);
        LogUtils.logi("HandlerUtils>>>[setTimeout]: "+id);
    }

    private static void expiredAutoRemove(final int id, long delay){
        ThreadUtils.asyncDelay(delay, new CallBack() {
            @Override
            public void execute() {
                LISTENER_MAP.remove(id);
                LogUtils.logi("HandlerUtils>>>[execute]: 自动移除id");
            }
        });
    }

    public static void removeTimeout(int id){
        if (LISTENER_MAP.containsKey(id)){
            Runnable runnable = LISTENER_MAP.remove(id);
            handler.removeCallbacks(runnable);
            LogUtils.logi("HandlerUtils>>>[removeTimeout]: "+id);
        }
    }

    public static void removeTimeout(Runnable runnable){
        LISTENER_MAP.remove(runnable.hashCode());
        handler.removeCallbacks(runnable);
    }

    public static void clearTimeout(){
        handler.removeCallbacksAndMessages(null);
        LISTENER_MAP.clear();
    }

}
