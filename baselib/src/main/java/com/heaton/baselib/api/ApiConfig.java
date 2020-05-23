package com.heaton.baselib.api;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public class ApiConfig {
    private String baseUrl;
    private Class apiServiceCls;

    public ApiConfig(String baseUrl, Class tClass) {
        this.baseUrl = baseUrl;
        this.apiServiceCls = tClass;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Class getApiServiceCls() {
        return apiServiceCls;
    }

    public void setApiServiceCls(Class apiServiceCls) {
        this.apiServiceCls = apiServiceCls;
    }
}
