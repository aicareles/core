package com.heaton.baselib.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.heaton.baselib.callback.ActivityResultCallback;

import java.util.TreeMap;

/**
 * author: jerry
 * date: 20-9-10
 * email: superliu0911@gmail.com
 * des:
 */
public class ActivityResultFragment extends Fragment {
    private static final TreeMap<Integer, ActivityResultCallback> LISTENER_MAP = new TreeMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void startActivity(Intent intent, ActivityResultCallback callback) {
        int requestCode = getRequestCode();
        LISTENER_MAP.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    private int getRequestCode() {
        int requestCode;
        if (LISTENER_MAP.isEmpty()) {
            requestCode = 1;
        } else {
            requestCode = LISTENER_MAP.lastKey() + 1;
        }
        return requestCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResultCallback callback = LISTENER_MAP.remove(requestCode);
        if (callback != null) {
            callback.onActivityResult(resultCode, data);
        }
    }
}
