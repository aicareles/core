package com.heaton.baselib.utils;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heaton.baselib.CoreBase;

/**
 * author: jerry
 * date: 20-8-7
 * email: superliu0911@gmail.com
 * des:
 */
public class ContextUtils {
    public static int getColor(@ColorRes int resId){
        return ContextCompat.getColor(CoreBase.getContext(), resId);
    }

    public static void setColor(@NonNull TextView textView, @ColorRes int resId){
        textView.setTextColor(getColor(resId));
    }

    public static Drawable getDrawable(@DrawableRes int id) {
        return ContextCompat.getDrawable(CoreBase.getContext(), id);
    }

    public static void setDrawable(@NonNull ImageView imageView,  @ColorRes int resId){
        imageView.setImageDrawable(getDrawable(resId));
    }

    public static void setVisibility(@NonNull View view, boolean visibility){
        view.setVisibility(visibility?View.VISIBLE:View.GONE);
    }
}
