package com.heaton.baselib.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.heaton.baselib.BuildConfig;
import com.heaton.baselib.Constance;
import com.heaton.baselib.FileProvider7;
import com.heaton.baselib.LogInterceptor;
import com.heaton.baselib.R;
import com.heaton.baselib.bean.UpdateVO;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.FileUtils;
import com.heaton.baselib.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 应用更新
 */
public class UpdateManager {

    private static final String TAG = "UpdateManager";
    public interface DownloadListener {
        void onPreDownload(String url);

        void onDownloading(int progress);

        void onDownloadComplete();

        void onDownloadFailed();

        void onInstall(File file);
    }

    private Activity mContext;
    private AlertDialog mDownloadDialog;
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_FAIL = 3;
    private static final int DOWN_BEFORE = 4;
    private boolean interceptFlag = false;
    private DownloadListener mDownloadListener;
    private boolean mApk = true;
    private TextView mTvTotal;
    private float mSize;
    private AlertDialog mDialog;
    private boolean mShowDialog;
    private Builder mBuilder;
    private UpdateNotification updateNotification;

    public static class Builder {
        private int iconSmall;
        private int iconLarge;
        private boolean supportGoogle;//google平台是否支持更新(支持则弹框跳转到google play,否则不提示)
        private boolean isForceUpdate;//是否强制更新
        private boolean isNotification;//是否有通知栏

        public Builder(){}

        public Builder iconSmall(int val) {
            iconSmall = val;
            return this;
        }

        public Builder iconLarge(int val) {
            iconLarge = val;
            return this;
        }

        public Builder supportGoogle(boolean supportGoogle) {
            this.supportGoogle = supportGoogle;
            return this;
        }

        public Builder isForceUpdate(boolean val) {
            isForceUpdate = val;
            return this;
        }

        public Builder isNotification(boolean notification) {
            isNotification = notification;
            return this;
        }

        public UpdateManager build(Activity context) {
            return new UpdateManager(context, this);
        }
    }


    private MessageHandler mHandler = new MessageHandler();

