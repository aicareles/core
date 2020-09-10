package com.heaton.baselib.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.List;

/**
 * author: jerry
 * date: 20-9-8
 * email: superliu0911@gmail.com
 * des: activity间传值
 */
public class IntentParams {
    private Intent intent;
    private Activity activity;

    IntentParams(Activity activity, Intent intent) {
        this.intent = intent;
        this.activity = activity;
    }

    public IntentParams put(String key, String value){
        intent.putExtra(key, value);
        return this;
    }

    public IntentParams put(String key, int value){
        intent.putExtra(key, value);
        return this;
    }

    public IntentParams put(String key, float value){
        intent.putExtra(key, value);
        return this;
    }

    public IntentParams put(String key, long value){
        intent.putExtra(key, value);
        return this;
    }

    public IntentParams put(String key, short value){
        intent.putExtra(key, value);
        return this;
    }

    public IntentParams put(String key, Parcelable value){
        intent.putExtra(key, value);
        return this;
    }

    public IntentParams put(String key, Serializable value){
        intent.putExtra(key, value);
        return this;
    }

    public void start(){
        activity.startActivity(intent);
    }

}
