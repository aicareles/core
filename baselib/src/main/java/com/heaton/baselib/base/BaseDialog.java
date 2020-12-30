package com.heaton.baselib.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;

import com.heaton.baselib.utils.HandlerUtils;
import com.heaton.baselib.utils.ToastUtil;

/**
 * 对话框基类
 * Created by jerry on 2018/8/17.
 */

public abstract class BaseDialog extends Dialog {
    protected final String TAG = this.getClass().getSimpleName();

    public Activity mActivity;
    protected int mLayoutId;

    public BaseDialog(@NonNull Context context) {
        super(context);
        this.mActivity = (Activity) context;
    }

    public BaseDialog(@NonNull Context context, @LayoutRes int layoutId) {
        super(context);
        mLayoutId = layoutId;
        this.mActivity = (Activity) context;
    }

    public BaseDialog(@NonNull Context context, @LayoutRes int layoutId, @StyleRes int themeResId) {
        super(context, themeResId);
        this.mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayoutId = layoutId();
        View view = View.inflate(mActivity, mLayoutId, null);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            //消除弹框白色背景
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        bindData();
        bindListener();
    }

    protected int layoutId(){
        return 0;
    }

    protected abstract void bindData();

    protected void bindListener(){}

    public void toast(int resId){
        ToastUtil.show(getContext(), resId);
    }

    public void toast(String msg){
        ToastUtil.show(msg);
    }

    public void setTimeout(long delay, Runnable runnable){
        HandlerUtils.setTimeout(2, delay, runnable);
    }

    public void removeTimeout(){
        HandlerUtils.removeTimeout(2);
    }

}
