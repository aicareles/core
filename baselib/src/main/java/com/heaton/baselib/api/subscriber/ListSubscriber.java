package com.heaton.baselib.api.subscriber;

import android.util.Log;

import com.heaton.baselib.api.BaseResponse;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 集合的请求
 *
 * @param <T>
 */
public class ListSubscriber<T> implements Observer<BaseResponse<List<T>>> {

    private static final String TAG = "ListSubscriber";

    private SubscriberListener<BaseResponse<List<T>>> mListener;
    //用于取消连接
    private Disposable mDisposeable;

    public ListSubscriber(SubscriberListener<BaseResponse<List<T>>> listener) {
        this.mListener = listener;
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Log.i(TAG, "网络中断，请检查您的网络状态！");
        } else if (e instanceof ConnectException) {
            Log.i(TAG, "网络中断，请检查您的网络状态！");
        } else {
            Log.i(TAG, "error:" + e.getMessage());
        }
        mListener.onFail(e.getMessage());
    }

    @Override
    public void onComplete() {
        Log.i(TAG, "onCompleted: 获取数据完成！");
    }

    @Override
    public void onSubscribe(Disposable d) {
        //拿到Disposable对象可随时取消请求
        mDisposeable = d;
    }

    @Override
    public void onNext(@NonNull BaseResponse<List<T>> response) {
        if (mListener != null) {
            if (response.isSuccess()) {
                mListener.onSuccess(response);
            } else {
                mListener.onFail(response.getMsg());
            }
        }
    }

}
