package com.heaton.baselib.callback;

/**
 * Description:
 * Data：2018/10/26-10:47
 * Author: Allen
 */
public interface RxCallBackUI<T> {

    T execute();

    void callBackUI(T t);
}
