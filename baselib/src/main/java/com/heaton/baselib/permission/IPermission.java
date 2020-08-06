package com.heaton.baselib.permission;

import java.util.List;

/**
 * description $desc$
 * created by jerry on 2019/8/5.
 */
public abstract class IPermission {
    //同意权限
    public abstract void permissionGranted();

    //拒绝权限并且选中不再提示
    public void permissionNoAskDenied(int requestCode, List<String> denyNoAskList){}

    //取消权限
    public void permissionDenied(int requestCode, List<String> denyList){}
}
