package com.heaton.baselib.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public abstract class BaseDialogFragment extends DialogFragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected View mRootView;
    private Unbinder mUnBinder;

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(null);
        //设置边距
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(layoutId(), null);
            mUnBinder = ButterKnife.bind(this, mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindData();
        bindListener();
    }

    protected abstract int layoutId();

    protected abstract void bindData();

    protected void bindListener(){}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    protected View inflate(int resId) {
        return LayoutInflater.from(getActivity()).inflate(resId, null);
    }
}
