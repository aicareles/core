package com.heaton.baselib.base;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.heaton.baselib.callback.ActivityResultCallback;

import java.util.Objects;

/**
 * author: jerry
 * date: 20-9-10
 * email: superliu0911@gmail.com
 * des:
 */
public class ActivityResult {
    private static final String TAG = ActivityResult.class.getSimpleName();
    private ActivityResultFragment mActivityResultFragment;

    public ActivityResult(@NonNull Fragment fragment) {
        this(Objects.requireNonNull(fragment.getActivity()));
    }

    public ActivityResult(@NonNull FragmentActivity activity) {
        mActivityResultFragment = getBestActivityResultFragment(activity.getSupportFragmentManager());
    }

    private ActivityResultFragment getBestActivityResultFragment(FragmentManager fragmentManager) {
        ActivityResultFragment activityResultFragment = findActResultFragment(fragmentManager);
        boolean isNewInstance = activityResultFragment == null;
        if (isNewInstance) {
            activityResultFragment = new ActivityResultFragment();
            fragmentManager
                    .beginTransaction()
                    .add(activityResultFragment, TAG)
                    .commitNow();
        }
        return activityResultFragment;
    }

    private ActivityResultFragment findActResultFragment(FragmentManager fragmentManager) {
        return (ActivityResultFragment) fragmentManager.findFragmentByTag(TAG);
    }

    public void start(Intent intent, ActivityResultCallback callback) {
        mActivityResultFragment.startActivity(intent, callback);
    }

    public void start(Class<?> clazz, ActivityResultCallback callback) {
        Intent intent = new Intent(mActivityResultFragment.getActivity(), clazz);
        start(intent, callback);
    }
}
