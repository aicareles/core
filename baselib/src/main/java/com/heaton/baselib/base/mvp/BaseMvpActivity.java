package com.heaton.baselib.base.mvp;

import android.os.Bundle;

import com.heaton.baselib.base.BaseActivity;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public abstract class BaseMvpActivity<P extends BaseMvpPresenter> extends BaseActivity implements MvpView {
    protected P presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.attachView(this);
    }

    protected abstract P createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        presenter = null;
    }
}
