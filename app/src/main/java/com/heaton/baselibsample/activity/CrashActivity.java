package com.heaton.baselibsample.activity;

import android.view.View;

import com.heaton.baselib.base.BaseActivity;
import com.heaton.baselibsample.R;

/**
 * author: jerry
 * date: 20-4-30
 * email: superliu0911@gmail.com
 * des:
 */
public class CrashActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_crash;
    }

    @Override
    protected void bindData() {

    }

    public void exit(View view) {
        System.exit(0);
    }
}
