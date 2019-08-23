//package com.heaton.baselib.utils;
//
//import com.qw.soul.permission.SoulPermission;
//import com.qw.soul.permission.bean.Permission;
//import com.qw.soul.permission.bean.Permissions;
//import com.qw.soul.permission.bean.Special;
//import com.qw.soul.permission.callbcak.CheckRequestPermissionListener;
//import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;
//import com.qw.soul.permission.callbcak.GoAppDetailCallBack;
//import com.qw.soul.permission.callbcak.SpecialPermissionListener;
//
///**
// * description $desc$
// * created by jerry on 2019/8/3.
// */
//public class PermissionUtils {
//    /**
//     * 请求单个权限
//     * @param permission
//     * @param checkRequestPermissionListener
//     */
//    public static void requestPermisson(String permission, CheckRequestPermissionListener checkRequestPermissionListener){
//        SoulPermission.getInstance().checkAndRequestPermission(permission, checkRequestPermissionListener);
//    }
//
//    /**
//     * 请求多个权限
//     * @param permissions
//     * @param checkRequestPermissionsListener
//     */
//    public static void requestPermissons(String[] permissions, CheckRequestPermissionsListener checkRequestPermissionsListener){
//        SoulPermission.getInstance().checkAndRequestPermissions(
//                Permissions.build(permissions), checkRequestPermissionsListener);
//    }
//
//    /**
//     * 检查某项权限
//     * @param permission
//     * @return
//     */
//    public static Permission checkSinglePermission(String permission){
//        return SoulPermission.getInstance().checkSinglePermission(permission);
//    }
//
//    /**
//     * 检查特殊权限[通知权限]
//     * @return
//     */
//    public static boolean checkSpecialPermission(Special special){
//        return SoulPermission.getInstance().checkSpecialPermission(special);
//    }
//
//    /**
//     * 检查并请求特殊权限[未知应用安装]
//     */
//    public static void checkAndRequestPermission(Special special, SpecialPermissionListener specialPermissionListener){
//        SoulPermission.getInstance().checkAndRequestPermission(special, specialPermissionListener);
//    }
//
//    /**
//     * 跳转到应用设置页
//     */
//    public static void goApplicationSettings(GoAppDetailCallBack callBack){
//        SoulPermission.getInstance().goApplicationSettings(callBack);
//    }
//
//    /**
//     * 设置跳过老的权限系统（老的系统默认权限直接授予）
//     */
//    public static void skipOldRom(boolean isSkip){
//        SoulPermission.skipOldRom(isSkip);
//    }
//
//    /**
//     * 设置debug模式(看日志打印)
//     */
//    public static void setDebug(boolean isDebug){
//        SoulPermission.setDebug(isDebug);
//    }
//
//}
