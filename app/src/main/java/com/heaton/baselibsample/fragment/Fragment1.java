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
public class Fragment1 extends BaseFragment {
    @BindView(R.id.tvText)
    TextView tvText;

    public static Fragment1 newInstance() {
        Bundle args = new Bundle();
        Fragment1 fragment = new Fragment1();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_connect;
    }

    @Override
    protected void bindData() {
        tvText.setText("fragment1");
    }

    @OnClick(R.id.tvText)
    public void onClickView(){
        //fragment2不加入回退栈(直接替换fragment1)----->此时fragment2为activity的最底部的fragment
//        Navigation.get().addToBackStack(false).navigate(Fragment2.newInstance());
        Navigation.get().navigate(Fragment2.newInstance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logi(TAG+"onDestroy");
    }

}
