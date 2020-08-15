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
    private BaseResponse.Wrapper wrapper;

    public ApiConfig(String baseUrl, Class apiServiceCls, BaseResponse.Wrapper wrapper) {
        this.baseUrl = baseUrl;
        this.apiServiceCls = apiServiceCls;
        this.wrapper = wrapper;
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

    public BaseResponse.Wrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(BaseResponse.Wrapper wrapper) {
        this.wrapper = wrapper;
    }
}
