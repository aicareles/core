package com.heaton.baselib.base.recycleview.drag;

/**
 * author: jerry
 * date: 20-5-14
 * email: superliu0911@gmail.com
 * des:
 */
public interface OnItemCallbackListener {
    /**
     * @param fromPosition 起始位置
     * @param toPosition 移动的位置
     */
    void onMove(int fromPosition, int toPosition);
    void onSwipe(int position);
}
