package com.heaton.baselib.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heaton.baselib.callback.HandleBackInterface;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseFragment extends Fragment implements HandleBackInterface {

    protected View mRootView;
    private Unbinder mUnBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = inflater.inflate(layoutId(), null);
            mUnBinder = ButterKnife.bind(this, mRootView);
        }
        bindData();
        bindListener();
        return mRootView;
    }

    protected abstract int layoutId();

    protected abstract void bindData();

    protected void bindListener(){}

    protected void toActivity(@NonNull Class cl) {
        startActivity(new Intent(getActivity(), cl));
    }

    protected void toActivity(@NonNull Class cl, Bundle bundle) {
        Intent intent = new Intent(getActivity(), cl);
        intent.putExtra(cl.getSimpleName(), bundle);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    protected View inflate(int resId) {
        return LayoutInflater.from(getActivity()).inflate(resId, null);
    }

    @Override
    public boolean onBackPressed() {
        //fragment中返回键拦截
        return false;//默认不处理
    }

}
