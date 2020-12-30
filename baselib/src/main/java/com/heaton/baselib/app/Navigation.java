package com.heaton.baselib.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * description fragment导航
 * created by jerry on 2019/8/7.
 */
public class Navigation {
    private static final String TAG = "Navigation";
    private static Navigation navigation;
    public MODE mode, initMode = MODE.REPLACE;
    public boolean addToBackStack, initAddToBackStack = true;
    public FragmentManager fragmentManager;
    public int containerViewId;

    public void init(FragmentActivity activity, int containerViewId){
        init(activity, containerViewId, addToBackStack, MODE.REPLACE);
    }

    public void init(FragmentActivity activity, int containerViewId, boolean addToBackStack, MODE mode){
        this.fragmentManager = activity.getSupportFragmentManager();
        this.containerViewId = containerViewId;
        this.addToBackStack = initAddToBackStack = addToBackStack;
        this.mode = initMode = mode;
    }

    public static Navigation get() {
        if (navigation == null){
            navigation = new Navigation();
        }
        return navigation;
    }

    public void navigate(Fragment fragment){
        if (mode == MODE.REPLACE){
            replace(fragment);
        }else if (mode == MODE.ADD){
            add(fragment);
        } else {
            show(fragment);
        }
        reset();
    }

    //栈中是否还有fragment
    public boolean pop(){
        if (fragmentManager.getBackStackEntryCount()>1){
            Log.e(TAG, "pop: "+fragmentManager.getBackStackEntryCount());
            fragmentManager.popBackStack();
            return true;
        }
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; ++i) {
            fragmentManager.popBackStack();
        }
        return false;
    }

    void show(Fragment fragment){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        for (Fragment f: fragmentManager.getFragments()) {
            transaction.hide(f);
        }
        String tag = fragment.getClass().getSimpleName();
        Fragment target = fragmentManager.findFragmentByTag(tag);
        if (target == null){
            target = fragment;
            transaction.add(containerViewId, target, tag);
        }else {
            transaction.show(target);
        }
        transaction.commitAllowingStateLoss();
    }

    public void hideFragment(Fragment fragment){
        Fragment target = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (target != null){
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(target);
            transaction.commitAllowingStateLoss();
        }
    }

    void add(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerViewId, fragment);
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    void replace(Fragment fragment){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerViewId, fragment);
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    //重置初始配置的值(以免每次执行navigate都要设置模式等,默认为初始配置的模式)
    void reset(){
        addToBackStack = initAddToBackStack;
        mode = initMode;
    }

    public Navigation mode(MODE mode) {
        this.mode = mode;
        return this;
    }

    public Navigation addToBackStack(boolean addToBackStack) {
        this.addToBackStack = addToBackStack;
        return this;
    }

    public Navigation containerViewId(int containerViewId) {
        this.containerViewId = containerViewId;
        return this;
    }

    public enum MODE {
        SHOW, REPLACE, ADD
    }
}
