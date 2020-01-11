package com.heaton.baselib.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * description $desc$
 * created by jerry on 2019/6/18.
 */
public class NotificationUtil {

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotifiction(Context context, String title, String content, Class<?>piClass, int largeIcon, int smallIcon) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, piClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        //版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            String appName = AppUtils.getAppName(context);
            String id = appName+"_channelId";
            NotificationChannel mChannel = new NotificationChannel(id, appName, NotificationManager.IMPORTANCE_HIGH);
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id);
            builder.setContentTitle(title)  //标题
                    .setContentText(content)   //内容
//                    .setSubText("notice")     //内容下面的一小段文字
//                    .setTicker("notice")      //收到信息后状态栏显示的文字信息
                    .setWhen(System.currentTimeMillis())    //系统显示时间
                    .setSmallIcon(smallIcon)     //收到信息后状态栏显示的小图标
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)    //设置默认的三色灯与振动器
                    .setDefaults(Notification.DEFAULT_SOUND)    //设置系统的提示音
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);       //设置点击后取消Notification
            builder.setContentIntent(pendingIntent);    //绑定PendingIntent对象
            Notification notification = builder.build();
            manager.notify(1, notification);
        } else if (Build.VERSION.SDK_INT >= 23) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                    .setSmallIcon(smallIcon)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setTicker("notice")
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSound(sound)
                    .setOngoing(false)
                    .setWhen(System.currentTimeMillis());
            Notification notification = builder.build();
            manager.notify(1, notification);
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(false)
                    .setTicker("notice")
                    .setDefaults(0)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                    .setSmallIcon(smallIcon)
                    .setWhen(System.currentTimeMillis());
            Notification notification = builder.build();
            manager.notify(1, notification);
        }
    }

    public static void cancelNotifiction(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);
    }

}
