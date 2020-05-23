package com.heaton.baselib.base.mvp;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public interface MvpPresenter<V extends MvpView> {
    void attachView(V view);

    void detachView();
}
