package com.heaton.baselib.base;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.heaton.baselib.R;
import com.heaton.baselib.app.language.LanguageManager;
import com.heaton.baselib.callback.ActivityResultCallback;
import com.heaton.baselib.permission.IPermission;
import com.heaton.baselib.permission.PermissionCompat;
import com.heaton.baselib.utils.GlobalStatusBarUtil;
import com.heaton.baselib.utils.HandlerUtils;
import com.heaton.baselib.utils.ToastUtil;

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        setHideBottomUIMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbind.unbind();
    }

    public void toActivity(@NonNull Class cl) {
        startActivity(new Intent(this, cl));
    }

    public void toActivity(@NonNull Class cl, Bundle bundle) {
        Intent intent = new Intent(this, cl);
        intent.putExtra(cl.getSimpleName(), bundle);
        startActivity(intent);
    }

    public IntentParams toActivityParams(Class cl){
        Intent intent = new Intent(this, cl);
        return new IntentParams(this, intent);
    }

    public void toActivityForResult(Class cl, ActivityResultCallback callback){
        ActivityResult result = new ActivityResult(this);
        result.start(cl, callback);
    }

    public void toActivityForResult(Intent intent, ActivityResultCallback callback){
        ActivityResult result = new ActivityResult(this);
        result.start(intent, callback);
    }

    public View inflate(int layoutIds) {
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
        ToastUtil.show(getBaseContext(), resid);
    }

    public void toast(String msg){
        ToastUtil.show(msg);
    }

    public void setTimeout(long delay, Runnable runnable){
        HandlerUtils.setTimeout(0, delay, runnable);
    }

    public void removeTimeout(){
        HandlerUtils.removeTimeout(0);
    }

    public Activity getActivity(){
        return BaseActivity.this;
    }

    public void requestPermission(String[] permissions, String rationale, IPermission iPermission){
        PermissionCompat.requestPermissions(this, permissions, rationale, iPermission);
    }

}
