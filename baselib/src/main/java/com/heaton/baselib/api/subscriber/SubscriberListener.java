package com.heaton.baselib.api.subscriber;

public interface SubscriberListener<T> {
    void onSuccess(T t);
    void onFail(String msg);
}
