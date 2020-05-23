package com.heaton.baselibsample.mvp;

import android.view.View;

import com.heaton.baselib.base.mvp.BaseMvpActivity;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselibsample.R;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public class LoginActivity extends BaseMvpActivity<LoginPresenter> implements ILoginContract.ILoginView {

    @Override
    protected LoginPresenter createPresenter() {
        return new LoginPresenter();
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void bindData() {

    }

    @Override
    public void loginFail(String msg) {
        LogUtils.logi("LoginActivity>>>[loginFail]: "+msg);
    }

    @Override
    public void loginSuccess(LoginVO loginVO) {
        LogUtils.logi("LoginActivity>>>[loginSuccess]: "+loginVO.getUserName());
    }

    public void login(View view) {
        presenter.login("aicareles","123456");
    }

    public void update(View view) {
        presenter.update();
    }
}
