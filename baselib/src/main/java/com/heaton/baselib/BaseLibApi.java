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
    public static void init(Context context){
        mContext = context;
        LogUtils.logInit();
    }

    public static Context getContext(){
        if (mContext == null){
            throw new IllegalStateException("please init BaseLibApi");
        }
        return mContext;
    }

}
