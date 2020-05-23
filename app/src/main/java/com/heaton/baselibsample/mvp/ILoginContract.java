package com.heaton.baselibsample.mvp;

import com.heaton.baselib.base.mvp.MvpView;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public interface ILoginContract {

    interface ILoginView extends MvpView {
        void loginFail(String msg);
        void loginSuccess(LoginVO loginVO);
    }

    interface ILoginPresenter {
        void login(String userName, String pwd);
        void register(String userName, String pwd);
        void update();
    }
}
