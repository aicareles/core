package com.heaton.baselib.api.subscriber;

import android.util.Log;

import com.heaton.baselib.api.BaseResponse;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * 简单的请求
 *
 * @param <T>
 */
public class SimpleSubscriber<T> implements Observer<BaseResponse> {

    private static final String TAG = "SimpleSubscriber";

    private SubscriberListener<BaseResponse> mListener;
    //用于取消连接
    private Disposable mDisposeable;

    public SimpleSubscriber(SubscriberListener<BaseResponse> listener) {
        this.mListener = listener;
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            Log.i(TAG, "网络中断，请检查您的网络状态！");
            mListener.onFail("请检查您的网络设置");
        } else if (e instanceof ConnectException) {
            Log.i(TAG, "网络中断，请检查您的网络状态！");
            mListener.onFail("请检查您的网络设置");
        } else if (e instanceof HttpException) {
            mListener.onFail("请检查您的网络设置");
        } else {
            Log.i(TAG, "error:" + e.getMessage());
            mListener.onFail(e.getMessage());
        }
        e.printStackTrace();

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
    public void onNext(BaseResponse response) {
        if (mListener != null) {
            if (response.isSuccess()) {
                mListener.onSuccess(response);
            } else {
                mListener.onFail(response.getMsg());
            }
        }
    }

}
