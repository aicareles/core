package com.heaton.baselib.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class FragmentUtils {
    private static final String TAG = "FragmentUtils";
    private static FragmentUtils fragmentUtils = new FragmentUtils();

    private List<Fragment> fragments = new ArrayList<>();

    public static FragmentUtils getInstance() {
        return fragmentUtils;
    }

    private FragmentUtils (){}

    public void showFragment(Builder builder){
        showFragmentByTag(builder.getFragmentManager(), builder.getFragment(), builder.getContainerViewId(), builder.getTag());
    }

    public void showFragmentByTag(FragmentManager manager, Fragment targetFragment, int containerViewId, String tag){
        // 开启一个Fragment事务
        FragmentTransaction transaction = manager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null){
            fragment = targetFragment;
            transaction.add(containerViewId, fragment, tag);
            fragments.add(fragment);
        }else {
            transaction.show(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    public void hideFragment(FragmentManager manager, Fragment fragment){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(fragment);
        transaction.commitAllowingStateLoss();
    }

    public void hideFragment(FragmentManager manager, String tag){
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment != null){
            hideFragment(manager, fragment);
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        for (Fragment fragment: fragments) {
            transaction.hide(fragment);
        }
    }

    public static class Builder {
        private Fragment fragment;
        private FragmentManager fragmentManager;
        private int containerViewId;
        private String tag;

        @NonNull
        public Fragment getFragment() {
            return fragment;
        }

        public Builder setFragment(Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        @NonNull
        public FragmentManager getFragmentManager() {
            return fragmentManager;
        }

        public Builder setFragmentManager(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
            return this;
        }

        @NonNull
        public int getContainerViewId() {
            return containerViewId;
        }

        public Builder setContainerViewId(int containerViewId) {
            this.containerViewId = containerViewId;
            return this;
        }

        @NonNull
        public String getTag() {
            return tag;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public void show(){
            FragmentUtils.getInstance().showFragment(this);
        }
    }
}
