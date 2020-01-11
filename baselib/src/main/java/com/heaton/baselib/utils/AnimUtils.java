/*
package com.heaton.baselib.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

public class AnimUtils {

    private static ObjectAnimator getAnimator(View view, String propertyName, long duration, int repeat_count, int repeat_mode, float... values){
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, propertyName, values);
        translateAnimator.setDuration(duration);
        translateAnimator.setRepeatCount(repeat_count);
        translateAnimator.setRepeatMode(repeat_mode);
        return translateAnimator;
    }

    public static void start(View view, TYPE type, long duration, float... values){
        start(view, type, duration, 0, ValueAnimator.RESTART, values);
    }

    public static void start(View view, TYPE type, long duration, int repeat_count, int repeat_mode, float... values){
        switch (type){
            case TRANSLATE:
                getAnimator(view, "", duration,);
                break;
            case ROTATE:

                break;
            case SCALE:

                break;
            case ALPHA:

                break;
        }
    }


    public enum TYPE {
        TRANSLATE_X, TRANSLATE_Y, ROTATE, SCALE_X, SCALE_Y, ALPHA
    }
}
*/
