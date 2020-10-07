package com.heaton.baselib.manager;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.heaton.baselib.LogInterceptor;
import com.heaton.baselib.Constance;
import com.heaton.baselib.crash.CrashCollect;
import com.heaton.baselib.crash.CrashHandler;
import com.heaton.baselib.crash.CrashInfo;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.BluetoothUtils;
import com.heaton.baselib.utils.FileUtils;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.utils.SPUtils;
import com.heaton.baselib.utils.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * description $desc$
 * created by jerry on 2019/4/10.
 */
public class UploadManager {

    private static final String TAG = "UploadManager";

    /**
     * APP第一次安装，上传App相关信息
     * @param context
     */
    public static void uploadInstallInfo(final Context context){
        boolean hasUploadInfo = SPUtils.get(context, Constance.SP.FIRST_INSTALL, true);
        if (!hasUploadInfo)return;
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("app_package", AppUtils.getPackageName(context));
        formBody.add("app_channel", AppUtils.getAppMetaData(context,"HEATON_CHANNEL"));
        formBody.add("phone_system", Constance.APP.PLATFORM);
        formBody.add("phone_brands", AppUtils.getDeviceBrand());
        formBody.add("phone_model" , AppUtils.getSystemModel());
        formBody.add("phone_system_version", AppUtils.getSystemVersion());
        formBody.add("run_time", TimeUtils.getNowTime());
        Request request = new Request.Builder()//创建Request 对象。
                .url(Constance.API.BASE_URL+Constance.API.APP_UPLOAD_INSTALL)
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject dataObject = JSON.parseObject(json);
                    if (dataObject.getInteger("status") == 0){
                        Log.i(TAG, "onResponse: 上传安装信息成功");
                        SPUtils.put(context, Constance.SP.FIRST_INSTALL, false);
                    }else {
                        Log.e(TAG, "onResponse: 上传安装信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 上传扫描前状态日志
     * @param context
     */
    public static void uploadStatusInfo(Context context){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("phone_system", Constance.APP.PLATFORM);
        formBody.add("phone_brands", AppUtils.getDeviceBrand());
        formBody.add("phone_model" , AppUtils.getSystemModel());
        formBody.add("phone_system_version", AppUtils.getSystemVersion());
        formBody.add("app_package", AppUtils.getPackageName(context));
        formBody.add("app_version_name", AppUtils.getVersionName(context));
        formBody.add("app_version_code", String.valueOf(AppUtils.getVersionCode(context)));
        formBody.add("bluetooth_status", String.valueOf(BluetoothUtils.isBleEnable()?1:0));
        formBody.add("wireless_support_status", String.valueOf(BluetoothUtils.isSupportAdvertiser()?1:0));
        formBody.add("gps_status", String.valueOf(AppUtils.isGpsOpen(context)?1:0));
        formBody.add("permission_status", String.valueOf(AppUtils.isPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)?1:0));
        Request request = new Request.Builder()//创建Request 对象。
                .url(Constance.API.BASE_URL+Constance.API.APP_UPLOAD_STATUS_INFO)
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject dataObject = JSON.parseObject(json);
                    if (dataObject.getInteger("status") == 0){
                        Log.i(TAG, "onResponse: 上传设备日志信息成功");
                    }else {
                        Log.e(TAG, "onResponse: 上传设备日志信息失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 上传错误信息
     *
     * @param
     */
    public static void uploadCrashInfo(final CrashInfo crashInfo){
        //读取txt数据
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("app_package", crashInfo.getAppPackage());
        formBody.add("app_channel", crashInfo.getAppChannel());
        formBody.add("phone_system", crashInfo.getPhoneSystem());
        formBody.add("phone_brands", crashInfo.getPhoneBrands());
        formBody.add("phone_model", crashInfo.getPhoneModel());
        formBody.add("phone_system_version", crashInfo.getPhoneSystemVersion());
        formBody.add("app_version_name", crashInfo.getAppVersionName());
        formBody.add("app_version_code", crashInfo.getAppVersionCode());
        formBody.add("exception_info", Base64.encodeToString(crashInfo.getExceptionInfo().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
        Request request = new Request.Builder()//创建Request 对象。
                .url(Constance.API.BASE_URL+Constance.API.APP_UPLOAD_CRASH)
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.loge("UploadManager>>>[onFailure]: 上传错误日志信息出错");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    com.alibaba.fastjson.JSONObject dataObject = JSON.parseObject(json);
                    if (dataObject.getInteger("status") == 0) {
                        LogUtils.logi("UploadManager>>>[onResponse]: 上传错误日志信息成功");
                        //删除文件
                        File crashLogFile = CrashCollect.getCrashLogFile();
                        if (crashLogFile.exists()){
                            crashLogFile.delete();
                        }
                    } else {
                        LogUtils.loge("UploadManager>>>[onResponse]: 上传错误日志信息失败");
                    }
                } catch (com.alibaba.fastjson.JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
