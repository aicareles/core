package com.heaton.baselibsample.fragment;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.heaton.baselib.app.cache.ACache;
import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselib.callback.CallBack;
import com.heaton.baselib.manager.UpdateManager;
import com.heaton.baselib.manager.UploadManager;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.DialogUtils;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.utils.NotificationUtil;
import com.heaton.baselib.utils.ThreadUtils;
import com.heaton.baselib.utils.SPUtils;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.bean.Article;
import com.heaton.baselibsample.bean.User;
import com.heaton.baselibsample.navigation.NavigationActivity;

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

    @Override
    protected void bindData() {
        //上传第一次安装信息
        UploadManager.uploadInstallInfo(getContext());
        //更新版本
        updateVersion();
        //上传操作信息
        UploadManager.uploadStatusInfo(getContext());

        initData();

        initArticle();

        initLog();

        if (!NotificationUtil.isNotificationEnabled(getContext())){
            NotificationUtil.enableNotificationToSet(getContext());
        }

    }

    private void updateVersion() {
        String channel = AppUtils.getAppMetaData(getContext(), "HEATON_CHANNEL");
        if (channel.equals("google")){
            new UpdateManager.Builder()
                    .iconLarge(R.mipmap.ic_launcher)
                    .iconSmall(R.mipmap.ic_launcher)
                    .build(getActivity())
                    .versionUpdate();
        }
    }

    private void initLog() {
        Article article = new Article();
        article.author = "tony";
        article.title = "kotlin in action";
        article.createDate = "2017-01-02";
        article.content = "just a test...";

        LogUtils.logd("initLog");
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

    private void asynDelay() {
        ThreadUtils.asyncDelay(3000, new CallBack() {
            @Override
            public void execute() {
                Log.e(TAG, "asynDelay:"+Thread.currentThread().getName());
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: >>>>");
    }

    @OnClick({R.id.getUser, R.id.getArticle, R.id.permission, R.id.retry, R.id.safe,
            R.id.asyn, R.id.asyn_delay, R.id.scheduled, R.id.nav,
            R.id.dialog_default, R.id.dialog_custom, R.id.dialog_progress, R.id.dialog_bottom})
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
            case R.id.asyn_delay:
                asynDelay();
                break;
            case R.id.scheduled:
                scheduled();
                break;
            case R.id.nav:
                toActivity(NavigationActivity.class);
                break;
            case R.id.dialog_default:
                DialogUtils.showCommonDialog(mActivity, "提示", "这是普通的对话框", new DialogUtils.DialogCall() {
                    @Override
                    public void onPositive() {
                        toast("positivie");
                    }

                    @Override
                    public void onNegative() {
                        toast("onNegative");
                    }
                });
                break;
            case R.id.dialog_custom:
                DialogUtils.showCustomDialog(mActivity, "提示", "这是普通的对话框", R.layout.update_layout, new DialogUtils.DialogCall() {
                    @Override
                    public void onPositive() {
                        toast("positivie");
                    }

                    @Override
                    public void onNegative() {
                        toast("onNegative");
                    }
                });
                break;
            case R.id.dialog_progress:
                DialogUtils.showProgressDialog(mActivity, "加载中...");
                break;
            case R.id.dialog_bottom:
                DialogUtils.showBottomDialog(mActivity, R.layout.dialog_bottom);
                break;
        }
    }

}
