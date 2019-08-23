package com.heaton.baselib.app.language;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.heaton.baselib.app.Constance;
import com.heaton.baselib.utils.SPUtils;

import java.util.Locale;

/**
 * Created by jerry on 2018/8/23.
 */

public class LanguageUtil {
    public static void applyLanguage(Context context, String newLanguage) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = SupportLanguageUtil.getSupportLanguage(newLanguage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // apply locale
            configuration.setLocale(locale);

        } else {
            // updateConfiguration
            configuration.locale = locale;
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
    }

    public static Context attachBaseContext(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context, language);
        } else {
            applyLanguage(context, language);
            return context;
        }
    }

    //得到保存语言
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getSaveLanguage(Context context) {
        String selectedLanguage = SPUtils.get(context, Constance.SP.LANGUAGE, SupportLanguageUtil.getSystemPreferredLanguage().getLanguage());
        selectedLanguage = SupportLanguageUtil.isSupportLanguage(selectedLanguage) ? selectedLanguage : Constance.SP.CHINESE;
        return selectedLanguage;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context, String language) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale;
        if (TextUtils.isEmpty(language)) {//如果没有指定语言使用系统首选语言
            locale = SupportLanguageUtil.getSystemPreferredLanguage();
        } else {//指定了语言使用指定语言，没有则使用首选语言
            locale = SupportLanguageUtil.getSupportLanguage(language);
        }
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }
}