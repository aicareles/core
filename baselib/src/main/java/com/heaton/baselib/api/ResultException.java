package com.heaton.baselib.api;

public class ResultException extends Throwable {

    private int status;
    private String msg;

    public ResultException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public ResultException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        // TODO Auto-generated constructor stub
    }

    public ResultException(int status, String msg) {
        this.status = status;
        this.msg = msg;
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
}
