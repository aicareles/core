package com.heaton.baselibsample.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.heaton.baselib.app.Navigation;
import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselibsample.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class Fragment3 extends BaseFragment {
    @BindView(R.id.tvText)
    TextView tvText;

    public static Fragment3 newInstance() {
        Bundle args = new Bundle();
        Fragment3 fragment = new Fragment3();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_connect;
    }

    @Override
    protected void bindData() {
        tvText.setText("fragment3");
    }

    @OnClick(R.id.tvText)
    public void onClickView(){
        Navigation.get().navigate(Fragment4.newInstance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logi(TAG+"onDestroy");
    }
}
