package com.heaton.baselib.base;

import android.os.Bundle;

import com.heaton.baselib.app.Navigation;

public abstract class BaseNavActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Navigation.get().init(this, containerViewId(), addToBackStack(), mode());
        super.onCreate(savedInstanceState);
    }

    protected abstract int containerViewId();

    protected Navigation.MODE mode(){
        return Navigation.MODE.REPLACE;
    }

    protected boolean addToBackStack(){
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!Navigation.get().pop()){
            finish();
        }
    }
}
