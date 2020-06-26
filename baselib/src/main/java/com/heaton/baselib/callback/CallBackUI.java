package com.heaton.baselib.callback;

import io.reactivex.disposables.Disposable;

/**
 * Description:
 * Data：2018/10/26-10:47
 * Author: Allen
 */
public abstract class CallBackUI<T> {

    public void onPreExecute(Disposable d){}

    public abstract T execute();

    public abstract void callBackUI(T t);
}
