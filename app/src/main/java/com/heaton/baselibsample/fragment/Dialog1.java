package com.heaton.baselibsample.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselibsample.R;

/**
 * author: jerry
 * date: 20-9-9
 * email: superliu0911@gmail.com
 * des:
 */
public class Dialog1 extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(R.layout.dialog_edit).create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.logi("Dialog1>>>[onDestroy]: ");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.logi("Dialog1>>>[onViewCreated]: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.logi("Dialog1>>>[onCreate]: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.logi("Dialog1>>>[onStart]: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.logi("Dialog1>>>[onStop]: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.logi("Dialog1>>>[onPause]: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.logi("Dialog1>>>[onResume]: ");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.logi("Dialog1>>>[onCreateView]: ");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.logi("Dialog1>>>[onAttach]: ");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        LogUtils.logi("Dialog1>>>[onAttachFragment]: ");
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        LogUtils.logi("Dialog1>>>[onCancel]: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.logi("Dialog1>>>[onDestroyView]: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.logi("Dialog1>>>[onDetach]: ");
    }
}
