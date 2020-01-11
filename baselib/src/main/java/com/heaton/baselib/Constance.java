package com.heaton.baselib;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建时间:  2018/1/5
 * 创建人:Alex-Jerry
 * 功能描述: 全局静态常量值
 */

public class Constance {
    /**
     * SharePreferences常量保存类
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface SP {
        String FIRST_INSTALL = "first_install";//应用是否第一次被安装
        String LANGUAGE = "language";//语言
    }

    //API静态
    @Retention(RetentionPolicy.SOURCE)
    public @interface API {
        String BASE_URL = "http://api.e-toys.cn/api/";//服务器路径
        String APP_UPLOAD_INSTALL   = "app/count";//上传安装信息的接口
        String APP_LAST_UPDATE     = "app/lastUpdate";//获取应用新版本
        String APP_UPLOAD_STATUS_INFO     = "App/add_app_status_info";//上传app状态信息（扫描时用）
        String APP_UPLOAD_CRASH     = "App/add_app_crash";//上传崩溃日志信息
    }

    //app
    @Retention(RetentionPolicy.SOURCE)
    public @interface APP {
        String PLATFORM = "Android";
    }

}
