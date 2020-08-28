package com.heaton.baselib.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.heaton.baselib.R;
import java.util.ArrayList;
import java.util.List;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PermissionFragment extends DialogFragment {
    private static IPermission permissionListener;
    private String[] permissions;
    private static final String PERMISSION_KEY = "permission_key";
    private static final String REQUEST_CODE = "request_code";
    private static final String REQUEST_RATIONALE = "request_rationale";
    private int requestCode = 1;
    private String rationale;
    private FragmentActivity activity;

    public static PermissionFragment newInstance() {
        return new PermissionFragment();
    }

    /**
     * 跳转到Activity申请权限
     *
     * @param activity     FragmentActivity
     * @param permissions Permission List
     * @param iPermission Interface
     */
    public void requestPermission(FragmentActivity activity, String[] permissions, String rationale, IPermission iPermission) {
        permissionListener = iPermission;
        /**弄一个看不见的Fragment，来处理回调*/
        show(activity.getSupportFragmentManager(), "");
        this.activity = activity;
        this.permissions = permissions;
        this.rationale = rationale;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setDimAmount(0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionInner(permissions);
    }

    /**
     * 申请权限
     *
     * @param permissions permission list
     */
    private void requestPermissionInner(String[] permissions) {
        if (PermissionUtils.hasSelfPermissions(activity, permissions)) {
            //all permissions granted
            if (permissionListener != null) {
                permissionListener.permissionGranted();
                permissionListener = null;
            }
            /**移除Fragment*/
            dismissAllowingStateLoss();
        } else {
            boolean shouldShowRequestPermissionRationale = PermissionUtils
                    .shouldShowRequestPermissionRationale(activity, permissions);
            if (TextUtils.isEmpty(rationale)){
                //request permissions
                requestPermissions(permissions, requestCode);
            }else {
                if (shouldShowRequestPermissionRationale){
                    showRequestPermissionRationale(activity, permissions,rationale);
                }else {
                    //request permissions
                    requestPermissions(permissions, requestCode);
                }
            }
        }
    }

    public void showRequestPermissionRationale(final Activity activity, final String[] permissions, String rationale){
        new AlertDialog.Builder(activity)
                .setMessage(rationale)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(activity, permissions, requestCode);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionUtils.verifyPermissions(grantResults)) {
            //所有权限都同意
            if (permissionListener != null) {
                permissionListener.permissionGranted();
            }
        } else {
            if (!PermissionUtils.shouldShowRequestPermissionRationale(activity, permissions)) {
                //权限被拒绝并且选中不再提示
                if (permissions.length != grantResults.length) return;
                List<String> denyNoAskList = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        denyNoAskList.add(permissions[i]);
                    }
                }
                if (permissionListener != null) {
                    permissionListener.permissionNoAskDenied(requestCode, denyNoAskList);
                }
            } else {
                //权限被取消
                if (permissionListener != null) {
                    List<String> denyList = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            denyList.add(permissions[i]);
                        }
                    }
                    permissionListener.permissionDenied(requestCode, denyList);
                }
            }

        }
        permissionListener = null;
        /**移除Fragment*/
        dismissAllowingStateLoss();
    }
}
