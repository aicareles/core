package com.heaton.baselibsample.fragment;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.heaton.baselib.app.cache.ACache;
import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselib.crash.RCrashHandler;
import com.heaton.baselib.manager.UpdateManager;
import com.heaton.baselib.manager.UploadManager;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.utils.SPUtils;
import com.heaton.baselibsample.TwoActivity;
import com.heaton.baselibsample.bean.Article;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.bean.User;
import com.heaton.baselibsample.setting.SettingFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cn.com.superLei.aoparms.annotation.Async;
import cn.com.superLei.aoparms.annotation.Cache;
import cn.com.superLei.aoparms.annotation.Permission;
import cn.com.superLei.aoparms.annotation.PermissionDenied;
import cn.com.superLei.aoparms.annotation.PermissionNoAskDenied;
import cn.com.superLei.aoparms.annotation.Prefs;
import cn.com.superLei.aoparms.annotation.Retry;
import cn.com.superLei.aoparms.annotation.Safe;
import cn.com.superLei.aoparms.annotation.Scheduled;
import cn.com.superLei.aoparms.annotation.SingleClick;
import cn.com.superLei.aoparms.common.permission.AopPermissionUtils;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class HomeFragment extends BaseFragment {

    private static final String TAG = "HomeFragment";
    public static final int REQUEST_PERMISSION_WRITE = 2;
    public static final int REQUEST_PERMISSION_CAMERA = 3;
    private String str;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_home;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void bindData() {
        initCrash();
        //上传第一次安装信息
        UploadManager.uploadInstallInfo(getContext());
        //更新版本
        new UpdateManager(getActivity()).versionUpdate();
        //上传操作信息
        UploadManager.uploadStatusInfo(getContext());

        initData();

        initArticle();

        initLog();
    }

    private void initLog() {
        Article article = new Article();
        article.author = "tony";
        article.title = "kotlin in action";
        article.createDate = "2017-01-02";
        article.content = "just a test...";

        LogUtils.logd("initLog");
        LogUtils.logd(TAG, "initLog>>>");
        LogUtils.loge("initLoge");
        LogUtils.loge("initLoge" + article.toString());
        LogUtils.logwtf("initLogwtf" + article.toString());
        LogUtils.logjson("{\"articleId\": 2259,\n" +
                "            \"category\": \"Android\",\n" +
                "            \"childCategory\": 0,\n" +
                "            \"comments\": 0,\n" +
                "            \"contributor\": \"\",\n" +
                "            \"contributorId\": 1,\n" +
                "            \"date\": \"2019-04-18 00:00:00\",\n" +
                "            \"des\": \"一个帮助你快速实现底部导航的自定义控件。\"}");
    }

    @Permission(value = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
            requestCode = REQUEST_PERMISSION_WRITE)
    private void initCrash() {
        //初始化崩溃日志路径
        final String crashPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "BaseLibSample/crashLog";
        RCrashHandler.getInstance(crashPath).init(getContext(), null);
    }

    @Permission(value = {Manifest.permission.CAMERA}, rationale = "为了更好的体验，请打开相机权限", requestCode = REQUEST_PERMISSION_CAMERA)
    public void permission() {
        Log.e(TAG, "permission: 权限已打开");
    }

    @PermissionDenied
    public void permissionDenied(int requestCode, List<String> denyList) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            Log.e(TAG, "permissionDenied>>>:相机权限被拒 " + denyList.toString());
        } else if (requestCode == REQUEST_PERMISSION_WRITE) {
            Log.e(TAG, "permissionDenied>>>:读写权限被拒 " + denyList.toString());
        }
    }

    @PermissionNoAskDenied
    public void permissionNoAskDenied(int requestCode, List<String> denyNoAskList) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            Log.e(TAG, "permissionNoAskDenied 相机权限被拒>>>: " + denyNoAskList.toString());
        } else if (requestCode == REQUEST_PERMISSION_WRITE) {
            Log.e(TAG, "permissionDenied>>>:读写权限被拒>>> " + denyNoAskList.toString());
        }
        AopPermissionUtils.showGoSetting(getActivity(), "为了更好的体验，建议前往设置页面打开权限");
    }

    public void getArticle() {
        Article article = SPUtils.get(getContext(), "article", new Article());
        Log.e(TAG, "getArticle: " + article.toString());
    }

    @Cache(key = "userList")
    private ArrayList<User> initData() {
        ArrayList<User> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setName("艾神一不小心:" + i);
            user.setPassword("密码:" + i);
            list.add(user);
        }
        return list;
    }

    @Prefs(key = "article")
    private Article initArticle() {
        Article article = new Article();
        article.author = "tony";
        article.title = "kotlin in action";
        article.createDate = "2017-01-02";
        article.content = "just a test...";
        return article;
    }

    @Async
    public void asyn() {
        Log.e(TAG, "useAync: " + Thread.currentThread().getName());
    }

    @Safe(callBack = "throwMethod")
    public void safe() {
        str.toString();
    }

    @Safe
    private void throwMethod(Throwable throwable) {
        Log.e(TAG, "throwMethod: >>>>>" + throwable.toString());
    }

    @SingleClick
    public void getUser() {
        ArrayList<User> users = ACache.get(getContext()).getAsList("userList", User.class);
        Log.e(TAG, "getUser: " + users.toString());
    }

    @Retry(count = 3, delay = 1000, asyn = true, retryCallback = "retryCallback")
    public boolean retry() {
        Log.e(TAG, "retryDo: >>>>>>" + Thread.currentThread().getName());
        return false;
    }

    @Safe
    private void retryCallback(boolean result) {
        Log.e(TAG, "retryCallback: >>>>" + result);
    }

    @Scheduled(interval = 1000L, count = 10, taskExpiredCallback = "taskExpiredCallback")
    public void scheduled() {
        Log.e(TAG, "scheduled: >>>>");
    }

    @Safe
    private void taskExpiredCallback() {
        Log.e(TAG, "taskExpiredCallback: >>>>");
    }

    public void music(){
        toFragment(MusicFragment.newInstance());
    }

    private void toFragment(Fragment fragment){
        FragmentHold.showFragment(getFragmentManager(), fragment);
    }

    private void setting() {
        toFragment(SettingFragment.newInstance());
    }

    public void fragment1() {
        toFragment(Fragment1.newInstance());
    }

    public void fragment2() {
        toFragment(Fragment2.newInstance());
    }

    public void fragment3() {
        toFragment(Fragment3.newInstance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: >>>>");
    }

    @OnClick({R.id.getUser, R.id.getArticle, R.id.permission, R.id.retry, R.id.safe,
            R.id.asyn, R.id.scheduled, R.id.music, R.id.setting, R.id.two_activity,
            R.id.fragment1, R.id.fragment2, R.id.fragment3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getUser:
                getUser();
                break;
            case R.id.getArticle:
                getArticle();
                break;
            case R.id.permission:
                permission();
                break;
            case R.id.retry:
                retry();
                break;
            case R.id.safe:
                safe();
                break;
            case R.id.asyn:
                asyn();
                break;
            case R.id.scheduled:
                scheduled();
                break;
            case R.id.music:
                music();
                break;
            case R.id.setting:
                setting();
                break;
            case R.id.two_activity:
                toActivity(TwoActivity.class);
                break;
            case R.id.fragment1:
                fragment1();
                break;
            case R.id.fragment2:
                fragment2();
                break;
            case R.id.fragment3:
                fragment3();
                break;
        }
    }

}
