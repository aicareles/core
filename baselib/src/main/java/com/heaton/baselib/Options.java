package com.heaton.baselib;

//配置类
public class Options {
    private boolean isLoggable;

    public Options() {
    }

    public Options(boolean isLoggable) {
        this.isLoggable = isLoggable;
    }

    public boolean isLoggable() {
        return isLoggable;
    }

    public void setLoggable(boolean loggable) {
        isLoggable = loggable;
    }
}
