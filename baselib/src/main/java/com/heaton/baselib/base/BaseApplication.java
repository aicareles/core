package com.heaton.baselib.base;

import android.app.Application;

import com.heaton.baselib.BaseCoreAPI;
import com.heaton.baselib.Configuration;

public class BaseApplication extends Application {

    private BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BaseCoreAPI.init(this, configuration());
    }

    //开启自定义配置
    protected Configuration configuration(){
        Configuration configuration = Configuration.defalut();
        configuration.loggable = true;
        return configuration;
    }
}
