package com.heaton.baselib;

import android.annotation.SuppressLint;
import android.content.Context;

import com.heaton.baselib.utils.LogUtils;

/**
 * description $desc$
 * created by jerry on 2019/5/28.
 */
public class BaseLibApi {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(Context context, Options options){
        mContext = context;
        setLoggable(options.isLoggable());
    }

    private static void setLoggable(boolean isLoggable){
        LogUtils.logInit(isLoggable);
    }

    public static Context getContext(){
        if (mContext == null){
            throw new IllegalStateException("please init BaseLibApi");
        }
        return mContext;
    }

}
