package com.heaton.baselib.base.mvp;

import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public class BaseMvpPresenter<V extends MvpView> implements MvpPresenter<V>{

    private WeakReference<V> viewRef;

    @Override public void attachView(V view) {
        viewRef = new WeakReference<V>(view);
    }

    /**
     * Get the attached view. You should always call {@link #isViewAttached()} to check if the view
     * is
     * attached to avoid NullPointerExceptions.
     *
     * @return <code>null</code>, if view is not attached, otherwise the concrete view instance
     */
    @Nullable
    public V getView() {
        return viewRef == null ? null : viewRef.get();
    }

    /**
     * Checks if a view is attached to this presenter. You should always call this method before
     * calling {@link #getView()} to get the view instance.
     */
    public boolean isViewAttached() {
        return viewRef != null && viewRef.get() != null;
    }

    @Override public void detachView() {
        if (viewRef != null) {
            viewRef.clear();
            viewRef = null;
        }
    }

}
