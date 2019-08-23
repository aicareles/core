package com.heaton.baselib.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by jerry on 2018/8/3.
 */

public class FutureThreadPool {

    private FutureThreadPool(){}
    private volatile static FutureThreadPool futureThreadPool;
    private static ExecutorService threadExecutor;
    /**
     * 获取线程池实例（单例模式）
     * @return
     */
    public static FutureThreadPool getInstance(){
        if(futureThreadPool==null){
            synchronized (FutureThreadPool.class) {
                futureThreadPool=new FutureThreadPool();
                threadExecutor= Executors.newSingleThreadExecutor();
            }
        }
        return futureThreadPool;
    }


    /**
     * 线程池处理Runnable(无返回值)
     * @param runnable Runnable参数
     */
    public void executeTask(Runnable runnable){
        threadExecutor.execute(runnable);
    }

    /**
     * 线程池处理Callable<T>，FutureTask<T>类型有返回值
     * @param callable Callable<T>参数
     * @return FutureTask<T>
     */
    public <T> FutureTask<T> executeTask(Callable<T> callable){
        FutureTask<T> futureTask= new FutureTask<T>(callable);
        threadExecutor.submit(futureTask);
        return futureTask;

    }
    /**
     * 线程池处理Runnable，FutureTask<T>类型有返回值(该方法不常用)
     * @param  runnable
     * @param  result Runnable任务执行完成后，返回的标识（注意：在调用时传入值，将在Runnable执行完成后，原样传出）
     * @return FutureTask<T>
     */
    public <T> FutureTask<T> executeTask(Runnable runnable,T result){
        FutureTask<T> futureTask= new FutureTask<T>(runnable,result);
        threadExecutor.submit(futureTask);
        return futureTask;
    }
    /**
     * 线程池处理自定义SimpleFutureTask，任务结束时有onFinish事件返回提示
     * @param mFutureTask 自定义SimpleFutureTask
     */
    public  <T> FutureTask<T>  executeFutureTask(SimpleFutureTask<T> mFutureTask){
        threadExecutor.submit(mFutureTask);
        return mFutureTask;
    }

}
