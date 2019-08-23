package com.heaton.baselib.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class CopyUtil {
    public static void copy(Context context,String text){
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }
}
