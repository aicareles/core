package com.heaton.baselib.base;

import android.app.Application;

import com.heaton.baselib.CoreBase;
import com.heaton.baselib.Configuration;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CoreBase.init(this, configuration());
    }

    //开启自定义配置
    protected Configuration configuration(){
        return Configuration.defalut();
    }
}
