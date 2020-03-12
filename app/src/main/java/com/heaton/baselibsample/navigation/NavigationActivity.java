package com.heaton.baselibsample.navigation;

import com.heaton.baselib.app.Navigation;
import com.heaton.baselib.base.BaseNavActivity;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.fragment.Fragment1;

public class NavigationActivity extends BaseNavActivity {

    @Override
    protected boolean addToBackStack() {
        return true;
    }

    @Override
    protected Navigation.MODE mode() {
        return Navigation.MODE.REPLACE;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_navigation;
    }

    @Override
    protected void bindData() {
        Navigation.get().navigate(Fragment1.newInstance());
    }

    @Override
    protected int containerViewId() {
        return R.id.navigation_container;
    }

}
