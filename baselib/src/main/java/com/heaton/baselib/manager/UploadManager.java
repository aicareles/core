package com.heaton.baselib.manager;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.heaton.baselib.LogInterceptor;
import com.heaton.baselib.Constance;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.BluetoothUtils;
import com.heaton.baselib.utils.FileUtils;
import com.heaton.baselib.utils.SPUtils;
import com.heaton.baselib.utils.TimeUtils;

import java.io.File;
import java.io.IOException;

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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    public static void uploadCrashInfo(Context context, final File crashFile){
        //读取txt数据
        String crashInfo = FileUtils.getFileContent(crashFile);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("app_package", AppUtils.getPackageName(context));
        formBody.add("app_channel", AppUtils.getAppMetaData(context, "HEATON_CHANNEL"));
        formBody.add("phone_system", Constance.APP.PLATFORM);
        formBody.add("phone_brands", AppUtils.getDeviceBrand());
        formBody.add("phone_model", AppUtils.getSystemModel());
        formBody.add("phone_system_version", AppUtils.getSystemVersion());
        formBody.add("app_version_name", AppUtils.getVersionName(context));
        formBody.add("app_version_code", String.valueOf(AppUtils.getVersionCode(context)));
        formBody.add("exception_info", crashInfo);
        Request request = new Request.Builder()//创建Request 对象。
                .url(Constance.API.BASE_URL+Constance.API.APP_UPLOAD_CRASH)
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
                    com.alibaba.fastjson.JSONObject dataObject = JSON.parseObject(json);
                    if (dataObject.getInteger("status") == 0) {
                        Log.i(TAG, "onResponse: 上传错误日志信息成功");
                        //删除文件
                        crashFile.delete();
                    } else {
                        Log.e(TAG, "onResponse: 上传错误日志信息失败");
                    }
                } catch (com.alibaba.fastjson.JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