    @SuppressLint("HandlerLeak")
    private class MessageHandler extends Handler {

        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case DOWN_BEFORE:
                    if (mDownloadListener != null) {
                        mDownloadListener.onPreDownload((String) msg.obj);
                    }
                    break;
                case DOWN_UPDATE:
                    if (mProgress != null) {
                        mProgress.setProgress(msg.arg1);
                    }
                    if (mDownloadListener != null) {
                        mDownloadListener.onDownloading(msg.arg1);
                    }
                    if (updateNotification != null){
                        updateNotification.setProgress(msg.arg1);
                    }
                    break;
                case DOWN_OVER:
                    if (mDownloadListener != null) {
                        mDownloadListener.onDownloadComplete();
                    }
                    if (updateNotification != null){
                        updateNotification.install((File) msg.obj);
                    }
                    install((File) msg.obj);
                    break;
                case DOWN_FAIL:
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.down_fail);
                        final String url = (String) msg.obj;
                        final float size = mSize;
                        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                downloadFile(url, size, mShowDialog);
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                    if (mDownloadListener != null) {
                        mDownloadListener.onDownloadFailed();
                    }
                    if (updateNotification != null){
                        updateNotification.setContentText("更新失败");
                    }
                    break;
                default:
                    super.dispatchMessage(msg);
            }
        }
    }

    public UpdateManager(Activity activity, Builder builder) {
        this.mContext = activity;
        this.mBuilder = builder;
        if (builder.isNotification){
            updateNotification = new UpdateNotification(mContext, mContext.getClass(), mBuilder.iconLarge, mBuilder.iconSmall);
        }
    }

    public UpdateManager(Activity activity) {
        this(activity, new Builder());
    }

    /**
     * 检查更新
     */
    public void versionUpdate(){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new LogInterceptor()).build();//创建OkHttpClient对象。
        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
        formBody.add("app_id", AppUtils.getPackageName(mContext));
        formBody.add("platform", Constance.APP.PLATFORM);
        Request request = new Request.Builder()//创建Request 对象。
                .url(Constance.API.BASE_URL+Constance.API.APP_LAST_UPDATE)
                .post(formBody.build())//传递请求体
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject dataObject = JSON.parseObject(json);
                    if (dataObject.getInteger("status") == 0){
                        String data = dataObject.getString("data");
                        if (!TextUtils.isEmpty(data)) {
                            final UpdateVO updateVO = JSONObject.parseObject(data, UpdateVO.class);
                            if (updateVO != null) {
                                int version = AppUtils.getVersionCode(mContext);
                                if (!TextUtils.isEmpty(updateVO.app_url) && version >= 0 && updateVO.app_version_number > version) {
                                    LogUtils.logi("onResponse: 检测到新版本");
                                    final String text = mContext.getResources().getString(R.string.find_new_version) + ":" + updateVO.app_version + "\n\n" + mContext.getResources().getString(R.string.size) + ":" + ((int) (updateVO.app_size / 1024 * 100)) / 100 + "MB\n\n" + updateVO.app_update;
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showNoticeDialog(text, updateVO.app_url, updateVO.app_size);
                                        }
                                    });
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*private void googleVersionUpdate(){
        ThreadUtils.async(new CallBack() {
            @Override
            public void execute() {
                Document document = null;
                try {
                    document = Jsoup.connect("https://play.google.com/store/apps/details?id=" + mContext.getPackageName() + "&hl=en")
                            .timeout(30000)
                            .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                            .referrer("http://www.google.com")
                            .get();
                    if (document != null) {
                        Elements element = document.getElementsContainingOwnText("Current Version");
                        for (Element ele : element) {
                            if (ele.siblingElements() != null) {
                                Elements sibElemets = ele.siblingElements();
                                for (Element sibElemet : sibElemets) {
                                    String newVersion = sibElemet.text();
                                    Log.e(TAG, "execute: 当前版本："+newVersion);
                                    if (Double.parseDouble(BuildConfig.VERSION_NAME) < Double.parseDouble(newVersion)) {
                                        //perform your task here like show alert dialogue "Need to upgrade app"
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("market://details?id=" + mContext.getPackageName()));
                                        if (intent.resolveActivity(mContext.getPackageManager()) != null) { //可以接收
                                            mContext.startActivity(intent);
                                        } else { //没有应用市场，我们通过浏览器跳转到Google Play
                                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + mContext.getPackageName()));
                                            mContext.startActivity(intent);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    private void showNoticeDialog(String updateMsg, String downloadUrl, float size) {
        if (AppUtils.isGoogleChannel(mContext) && !mBuilder.supportGoogle){
            //如果属于google渠道,并且不支持google更新检查,则不做任何处理
            LogUtils.logi("UpdateManager>>>[google平台]: 不做更新检查");
        }else {
            showNoticeDialog(updateMsg, downloadUrl, size, true);
        }
    }

    private void showNoticeDialog(String updateMsg, String downloadUrl, float size, boolean showDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setTitle(R.string.update_title);
        builder.setMessage(updateMsg);
        final String url = downloadUrl;
        final float s = size;
        final boolean show = showDialog;
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (AppUtils.isGoogleChannel(mContext)){
                    String packageName = mContext.getPackageName();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + packageName));
                    intent.setPackage("com.android.vending");//这里对应的是谷歌商店，跳转别的商店改成对应的即可
                    if (intent.resolveActivity(mContext.getPackageManager()) != null) { //可以接收
                        mContext.startActivity(intent);
                    } else { //没有应用市场，我们通过浏览器跳转到Google Play
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                        mContext.startActivity(intent);
                    }
                }else {
                    downloadFile(url, s, show);
                }
            }
        });
        builder.setNegativeButton(R.string.remind_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mDialog = builder.show();
    }

    public void setDownloadDialog(AlertDialog downloadDialog) {
        if (downloadDialog != mDownloadDialog) {
            mDownloadDialog = downloadDialog;
        }
    }

    public void downloadFile(String downloadUrl, float size, boolean showDialog) {
        if (TextUtils.isEmpty(downloadUrl)) {
            Toast.makeText(mContext, mContext.getString(R.string.download_url_error), Toast.LENGTH_LONG).show();
            return;
        }
        //解决android10.0  安装包文件放到/storage/emulated/0/Android/data/包名/files文件夹下,不需要动态授权
        /*File path = Environment.getExternalStorageDirectory();
        File dirPath = new File(path, "download");*/
        File dirPath = FileUtils.getExternalFilePath(mContext, "apk");
        LogUtils.logi("UpdateManager>>>[downloadFile]: "+dirPath);
        if (!dirPath.exists()) {
            if (dirPath.mkdir()) {
                Toast.makeText(mContext, "没有权限", Toast.LENGTH_LONG).show();
                return;
            }
        }
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        File saveFile = new File(dirPath, fileName);

        if (saveFile.exists()) {
            install(saveFile);
            return;
        }

        try {
            if (mDownloadDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setCancelable(false);
                builder.setTitle(R.string.update_title);
                builder.setView(R.layout.update_layout);
                mDownloadDialog = builder.create();
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mDownloadThread.interrupt();
                        interceptFlag = true;
                    }
                });
            }
            mSize = size;
            mDownloadDialog.show();
            View progressView = mDownloadDialog.findViewById(R.id.progress);
            if (progressView != null && progressView instanceof ProgressBar) {
                mProgress = (ProgressBar) progressView;
            }
            View totalView = mDownloadDialog.findViewById(R.id.tv_total);
            if (totalView != null && totalView instanceof TextView) {
                mTvTotal = (TextView) totalView;
                mTvTotal.setText(mContext.getString(R.string.total_size, mSize / 1024));
            }

            mShowDialog = showDialog;
            saveFile = new File(dirPath, fileName + ".tmp");
            downloadApk(saveFile, downloadUrl);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    public void showDownloadDialog(String downloadUrl, float size) {
        downloadFile(downloadUrl, size, true);
    }

    private DownloadThread mDownloadThread;

    private class DownloadThread extends Thread {
        String downloadUrl;
        File saveFile;

        DownloadThread(File saveFile, String downloadUrl) {
            this.saveFile = saveFile;
            this.downloadUrl = downloadUrl;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                FileOutputStream fos = new FileOutputStream(saveFile);
                mHandler.obtainMessage(DOWN_BEFORE, downloadUrl).sendToTarget();
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numRead = is.read(buf);
                    count += numRead;
                    int progress = (int) (((float) count / length) * 100);
                    //更新进度
                    mHandler.obtainMessage(DOWN_UPDATE, progress, 0).sendToTarget();
                    if (numRead <= 0) {
                        File file = new File(saveFile.getCanonicalPath().replace(".tmp", ""));
                        saveFile.renameTo(file);
                        //下载完成通知安装
                        mHandler.obtainMessage(DOWN_OVER, file).sendToTarget();
                        break;
                    }
                    fos.write(buf, 0, numRead);
                } while (!interceptFlag);//点击取消就停止下载.
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.obtainMessage(DOWN_FAIL, downloadUrl).sendToTarget();
            }
        }
    }

    public DownloadListener getDownloadListener() {
        return mDownloadListener;
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        this.mDownloadListener = downloadListener;
    }

    /**
     * 是否是安装文件
     *
     * @return 是安装文件
     */
    public boolean isApk() {
        return mApk;
    }

    /**
     * 设置是否是安装文件
     *
     * @param apk 是安装文件
     */
    public void setApk(boolean apk) {
        this.mApk = apk;
    }

    /**
     * 下载apk
     */
    private void downloadApk(File saveFile, String downloadUrl) {
        if (mDownloadThread != null) {
            if (mDownloadThread.isAlive()) {
                return;
            }
        }
        mDownloadThread = new DownloadThread(saveFile, downloadUrl);
        mDownloadThread.start();
    }

    /**
     * 安装
     *
     * @param file 文件位置
     */
    private void install(File file) {
        if (!file.exists()) {
            return;
        }
        //新修改  7.0以上版本安装崩溃问题
        if (mDownloadDialog != null) {
            mDownloadDialog.dismiss();
        }
        if (mDownloadListener != null) {
            mDownloadListener.onInstall(file);
        }
        if (mApk) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                //7.0 以上安装
//                Uri fileUri = FileProvider.getUriForFile(mContext, Provider.class.getName(), file);//android 7.0以上
//                intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
//                grantUriPermission(mContext, fileUri, intent);
//            } else {
//                //7.0 以下安装
//                intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
//            }
            FileProvider7.setIntentDataAndType(mContext,
                    intent, "application/vnd.android.package-archive", file, true);
            //跳转
            mContext.startActivity(intent);
            //android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private static void grantUriPermission(Context context, Uri fileUri, Intent intent) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }


   /* NotificationCompat.Builder notifyBuilder;
    NotificationManager manager;
    public void showNotification(String title, String content, Class<?>piClass, int largeIcon, int smallIcon, int id) {
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(mContext, piClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        //版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            String appName = AppUtils.getAppName(mContext);
            String _channelId = appName+"_channelId";
            NotificationChannel mChannel = new NotificationChannel(_channelId, appName, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(mChannel);
            notifyBuilder = new NotificationCompat.Builder(mContext, _channelId);
            notifyBuilder.setContentTitle(title)  //标题
                    .setContentText(content)   //内容
                    .setWhen(System.currentTimeMillis())    //系统显示时间
                    .setSmallIcon(smallIcon)     //收到信息后状态栏显示的小图标
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), largeIcon))
                    .setContentIntent(pendingIntent);    //绑定PendingIntent对象
            Notification notification = notifyBuilder.build();
            manager.notify(id, notification);
        } else if (Build.VERSION.SDK_INT >= 23) {
            notifyBuilder = new NotificationCompat.Builder(mContext);
            notifyBuilder.setContentTitle(title)
                    .setContentText(content)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), largeIcon))
                    .setSmallIcon(smallIcon)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis());
            Notification notification = notifyBuilder.build();
            manager.notify(id, notification);
        } else {
            Notification.Builder builder = new Notification.Builder(mContext);
            builder.setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setOngoing(false)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), largeIcon))
                    .setSmallIcon(smallIcon)
                    .setWhen(System.currentTimeMillis());
            Notification notification = builder.build();
            manager.notify(id, notification);
        }
    }

    private void updateNotification(int arg1) {
        if (Build.VERSION.SDK_INT >= 24){
            notifyBuilder.setProgress(100,arg1,false);
            notifyBuilder.setContentText(arg1+"%");
            manager.notify(1001,notifyBuilder.build());
        }else{
            Notification notification = notifyBuilder.build();
            notification.contentView.setProgressBar(android.R.id.progress,100,arg1,false);
            notifyBuilder.setContentText(arg1+"%");
            manager.notify(1001,notification);
        }
    }

    private void cancelNotification(){

    }*/

}