package com.heaton.baselibsample.setting;

import android.os.Bundle;

import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.fragment.FragmentHold;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class LanguageFragment extends BaseFragment {
    private static final String TAG = "SettingFragment";

    public static LanguageFragment newInstance() {
        Bundle args = new Bundle();
        LanguageFragment fragment = new LanguageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_language;
    }

    @Override
    protected void bindData() {

    }


    @Override
    public boolean onBackPressed() {
        FragmentHold.showFragment(getFragmentManager(), SettingFragment.newInstance());
        return true;
    }
}
