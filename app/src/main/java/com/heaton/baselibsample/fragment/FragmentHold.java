package com.heaton.baselibsample.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.heaton.baselib.utils.FragmentUtils;
import com.heaton.baselibsample.R;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class FragmentHold {

    public static void showFragment(FragmentManager manager, Fragment fragment){
        new FragmentUtils.Builder()
                .setFragmentManager(manager)
                .setFragment(fragment)
                .setContainerViewId(R.id.fl_content)
                .setTag(fragment.getClass().getSimpleName())
                .show();
    }
}
