package com.heaton.baselib.utils;


import android.support.annotation.Nullable;

import com.heaton.baselib.BuildConfig;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogcatLogStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 * 如果用于android平台，将信息记录到“LogCat”。如果用于java平台，将信息记录到“Console”
 * 使用logger封装
 */
public class LogUtils {
    /**
     * 在application调用初始化
     */
    public static void logInit(final boolean isLoggable) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(3)        // (Optional) Hides internal method calls up to offset. Default 5
//                .logStrategy(new LogcatLogStrategy()) // (Optional) Changes the log strategy to print out. Default LogCat
                .tag("Heaton_LOGGER")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return isLoggable;
            }
        });
    }
    public static void logd(String message, Object... args) {
        Logger.d(message, args);
    }

    public static void loge(Throwable throwable, String message, Object... args) {
        Logger.e(throwable, message, args);
    }

    public static void loge(String message, Object... args) {
        Logger.e(message, args);
    }

    public static void logi(String message, Object... args) {
        Logger.i(message, args);
    }
    public static void logv(String message, Object... args) {
        Logger.v(message, args);
    }
    public static void logw(String message, Object... args) {
        Logger.v(message, args);
    }
    public static void logwtf(String message, Object... args) {
        Logger.wtf(message, args);
    }

    public static void logjson(String message) {
        Logger.json(message);
    }
    public static void logxml(String message) {
        Logger.xml(message);
    }
}
