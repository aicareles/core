package com.heaton.baselibsample.fragment;

import android.os.Bundle;
import android.util.Log;

import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.activity.LanguageActivity;

import butterknife.OnClick;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class SettingFragment extends BaseFragment {
    private static final String TAG = "SettingFragment";

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void bindData() {

    }

    @OnClick(R.id.tv_language)
    public void onClickView(){
        toActivity(LanguageActivity.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }
}
