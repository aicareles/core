package com.heaton.baselibsample.mvp;

import android.content.Context;

import com.heaton.baselib.api.Api;
import com.heaton.baselib.api.BaseResponse;
import com.heaton.baselib.api.BaseObserver;
import com.heaton.baselib.api.RxSchedulers;
import com.heaton.baselib.base.mvp.BaseMvpPresenter;
import com.heaton.baselib.bean.UpdateVO;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselibsample.api.MyApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public class LoginPresenter extends BaseMvpPresenter<ILoginContract.ILoginView> implements ILoginContract.ILoginPresenter {

    private MyApiService apiService = Api.apiService(MyApiService.class);

    @Override
    public void login(String userName, String pwd) {
        boolean nextBoolean = new Random().nextBoolean();
        if (nextBoolean){
            getView().loginSuccess(new LoginVO());
        }else {
            getView().loginFail("登录失败");
        }
    }

    @Override
    public void register(String userName, String pwd) {

    }

    @Override
    public void update() {
        Map<String, String> map = new HashMap<>();
        map.put("app_id","cn.com.heaton.icoasters");
        map.put("platform","Android");
        apiService.update(map)
                .compose(RxSchedulers.io2main())
                .subscribe(new BaseObserver<UpdateVO>() {
                    @Override
                    protected void onSuccess(BaseResponse<UpdateVO> response) {
                        LogUtils.logi("LoginPresenter>>>[onSuccess]: "+response);
                    }

                    @Override
                    protected void onFail(String msg) {
                        LogUtils.logi("LoginPresenter>>>[onFail]: "+msg);
                    }
                });
                /*.subscribe(new BaseObserver<UpdateVO>() {
                    @Override
                    protected void onRequestStart() {
                        LogUtils.logi("LoginPresenter>>>[onRequestStart]: ");
                        showProgressDialog((Context) getView(), "sss", "aaa");
                    }

                    @Override
                    protected void onRequestEnd() {
                        LogUtils.logi("LoginPresenter>>>[onRequestEnd]: ");
                        closeProgressDialog();
                    }

                    @Override
                    protected void onSuccess(UpdateVO updateVO){
                        LogUtils.logi("LoginPresenter>>>[onSuccess]: "+updateVO);
                    }

                    @Override
                    protected void onFail(String msg) {
                        LogUtils.logi("LoginPresenter>>>[onFail]: "+msg);
                    }
                });*/
    }
}
