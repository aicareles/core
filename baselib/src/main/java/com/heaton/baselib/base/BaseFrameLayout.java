package com.heaton.baselib.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;


public abstract class BaseFrameLayout extends FrameLayout {

    protected final String TAG = this.getClass().getSimpleName();

    public BaseFrameLayout(@NonNull Context context) {
        super(context);
        inflate(context);
    }

    public BaseFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context);
    }

    public BaseFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context);
    }

    protected void inflate(Context context){
        LayoutInflater.from(context).inflate(layoutId(), this, true);
        bindData();
        bindListener();
    }

    protected abstract int layoutId();

    protected abstract void bindData();

    protected void bindListener(){}

    public void onResume(){}

    public void onPause(){}

    public void onDestroy(){}


}
