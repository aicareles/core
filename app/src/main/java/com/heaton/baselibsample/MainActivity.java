package com.heaton.baselibsample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.heaton.baselib.utils.BusUtils;
import com.heaton.baselib.utils.HandleBackUtil;
import com.heaton.baselibsample.bean.User;
import com.heaton.baselibsample.fragment.FragmentHold;
import com.heaton.baselibsample.fragment.HomeFragment;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BusUtils.Bus(tag = "main")
    public void noParamFun(User user) {//要安装插件
        Log.e(TAG, "noParamFun: "+user.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentHold.showFragment(getSupportFragmentManager(), HomeFragment.newInstance());

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //将这一行注释掉，阻止activity保存fragment的状态,不然过段时间后会出现fragment重叠问题
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
//            super.onBackPressed();
            home();
        }
    }

    private void home() {
        //实现Home键效果
        Intent i= new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusUtils.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusUtils.unregister(this);
    }
}
