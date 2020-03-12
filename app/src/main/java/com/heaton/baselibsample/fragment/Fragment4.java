package com.heaton.baselibsample.fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselibsample.R;

import butterknife.BindView;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class Fragment4 extends BaseFragment {
    private static final String TAG = "Fragment4";
    @BindView(R.id.tvText)
    TextView tvText;

    public static Fragment4 newInstance() {
        Bundle args = new Bundle();
        Fragment4 fragment = new Fragment4();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_connect;
    }

    @Override
    protected void bindData() {
        tvText.setText("fragment4");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: >>>>");
    }
}
