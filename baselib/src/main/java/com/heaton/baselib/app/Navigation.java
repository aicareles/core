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
    private List<Fragment> fragments = new ArrayList<>();
    public MODE mode = MODE.REPLACE;
    public boolean addToBackStack;
    public FragmentManager fragmentManager;
    public int containerViewId;

    public void init(FragmentActivity activity, int containerViewId){
        init(activity, containerViewId, true, MODE.REPLACE);
    }

    public void init(FragmentActivity activity, int containerViewId, boolean addToBackStack, MODE mode){
        this.fragmentManager = activity.getSupportFragmentManager();
        this.containerViewId = containerViewId;
        this.addToBackStack = addToBackStack;
        this.mode = mode;
    }

    public static Navigation of() {
        if (navigation == null){
            navigation = new Navigation();
        }
        return navigation;
    }

    public void navigate(Fragment fragment){
        if (mode == MODE.REPLACE){
            replace(fragment);
        }else {
            show(fragment);
        }
    }

    //栈中是否还有fragment
    public boolean pop(){
        if (fragmentManager.getBackStackEntryCount()>1){
            Log.e(TAG, "pop: "+fragmentManager.getBackStackEntryCount());
            fragmentManager.popBackStack();
            return true;
        }
        return false;
    }

    void show(Fragment fragment){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        for (Fragment f: fragments) {
            transaction.hide(f);
        }
        String tag = fragment.getClass().getSimpleName();
        Fragment target = fragmentManager.findFragmentByTag(tag);
        if (target == null){
            target = fragment;
            transaction.add(containerViewId, target, tag);
            fragments.add(target);
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

    void replace(Fragment fragment){
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerViewId, fragment);
        if (addToBackStack){
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
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
        SHOW, REPLACE
    }
}
