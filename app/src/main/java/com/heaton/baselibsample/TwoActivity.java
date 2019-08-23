package com.heaton.baselibsample;

import android.os.Handler;
import android.support.v4.content.res.ComplexColorCompat;
import android.support.v4.os.HandlerCompat;
import android.util.Log;
import android.view.View;

import com.heaton.baselib.base.BaseActivity;

/**
 * description $desc$
 * created by jerry on 2019/8/16.
 */
public class TwoActivity extends BaseActivity {

    private static final String TAG = "TwoActivity";
    Handler handler;

    @Override
    protected int layoutId() {
        return R.layout.activity_two;
    }

    @Override
    protected void bindData() {
        handler = new Handler();
        HandlerCompat.postDelayed(handler, new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: >>>>>");
            }
        }, "test", 5000);

    }

    public void sendMsg(View view){
        handler.removeCallbacksAndMessages("test");
        /*User user = new User();
        user.setName("two-activity");
        user.setPassword("123456");
        BusUtils.post("main", user);*/
    }
}
