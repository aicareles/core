package com.heaton.baselib.permission;

import android.support.v4.app.FragmentActivity;

/**
 * author: jerry
 * date: 20-7-18
 * email: superliu0911@gmail.com
 * des:
 */
public class PermissionCompat {
    public static void requestPermissions(FragmentActivity activity, String[] permission, String rationale, IPermission listener) {
        if (activity == null) {
            return;
        }
        if (PermissionUtils.hasSelfPermissions(activity, permission)) {
            listener.permissionGranted();
            return;
        }
        PermissionFragment fragment = PermissionFragment.newInstance();
        fragment.requestPermission(activity, permission, rationale, listener);
    }
}
