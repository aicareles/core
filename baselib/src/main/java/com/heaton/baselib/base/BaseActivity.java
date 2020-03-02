package com.heaton.baselib.base;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.heaton.baselib.R;
import com.heaton.baselib.app.language.LanguageManager;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.GlobalStatusBarUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: jerry
 * Date: 2018/4/8
 * Time: 23:28
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    public Toolbar toolbar;
    public TextView abTitle;
    private AlertDialog mProgressDialog;
    private Unbinder unbind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullScreen();
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(layoutId());
        setHideBottomUIMenu();
        initNavagation();
        initToolBar();
        unbind = ButterKnife.bind(this);
        bindData();
        bindListener();
    }

    /**
     * 初始化导航栏(状态栏、底部虚拟键---暂时未做)
     */
    private void initNavagation() {
        if (isStatusbarTransparent()){
            GlobalStatusBarUtil.setUpStatusBar(this, android.R.color.transparent, false);
        }
        GlobalStatusBarUtil.setFitsSystemWindows(this, true);
    }

    private void setFullScreen(){
        if (isFullScreen()){
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
    }

    private void setHideBottomUIMenu(){
        if (isHideBottomUIMenu()){
            hideBottomUIMenu();
        }
    }

    /**
     * 是否状态栏全透明沉浸式
     */
    protected boolean isStatusbarTransparent(){
        return false;
    }

    /**
     * 是否竖屏
     * @return
     */
    protected boolean isPortrait(){
        return true;
    }

    /**
     * 是否全屏
     * @return
     */
    protected boolean isFullScreen(){
        return false;
    }

    /**
     * 是否隐藏虚拟键
     * @return
     */
    protected boolean isHideBottomUIMenu(){
        return false;
    }

    protected abstract int layoutId();

    protected abstract void bindData();

    protected void bindListener(){}

    /*@Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setHideBottomUIMenu();
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbind.unbind();
    }

    protected void toActivity(@NonNull Class cl) {
        startActivity(new Intent(this, cl));
    }

    protected void toActivity(@NonNull Class cl, Bundle bundle) {
        Intent intent = new Intent(this, cl);
        intent.putExtra(cl.getSimpleName(), bundle);
        startActivity(intent);
    }

    protected View inflate(int layoutIds) {
        return LayoutInflater.from(this).inflate(layoutIds, null);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageManager.attachBaseContext(newBase));
    }

    //自己新添加的
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (abTitle != null) {
            abTitle.setText(title);
        }
    }

    //自己新添加的
    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            abTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        }
        if (abTitle != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void setTranslucentStatus() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置横竖屏
        setRequestedOrientation(isPortrait()?ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        GlobalStatusBarUtil.setStatusColor(this, getStatusBarColorId());
        GlobalStatusBarUtil.setStatusBarDarkFont(this, isDarkFont());
    }


    /**
     * 可以重写状态栏的颜色
     *
     * @return
     */
    protected int getStatusBarColorId() {
        return ContextCompat.getColor(this, android.R.color.black);
    }

    /**
     * 是否设置状态栏字体黑色
     * @return
     */
    protected boolean isDarkFont() {
        return false;
    }

    /**
     * hide inputMethod
     */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            View localView = getCurrentFocus();
            if (localView != null && localView.getWindowToken() != null) {
                IBinder windowToken = localView.getWindowToken();
                inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
            }
        }
    }

    /**
     * show inputMethod
     */
    public void showSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(null != imm) {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            imm.showSoftInput(v, 0);
        }
    }

    //显示对话框
    public void showProgressDialog(String tipContext) {
        if (mProgressDialog == null) {
            mProgressDialog = new AlertDialog.Builder(this).setCancelable(false).create();
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_dialog);
        }
        TextView message = mProgressDialog.findViewById(R.id.message);
        message.setText(tipContext);
        mProgressDialog.show();
    }

    //隐藏对话框
    public void dismissProgressDialog(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    public void toast(int resid){
        Toast.makeText(this, resid, Toast.LENGTH_SHORT).show();
    }

    public void toast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /*---------------------------------------------------------------------------以下是android6.0动态授权的封装十分好用---------------------------------------------------------------------------*/
    private int                   mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions   = new SparseArray<>();//请求权限运行列表

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runnable.mGranted = true;
        }
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if(runnable == null){
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
			是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
			是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissionTemp, requestCode);
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GrantedResult runnable = mPermissions.get(requestCode);
                            if (runnable == null) {
                                return;
                            }
                            runnable.mGranted = false;
                            runOnUiThread(runnable);
                        }
                    }).create();
            dialog.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode);
            }
        }
    }

    public static abstract class GrantedResult implements Runnable{
        private boolean mGranted;
        public abstract void onResult(boolean granted);
        @Override
        public void run(){
            onResult(mGranted);
        }
    }


    public Activity getActivity(){
        return BaseActivity.this;
    }

}
