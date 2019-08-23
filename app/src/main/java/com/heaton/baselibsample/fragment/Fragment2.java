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
public class Fragment2 extends BaseFragment {
    private static final String TAG = "Fragment1";
    @BindView(R.id.tvText)
    TextView tvText;

    public static Fragment2 newInstance() {
        Bundle args = new Bundle();
        Fragment2 fragment = new Fragment2();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.fragment_connect;
    }

    @Override
    protected void bindData() {
        tvText.setText("fragment2");
    }

    @Override
    protected void bindListener() {

    }

    @Override
    public boolean onBackPressed() {
        FragmentHold.showFragment(getFragmentManager(), HomeFragment.newInstance());
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: >>>>");
    }
}
