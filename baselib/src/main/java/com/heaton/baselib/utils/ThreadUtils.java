package com.heaton.baselib.utils;

import android.os.Handler;
import android.os.Looper;

import com.heaton.baselib.callback.CallBack;
import com.heaton.baselib.callback.CallBackUI;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Description:封装的rxjava切换线程的通用工具，子线程完成任务切换成到主线程
 * Data：2018/10/26-10:39
 * Author: Allen
 */
public class ThreadUtils {
    private static final Handler MAIN_HANDLER       = new Handler(Looper.getMainLooper());

    public static <T> void asyncCallback(final CallBackUI<T> callBackUI) {
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {
                //onNext中的参数不能为null，否则onNext接收不到
                emitter.onNext(callBackUI.execute());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {
                callBackUI.onPreExecute(d);
            }

            @Override
            public void onNext(T t) {
                callBackUI.callBackUI(t);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public static void async(final CallBack callBack){
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                callBack.execute();
            }
        });
    }

    public static void asyncDelay(final long delay, final CallBack callBack){
        TaskExecutor.executeTask(new Runnable() {
            @Override
            public void run() {
                delay(delay);
                callBack.execute();
            }
        });
    }

    public static void ui(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            MAIN_HANDLER.post(runnable);
        }
    }

    public static void uiDelay(final Runnable runnable, long delayMillis) {
        MAIN_HANDLER.postDelayed(runnable, delayMillis);
    }

    public static void delay(long delay){
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
