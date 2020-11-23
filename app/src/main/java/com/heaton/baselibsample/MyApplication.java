package com.heaton.baselibsample;

import android.app.Application;

import com.heaton.baselib.BaseCoreAPI;
import com.heaton.baselib.Configuration;
import com.heaton.baselib.api.ApiConfig;
import com.heaton.baselib.app.language.Language;
import com.heaton.baselib.crash.CrashHandler;
import com.heaton.baselibsample.api.MyApiService;

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

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        Language language = new Language(Language.MODE.CUSTOM, Locale.CHINA);
//        BaseResponse.Wrapper wrapper = new BaseResponse.Wrapper("status","msg","data");
        ApiConfig apiConfig = new ApiConfig("http://api.e-toys.cn/api/", MyApiService.class, null);
        Configuration configuration = new Configuration
                .Builder()
                .loggable(true)
                .logTag("CoreComponent")
                .language(language)
                .langable(false)
                .apiConfig(apiConfig)
                .build();
        BaseCoreAPI.init(this, configuration);
        initCrash();

    }

    private void initCrash() {
        new CrashHandler.Builder()
//                .targetClass(CrashActivity.class)
                /*.crashUploader(new CrashHandler.CrashUploader() {
                    @Override
                    public void crashMessage(CrashInfo crashInfo) {
                        Log.e(TAG, "uploadCrashMessage: "+crashInfo.toString());
                    }
                })*/
                .build()
                .init(getApplicationContext());
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
