package com.heaton.baselibsample.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.heaton.baselib.app.cache.ACache;
import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselib.callback.CallBack;
import com.heaton.baselib.manager.DownloadManager;
import com.heaton.baselib.manager.UpdateManager;
import com.heaton.baselib.manager.UploadManager;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.DialogUtils;
import com.heaton.baselib.utils.FileUtils;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.utils.NotificationUtil;
import com.heaton.baselib.utils.ThreadUtils;
import com.heaton.baselib.utils.SPUtils;
import com.heaton.baselib.utils.ToastUtil;
import com.heaton.baselibsample.MainActivity;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.bean.Article;
import com.heaton.baselibsample.bean.User;
import com.heaton.baselibsample.navigation.NavigationActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

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

    public void getArticle() {
        Article article = SPUtils.get(getContext(), "article", new Article());
        Log.e(TAG, "getArticle: " + article.toString());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: >>>>");
    }

    @OnClick({R.id.getUser, R.id.getArticle, R.id.permission, R.id.retry, R.id.safe,
            R.id.asyn, R.id.asyn_delay, R.id.scheduled, R.id.nav, R.id.download,
            R.id.dialog_default, R.id.dialog_custom, R.id.dialog_progress, R.id.dialog_bottom})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getUser:
//                ToastUtil.show(R.string.test);
                new Dialog1().show(getFragmentManager(),"");
                break;
            case R.id.getArticle:
//                getArticle();
                toast(R.string.test);
                break;
            case R.id.permission:
                break;
            case R.id.retry:
                break;
            case R.id.safe:
                break;
            case R.id.asyn:
                break;
            case R.id.asyn_delay:
                break;
            case R.id.scheduled:
                break;
            case R.id.nav:
                toActivity(NavigationActivity.class);
                break;
            case R.id.download:
                String url = "http://app.heaton.cn/Puff%20The%20Magic%20Dargan.mp3";
                String filePath = new File(mActivity.getExternalFilesDir(Environment.DIRECTORY_MUSIC), FileUtils.getFileNameByUrl(url)).getAbsolutePath();
                DownloadManager.download(url, filePath, new DownloadManager.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(String path) {
                        LogUtils.logi("HomeFragment>>>[onDownloadSuccess]: "+path);
                    }

                    @Override
                    public void onDownloading(int progress) {
                        LogUtils.logi("HomeFragment>>>[onDownloading]: "+progress);
                    }

                    @Override
                    public void onDownloadFailed(String msg) {
                        LogUtils.logi("HomeFragment>>>[onDownloadFailed]: "+msg);
                    }
                });
                break;
            case R.id.dialog_default:
                DialogUtils.showCommonDialog(mActivity, "提示", "这是普通的对话框", new DialogUtils.DialogCall() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        toast("positivie");
                    }

                    @Override
                    public void onNegative() {
                        toast("onNegative");
                    }
                });
                break;
            case R.id.dialog_custom:
                DialogUtils.showCustomDialog(mActivity, "提示", R.layout.update_layout, new DialogUtils.DialogCall() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        toast("positivie");
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
