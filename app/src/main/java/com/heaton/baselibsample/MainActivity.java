package com.heaton.baselibsample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.heaton.baselib.app.language.Language;
import com.heaton.baselib.app.language.LanguageManager;
import com.heaton.baselib.base.BaseActivity;
import com.heaton.baselib.callback.ActivityResultCallback;
import com.heaton.baselib.callback.CallBackUI;
import com.heaton.baselib.permission.IPermission;
import com.heaton.baselib.utils.HandlerUtils;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.utils.ThreadUtils;
import com.heaton.baselib.widget.NavigationBar;
import com.heaton.baselibsample.activity.LanguageActivity;
import com.heaton.baselibsample.fragment.HomeFragment;
import com.heaton.baselibsample.fragment.MusicFragment;
import com.heaton.baselibsample.fragment.SettingFragment;

import butterknife.BindView;
import io.reactivex.disposables.Disposable;


public class MainActivity extends BaseActivity {

    private String[] permission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    private String ss = null;
//    private String ss = "修复后的数据";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getDarkModeStatus(this)) {
            setTheme(R.style.main_theme_dark);
        }else {
            setTheme(R.style.main_theme_light);
            LogUtils.logi("MainActivity>>>[onCreate]: "+getDarkModeStatus(this));
        }
        super.onCreate(savedInstanceState);

        toast(R.string.test);

//        toActivityParams(LanguageActivity.class).put("a", 1).put("b", "b").start();

        /*toActivityForResult(LanguageActivity.class, new ActivityResultCallback() {
            @Override
            public void onActivityResult(int resultCode, Intent data) {
                LogUtils.logi("MainActivity>>>[onActivityResult]: "+resultCode+"---"+data.getStringExtra("data"));
            }
        });*/

        ThreadUtils.asyncCallback(new CallBackUI<Boolean>() {
            @Override
            public void onPreExecute(Disposable d) {
                super.onPreExecute(d);
                Log.e(TAG, "onPreExecute: ");
                showProgressDialog("下载中...");
            }

            @Override
            public Boolean execute() {
                Log.e(TAG, "execute: ");
                return false;
            }

            @Override
            public void callBackUI(Boolean aBoolean) {
                Log.e(TAG, "callBackUI: ");
            }
        });

        requestPermission(permission, "拍照需要访问摄像头权限", new IPermission() {
            @Override
            public void permissionGranted() {
                LogUtils.logi("MainActivity>>>[permissionGranted]: ");
            }
        });

        showProgressDialog("扫描中...");
        setTimeout(4000, () -> {
            LogUtils.logi("MainActivity>>>[onCreate]: 哈哈哈");
            toast("扫描超时，没有扫描到任何设备");
            dismissProgressDialog();
        });

        ThreadUtils.uiDelay(()->{
            removeTimeout();
            dismissProgressDialog();
            toast("已扫描到设备");
        },2000);

    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void bindData() {
        navigationBar.addTab(HomeFragment.class, new NavigationBar.TabParam(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "0"));
        navigationBar.addTab(MusicFragment.class, new NavigationBar.TabParam(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "1"));
        navigationBar.addTab(SettingFragment.class, new NavigationBar.TabParam(R.mipmap.ic_launcher, R.mipmap.ic_launcher, "2"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //将这一行注释掉，阻止activity保存fragment的状态,不然过段时间后会出现fragment重叠问题
        super.onSaveInstanceState(outState);
    }

    private void home() {
        //实现Home键效果
        Intent i= new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    //检查当前系统是否已开启暗黑模式
    public static boolean getDarkModeStatus(Context context) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }
}
