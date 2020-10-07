package com.heaton.baselib.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 任务结束回调onFinish的添加
 * Created by jerry on 2018/8/3.
 */

public abstract class SimpleFutureTask<T> extends FutureTask<T> {

    public SimpleFutureTask(Callable<T> callable) {
        super(callable);
    }

    @Override
    protected void done() {
        onFinish();
    }

    public abstract void onFinish();


}
