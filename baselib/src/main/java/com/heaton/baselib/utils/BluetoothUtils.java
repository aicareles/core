package com.heaton.baselib.utils;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * description $desc$
 * created by jerry on 2019/4/10.
 */
public class BluetoothUtils {

    public static boolean isBleEnable() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter!=null){
            return adapter.isEnabled();
        }
        return false;
    }

    public static boolean isSupportAdvertiser(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)return false;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter!=null){
            return adapter.getBluetoothLeAdvertiser() != null;
        }
        return false;
    }
}
