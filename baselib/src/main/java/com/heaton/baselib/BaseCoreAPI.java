package com.heaton.baselib;

import android.annotation.SuppressLint;
import android.content.Context;

import com.heaton.baselib.api.Api;
import com.heaton.baselib.app.language.LanguageManager;
import com.heaton.baselib.utils.LogUtils;

/**
 * description 核心基础类入口
 * created by jerry on 2019/5/28.
 */
public class BaseCoreAPI {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void init(Context context){
        init(context, null);
    }

    public static void init(Context context, Configuration configuration){
        mContext = context;
        if (configuration == null){
            configuration = Configuration.defalut();
        }
        LogUtils.logInit(configuration.loggable, configuration.logTag);
        LanguageManager.init(context, configuration.language);
        if (configuration.apiConfig != null){
            Api.init(configuration.apiConfig);
        }
    }

    public static Context getContext(){
        if (mContext == null){
            throw new IllegalStateException("please init BaseCoreAPI");
        }
        return mContext;
    }

}
