package com.heaton.baselib.manager;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.heaton.baselib.BaseLibApi;

/**
 * description $desc$
 * created by jerry on 2019/8/6.
 */
public class PhoneStateManager {

    private static PhoneStateManager manager;

    private PhoneStateListener phoneStateListener;

    private PhoneStateManager(){
        TelephonyManager teleMgr = (TelephonyManager) BaseLibApi.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        teleMgr.listen(listener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
    }

    private android.telephony.PhoneStateListener listener = new android.telephony.PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    if (phoneStateListener != null){
                        phoneStateListener.callIdle();
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    if (phoneStateListener != null){
                        phoneStateListener.callRinging();
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (phoneStateListener != null){
                        phoneStateListener.callOffHook();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public static PhoneStateManager getInstance() {
        if (manager == null){
            manager = new PhoneStateManager();
        }
        return manager;
    }

    public void setPhoneStateListener(PhoneStateListener phoneStateListener){
        this.phoneStateListener = phoneStateListener;
    }


    public interface PhoneStateListener{
        void callIdle();//空闲
        void callRinging();//响铃中
        void callOffHook();//电话中
    }
}
