package com.heaton.baselib.crash;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.heaton.baselib.callback.CallBack;
import com.heaton.baselib.manager.UploadManager;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.utils.ThreadUtils;

import java.io.File;

/**
 * author: jerry
 * date: 20-4-30
 * email: superliu0911@gmail.com
 * des: 自定义捕获异常，并提交错误日志信息到服务器
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Context context;
    private CrashCollect crashCollect;

    private CrashUploader crashUploader;
    private Class targetClass;

    private CrashHandler(Builder builder) {
        this.crashUploader = builder.crashUploader;
        this.targetClass = builder.targetClass;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        crashCollect = new CrashCollect(context);
        //保存一份系统默认的CrashHandler
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //使用我们自定义的异常处理器替换程序默认的
        Thread.setDefaultUncaughtExceptionHandler(this);
        //读取上次崩溃日志,如果存在说明上次未提交成功,重新提交
        File file = crashCollect.getCrashLogFile();
        if (file.exists()){
            //上传上次的错误信息
            CrashInfo crashInfo = crashCollect.collectCrashInfo(null);
            UploadManager.uploadCrashInfo(crashInfo);
        }
    }

    public static class Builder {
        private CrashUploader crashUploader;
        private Class targetClass;

        public Builder crashUploader(CrashUploader crashUploader) {
            this.crashUploader = crashUploader;
            return this;
        }

        public Builder targetClass(Class targetClass) {
            this.targetClass = targetClass;
            return this;
        }

        public CrashHandler build(){
            return new CrashHandler(this);
        }
    }


    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
     *
     * @param t 出现未捕获异常的线程
     * @param e 未捕获的异常，有了这个ex，我们就可以得到异常信息
     */
    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        if (!catchCrashException(e) && defaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            defaultHandler.uncaughtException(t, e);
        } else {
            ThreadUtils.asyncDelay(4000L, new CallBack() {
                @Override
                public void execute() {
                    Log.e(TAG, "uncaughtException: 终止退出程序");
                    if (targetClass == null){
                        defaultHandler.uncaughtException(t, e);
                    }
                    //退出程序
                    //退出JVM(java虚拟机),释放所占内存资源,0表示正常退出(非0的都为异常退出)
                    System.exit(0);
                    //从操作系统中结束掉当前程序的进程
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean catchCrashException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        toTargetActivity();
        ThreadUtils.async(new CallBack() {
            @Override
            public void execute() {
                final CrashInfo crashInfo = crashCollect.collectCrashInfo(ex);
                crashCollect.saveCrashInfoToFile(crashInfo.getExceptionInfo());
                ThreadUtils.delay(1000);
                Log.e(TAG, "正在上传崩溃信息到服务器..." );
                if (crashUploader != null){
                    crashUploader.crashMessage(crashInfo);
                }else {
                    //上传崩溃信息到服务器
                    UploadManager.uploadCrashInfo(crashInfo);
                }
            }
        });
        return true;
    }

    private void toTargetActivity(){
        if (targetClass != null){
            Intent intent = new Intent();
            intent.setClass(context, targetClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    /**
     * 崩溃信息上传接口回调
     */
    public interface CrashUploader {
        void crashMessage(CrashInfo crashInfo);
    }
}
