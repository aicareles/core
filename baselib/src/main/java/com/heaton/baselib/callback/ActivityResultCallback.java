package com.heaton.baselib.callback;

import android.content.Intent;

/**
 * author: jerry
 * date: 20-9-10
 * email: superliu0911@gmail.com
 * des:
 */
public interface ActivityResultCallback {
    void onActivityResult(int resultCode, Intent data);
}
