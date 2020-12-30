package com.heaton.baselib.base.recycleview.drag;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * author: jerry
 * date: 20-5-14
 * email: superliu0911@gmail.com
 * des:
 */
public class YolandaItemTouchHelper extends ItemTouchHelper {
    private Callback mCallback;
    public YolandaItemTouchHelper(Callback callback) {
        super(callback);
        this.mCallback = callback;
    }

    public Callback getCallback() {
        return mCallback;
    }
}
