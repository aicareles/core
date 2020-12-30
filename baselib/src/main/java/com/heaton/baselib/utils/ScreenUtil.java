package com.heaton.baselib.utils;

import android.content.Context;

import com.heaton.baselib.CoreBase;

public class ScreenUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = getScale(context);
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = getScale(context);
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = getScale(context);
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = getScale(context);
        return (int) (spValue * fontScale + 0.5f);
    }

    private static float getScale(Context context) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return findScale(fontScale);
    }
    private static float findScale(float scale){
        if(scale<=1){
            scale=1;
        }else if(scale<=1.5){
            scale=1.5f;
        }else if(scale<=2){
            scale=2f;
        }else if(scale<=3){
            scale=3f;
        }
        return scale;
    }

    public static int dip2px( float dipValue) {
        final float scale = CoreBase.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = getScale(CoreBase.getContext());
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

}
