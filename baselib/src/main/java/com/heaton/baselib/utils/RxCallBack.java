package com.heaton.baselib.utils;

/**
 * Description:
 * Data：2018/10/26-10:47
 * Author: Allen
 */
public interface RxCallBack<T> {

    T doSomeThing();

    void callBackUI(T t);
}
