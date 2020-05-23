package com.heaton.baselib.base.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heaton.baselib.base.BaseFragment;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public abstract class BaseMvpFragment<P extends BaseMvpPresenter> extends BaseFragment implements MvpView {
    protected P presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        presenter = createPresenter();
        presenter.attachView(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract P createPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }
}
