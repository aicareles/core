package com.heaton.baselibsample;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.heaton.baselib.base.BaseActivity;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselib.widget.NavigationBar;
import com.heaton.baselibsample.fragment.HomeFragment;
import com.heaton.baselibsample.fragment.MusicFragment;
import com.heaton.baselibsample.fragment.SettingFragment;

import butterknife.BindView;


public class MainActivity extends BaseActivity {

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getDarkModeStatus(this)) {
            setTheme(R.style.main_theme_dark);
        }else {
            setTheme(R.style.main_theme_light);
            LogUtils.logi("MainActivity>>>[onCreate]: "+getDarkModeStatus(this));
        }
        super.onCreate(savedInstanceState);
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

    @Override
    public void onBackPressed() {
        /*if (!HandleBackUtil.handleBackPress(this)) {
//            super.onBackPressed();
            home();
        }*/
        home();
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
