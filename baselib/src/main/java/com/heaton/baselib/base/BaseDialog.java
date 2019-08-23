package com.heaton.baselib.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * 对话框基类
 * Created by jerry on 2018/8/17.
 */

public abstract class BaseDialog extends Dialog {

    public Activity mActivity;
    private AlertDialog mConnectDialog;

    public BaseDialog(@NonNull Context context) {
        super(context);
        this.mActivity = (Activity) context;
    }

    public BaseDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setOwnerActivity((Activity) context);
        this.mActivity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        View view = View.inflate(mActivity, getLayoutResource(), null);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        ButterKnife.bind(this, view);
        onInitView();
        onInitData();
        initLinsenter();

    }

    protected abstract int getLayoutResource();

    protected abstract void onInitView();

    protected abstract void onInitData();

    protected  void initLinsenter(){};


   /* //显示连接设备对话框
    public void showProgressDialog(String msg) {
        if (mConnectDialog == null) {
            mConnectDialog = new AlertDialog.Builder(mActivity).setCancelable(false).create();
            mConnectDialog.setCanceledOnTouchOutside(false);
            mConnectDialog.show();
            mConnectDialog.setContentView(R.layout.progress_dialog);
            TextView message = (TextView) mConnectDialog.findViewById(R.id.message);
            message.setText(msg);
        }
        mConnectDialog.show();
    }*/

   /* //隐藏对话框
    public void dismissDialog(){
        if (mConnectDialog != null){
            mConnectDialog.dismiss();
        }
    }*/

    public void toast(int resid){
        Toast.makeText(mActivity, resid, Toast.LENGTH_SHORT).show();
    }
}
