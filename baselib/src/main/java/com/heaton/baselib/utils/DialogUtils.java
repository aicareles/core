package com.heaton.baselib.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.heaton.baselib.R;

/**
 * description $desc$
 * created by jerry on 2019/7/11.
 */
public class DialogUtils {

    //显示对话框
    public static AlertDialog showProgressDialog(Activity activity, String tipContext) {
        AlertDialog progressDialog = new AlertDialog.Builder(activity).create();
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        TextView message = progressDialog.findViewById(R.id.message);
        message.setText(tipContext);
        progressDialog.show();
        return progressDialog;
    }

    /**
     * 底部弹出式
     */
    public static Dialog showBottomDialog(Activity context, int layout) {
        final Dialog dialog = new Dialog(context, R.style.BottomDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(layout, null);
        dialog.setContentView(contentView);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        int width = ScreenUtil.getScreenWidth(context);
        lp.width = width;
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.BottomDialogAnimation); // 添加动画
        dialog.show();
        return dialog;
    }

    public static AlertDialog showCommonDialog(Activity activity, String title, String message, final DialogCall callBack) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onNegative();
                    }
                })
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onPositive(dialog);
                    }
                })
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showCustomDialog(Activity activity, String title, int layout, final DialogCall callBack) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(layout)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onNegative();
                    }
                })
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.onPositive(dialog);
                    }
                })
                .create();
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog showInputDialog(Activity activity, String title, final DialogCall callBack){
        return showCustomDialog(activity, title, R.layout.dialog_edit, callBack);
    }

    public abstract static class DialogCall {
        public abstract void onPositive(DialogInterface dialog);

        public void onNegative(){}
    }
}
