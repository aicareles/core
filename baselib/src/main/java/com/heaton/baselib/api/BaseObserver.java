package com.heaton.baselib.api;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author: jerry
 * date: 20-5-23
 * email: superliu0911@gmail.com
 * des:  可以统一使用这个
 */
public abstract class BaseObserver<T> implements Observer<BaseResponse<T>> {

    private ProgressDialog dialog;

    @Override
    public void onSubscribe(Disposable d) {
        onRequestStart();
    }

    @Override
    public void onNext(BaseResponse<T> response) {
        onSuccess(response);
        onRequestEnd();
    }

    @Override
    public void onError(Throwable e) {
//        Log.w(TAG, "onError: ", );这里可以打印错误信息
        onRequestEnd();
        try {
            if (e instanceof ConnectException
                    || e instanceof TimeoutException
                    || e instanceof NetworkErrorException
                    || e instanceof UnknownHostException) {
                onFail("请检查您的网络设置");
            } else {
                onFail(e.getMessage());
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onComplete() {}

    protected abstract void onSuccess(BaseResponse<T> response);

    /**
     * 返回失败
     */
    protected abstract void onFail(String msg);

    protected void onRequestStart() {}

    protected void onRequestEnd() {}

    public void showProgressDialog(Context context,String title,String message) {
        dialog = ProgressDialog.show(context, title, message);
    }

    public void closeProgressDialog() {
        if (dialog != null){
            dialog.dismiss();
        }
    }


}
