package com.heaton.baselib.api;

import com.google.gson.annotations.SerializedName;

/**
 * 获取json数据基类
 */

public class BaseResponse<T> implements ErrorStatus{

    @SerializedName("status")
    public int status;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public T data;

    /**
     * 是否请求成功
     *
     * @return 是否成功
     */
    public boolean isSuccess() {
        return status == OK;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
