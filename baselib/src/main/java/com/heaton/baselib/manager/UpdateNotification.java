package com.heaton.baselib.manager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.heaton.baselib.FileProvider7;
import com.heaton.baselib.utils.AppUtils;

import java.io.File;

/**
 * author: jerry
 * date: 20-4-28
 * email: superliu0911@gmail.com
 * des:
 */
public class UpdateNotification {

    private static final String TAG = "UpdateNotification";
    private Context mContext;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private NotificationCompat.Builder mBuilder;
    private int mNotificationId = 0x1234;
    private int mCurrentProgress = 0;

    //初始化通知
    UpdateNotification(Context context, Class<?>piClass, int largeIcon, int smallIcon) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = AppUtils.getAppName(context);
            channelId = appName+"_channelId";
            NotificationChannel mChannel = new NotificationChannel(channelId, appName, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            mNotificationManager.createNotificationChannel(mChannel);
        }
        Intent intent = new Intent(context, piClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(context, channelId);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setContentTitle("更新中...")  //标题
                .setWhen(System.currentTimeMillis())    //系统显示时间
                .setContentText("下载进度: 0%")
                .setProgress(100, 0, false)
                .setSmallIcon(smallIcon)     //收到信息后状态栏显示的小图标
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setContentIntent(pendingIntent)   //绑定PendingIntent对象
                .setAutoCancel(true)//设置通知被点击一次是否自动取消
                .setContentIntent(pendingIntent);
        mNotification = mBuilder.build();
    }

    /**
     * 更新下载进度
     *
     */
    public void setProgress(int progress) {
        if (mBuilder == null || mNotificationManager == null) {
            return;
        }
        if (mCurrentProgress == progress){
            return;
        }
        mCurrentProgress = progress;
        mBuilder.setProgress(100, progress, false);
        mBuilder.setContentText(progress + "%");
        mNotification = mBuilder.build();
        mNotificationManager.notify(mNotificationId, mNotification);
    }

    public void setContentText(String contentText){
        if (mBuilder == null || mNotificationManager == null) {
            return;
        }
        mBuilder.setContentTitle(contentText);
        mNotification = mBuilder.build();
        mNotificationManager.notify(mNotificationId, mNotification);
    }

    public void cancelNotification() {
        if (mNotificationManager == null) {
            return;
        }
        mNotificationManager.cancel(mNotificationId);
    }

    public void install(File file) {
        if (mBuilder == null || mNotificationManager == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.android.package-archive", file, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        mBuilder.setContentTitle("更新成功,点击安装");
        mBuilder.setContentIntent(pendingIntent);
        mNotification = mBuilder.build();
        mNotificationManager.notify(mNotificationId, mNotification);

    }
}