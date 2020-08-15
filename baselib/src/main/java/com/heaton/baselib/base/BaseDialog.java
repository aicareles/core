package com.heaton.baselib.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

/**
 * 对话框基类
 * Created by jerry on 2018/8/17.
 */

public abstract class BaseDialog extends Dialog {
    protected final String TAG = this.getClass().getSimpleName();

    public Activity mActivity;

    public BaseDialog(@NonNull Context context) {
        super(context);
        this.mActivity = (Activity) context;
    }

    public BaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setOwnerActivity((Activity) context);
        this.mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(mActivity, layoutId(), null);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            //消除弹框白色背景
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        bindData();
        bindListener();
    }

    protected abstract int layoutId();

    protected abstract void bindData();

    protected void bindListener(){}

    public void toast(int resid){
        Toast.makeText(mActivity, resid, Toast.LENGTH_SHORT).show();
    }

    public void toast(String msg){
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

}
