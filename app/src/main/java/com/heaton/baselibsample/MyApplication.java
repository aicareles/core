package com.heaton.baselibsample;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.heaton.baselib.BaseCoreAPI;
import com.heaton.baselib.Configuration;
import com.heaton.baselib.api.ApiConfig;
import com.heaton.baselib.api.BaseResponse;
import com.heaton.baselib.app.language.Language;
import com.heaton.baselib.crash.CrashHandler;
import com.heaton.baselib.crash.CrashInfo;
import com.heaton.baselibsample.activity.CrashActivity;
import com.heaton.baselibsample.api.MyApiService;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;

import java.util.Locale;


/**
 * 应用入口
 * Created by LiuLei on 2016/4/25.
 * 　　　　　　　　┏┓　　　┏┓
 * 　　　　　　　┏┛┻━━━┛┻┓
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃
 * 　　　　　　　┃　＞　　　＜　┃
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃...　⌒　...　┃
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃   神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┗━━━┓
 * 　　　　　　　　　┃　　　　　　　┣┓
 * 　　　　　　　　　┃　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    private static MyApplication mApplication;

    /*protected MyApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.heaton.baselibsample.ApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        Language language = new Language(Language.MODE.AUTO, Locale.ENGLISH);
//        BaseResponse.Wrapper wrapper = new BaseResponse.Wrapper("status","msg","data");
        ApiConfig apiConfig = new ApiConfig("http://api.e-toys.cn/api/", MyApiService.class, null);
        Configuration configuration = new Configuration
                .Builder()
                .loggable(true)
                .logTag("CoreComponent")
                .language(language)
                .apiConfig(apiConfig)
                .build();
        BaseCoreAPI.init(this, configuration);
        initCrash();

//        Bugly.init(this, "69755c9a5d", true);
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String s) {
                Log.e(TAG, "onPatchReceived: 补丁下载地址:"+s);
            }

            @Override
            public void onDownloadReceived(long l, long l1) {
                Log.e(TAG, "onDownloadReceived: 补丁下载长度:"+l+"----"+l1);
            }

            @Override
            public void onDownloadSuccess(String s) {
                Log.e(TAG, "onDownloadSuccess: 补丁下载成功:"+s);
            }

            @Override
            public void onDownloadFailure(String s) {
                Log.e(TAG, "onDownloadFailure: 补丁下载失败:"+s);
            }

            @Override
            public void onApplySuccess(String s) {
                Log.e(TAG, "onApplySuccess: 补丁应用成功:"+s);
            }

            @Override
            public void onApplyFailure(String s) {
                Log.e(TAG, "onApplyFailure: 补丁应用失败:"+s);
            }

            @Override
            public void onPatchRollback() {

            }
        };
    }

    private void initCrash() {
        new CrashHandler.Builder()
                .targetClass(CrashActivity.class)
                .crashUploader(new CrashHandler.CrashUploader() {
                    @Override
                    public void crashMessage(CrashInfo crashInfo) {
                        Log.e(TAG, "uploadCrashMessage: "+crashInfo.toString());
                    }
                })
                .build()
                .init(getApplicationContext());
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
//        Beta.installTinker();
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

}
/**
 * 　                   &#######&
 * 　                   #########&
 * 　                  ###########&
 * 　                 ##&#$###$  ##&
 * 　                ;###  ####& ####
 * 　                ###;#######  ####
 * 　               &###########  #####
 * 　              ;#########o##   #####
 * 　              #########  ##   ######
 * 　            ;########### ###   ######
 * 　            ################   #######
 * 　           #################;   ######,
 * 　           #############$####   #######
 * 　          ########&;,########   &#######
 * 　        ;#########   &###       ########
 * 　        ##########    ###;      ########
 * 　       ###########     ##$      ########
 * 　       ###########     ###     #########
 * 　       ##########&$     ##    ;########
 * 　       #########, !      ##   ########
 * 　      ;########&          ##  #######
 * 　        #&#####            ##   &#&
 * 　          o# &#             #;
 * 　             ##             ##
 * 　             &#&           ;##
 * 　              ##           ###
 * <p/>
 * 　　　　　　　　　葱官赐福　　百无禁忌
 */
