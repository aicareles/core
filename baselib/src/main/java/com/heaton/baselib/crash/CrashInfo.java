package com.heaton.baselib.crash;

/**
 * author: jerry
 * date: 20-4-30
 * email: superliu0911@gmail.com
 * des: 崩溃详细信息
 */
public class CrashInfo {
    private String appPackage;
    private String appChannel;
    private String phoneSystem;
    private String phoneBrands;
    private String phoneModel;
    private String phoneSystemVersion;
    private String appVersionName;
    private String appVersionCode;
    private String exceptionInfo;

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public String getAppChannel() {
        return appChannel;
    }

    public void setAppChannel(String appChannel) {
        this.appChannel = appChannel;
    }

    public String getPhoneSystem() {
        return phoneSystem;
    }

    public void setPhoneSystem(String phoneSystem) {
        this.phoneSystem = phoneSystem;
    }

    public String getPhoneBrands() {
        return phoneBrands;
    }

    public void setPhoneBrands(String phoneBrands) {
        this.phoneBrands = phoneBrands;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneSystemVersion() {
        return phoneSystemVersion;
    }

    public void setPhoneSystemVersion(String phoneSystemVersion) {
        this.phoneSystemVersion = phoneSystemVersion;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getExceptionInfo() {
        return exceptionInfo;
    }

    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    @Override
    public String toString() {
        return "CrashInfo{" +
                "appPackage='" + appPackage + '\'' +
                ", appChannel='" + appChannel + '\'' +
                ", phoneSystem='" + phoneSystem + '\'' +
                ", phoneBrands='" + phoneBrands + '\'' +
                ", phoneModel='" + phoneModel + '\'' +
                ", phoneSystemVersion='" + phoneSystemVersion + '\'' +
                ", appVersionName='" + appVersionName + '\'' +
                ", appVersionCode='" + appVersionCode + '\'' +
                ", exceptionInfo='" + exceptionInfo + '\'' +
                '}';
    }
}
