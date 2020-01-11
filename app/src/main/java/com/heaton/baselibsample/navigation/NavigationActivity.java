package com.heaton.baselibsample.navigation;

import com.heaton.baselib.base.BaseActivity;
import com.heaton.baselib.app.Navigation;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.fragment.Fragment1;

public class NavigationActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected void bindData() {
        Navigation.of().init(this, R.id.navigation_container);
        Navigation.of().navigate(Fragment1.newInstance());
    }

    @Override
    public void onBackPressed() {
        if (!Navigation.of().pop()){
            finish();
        }
    }
}
