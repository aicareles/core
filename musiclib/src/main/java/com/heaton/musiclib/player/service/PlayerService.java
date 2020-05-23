package com.heaton.musiclib.player.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.heaton.musiclib.BuildConfig;
import com.heaton.musiclib.IPlayerCallback;
import com.heaton.musiclib.MusicPlayer;
import com.heaton.musiclib.R;
import com.heaton.musiclib.player.MediaPlayerCompat;
import com.heaton.musiclib.player.PlayerHelper;
import com.heaton.musiclib.player.constant.PlayerFinal;
import com.heaton.musiclib.utils.Utils;
import com.heaton.musiclib.vo.MusicVO;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

/**
 * 歌曲播放service
 */
public class PlayerService extends Service implements Runnable {
    public static final String TAG             = "PlayerService";
    public static final int NOTIFICATION_ID = 2;
    public static final int    MAX_SEEK_TICKER = 500;//进度条时间，毫秒
    public static final int    STATE_CHANGE    = 0x101;//状态变更
    public static final int    SEEK_CHANGE     = 0x102;//进度变更
    public static final int    MODE_CHANGE     = 0x103;//模式变更
    public static final int    ERROR_CHANGE    = 0x104;//播放出错
    // 当前音乐播放状态，默认为等待
    public              int    state           = PlayerFinal.STATE_WAIT;
    public boolean hold;//来电等待

    // 当前音乐循环模式，默认为随机
    public int playMode = PlayerFinal.MODE_LOOP;

    // 表示播放状态是否改变，进度条是否改变，播放模式时候改变
    public boolean stateChange, seekChange, modeChange;
    // 常驻线程是否运行
    public Boolean isRun = true;
    // 播放歌曲帮助类
    public PlayerHelper player;
    // 当前播放列表
    public List<MusicVO> serviceMusicList;
    // 当前播放歌曲位置
    public int servicePosition = 0;
    // 当前歌曲播放进度
    private int    progress = 0;
    // 当前歌曲进度条最大值
    private int    max      = 0;
    // 当前播放的时间
    private String time     = "0:00";
    // 当前歌曲播放的时长
    private String duration = "0:00";
    private AudioManager mAudioManager;
    private int          mLastSeekTime;//上次进度更新时间
//    private static PlayerReceiver receiver = null;


    // 监听回调
    private RemoteCallbackList<IPlayerCallback> mCallbacks = new RemoteCallbackList<>();

    // handler匿名内部类，用于监听器遍历回调
    private Handler handler = new MessageHandler(this);

    private static class MessageHandler extends Handler {
        private WeakReference<PlayerService> weakReference;

        MessageHandler(PlayerService service) {
            weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (weakReference == null) {
                return;
            }
            PlayerService playerService = weakReference.get();
            if (playerService == null) {
                return;
            }
            switch (msg.what) {
                case STATE_CHANGE:
                    int count = playerService.mCallbacks.beginBroadcast();
                    for (int i = 0; i < count; i++) {
                        try {
                            playerService.mCallbacks.getBroadcastItem(i).onStateChange(playerService.state, playerService.playMode, playerService.servicePosition);
                        } catch (RemoteException e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                    playerService.mCallbacks.finishBroadcast();
                    break;
                case SEEK_CHANGE:
                    count = playerService.mCallbacks.beginBroadcast();
                    for (int i = 0; i < count; i++) {
                        try {
                            playerService.mCallbacks.getBroadcastItem(i).onSeekChange(playerService.progress, playerService.max, playerService.time, playerService.duration);
                        } catch (RemoteException e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                    playerService.mCallbacks.finishBroadcast();
                    break;
                case MODE_CHANGE:
                    count = playerService.mCallbacks.beginBroadcast();
                    for (int i = 0; i < count; i++) {
                        try {
                            playerService.mCallbacks.getBroadcastItem(i).onModeChange(playerService.playMode);
                        } catch (RemoteException e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                    playerService.mCallbacks.finishBroadcast();
                    break;
                case ERROR_CHANGE:
                    count = playerService.mCallbacks.beginBroadcast();
                    for (int i = 0; i < count; i++) {
                        try {
                            playerService.mCallbacks.getBroadcastItem(i).onStateChange(playerService.state, playerService.playMode, playerService.servicePosition);
                        } catch (RemoteException e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
                    playerService.mCallbacks.finishBroadcast();

                    if (playerService.playMode != PlayerFinal.MODE_SINGLE) {
                        try {
                            playerService.mBinder.next();
                        } catch (RemoteException e) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace();
                            }
                        }
                    }
//                    Toast.makeText(getBaseContext(),"文件格式不支持或已损坏",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    MusicPlayer.Stub mBinder = new MusicPlayer.Stub() {
        /**
         * 播放或者暂停
         * @throws RemoteException
         */
        @Override
        public void playOrPause() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "playOrPause");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                // 如果接收的是点击暂停/播放键时的广播
                // 根据当前状态点击后，进行相应状态改变
                switch (state) {
                    case PlayerFinal.STATE_PLAY:
                    case PlayerFinal.STATE_CONTINUE:
                        state = PlayerFinal.STATE_PAUSE;
                        break;
                    case PlayerFinal.STATE_PAUSE:
                        state = PlayerFinal.STATE_CONTINUE;
                        break;
                    case PlayerFinal.STATE_WAIT:
                    case PlayerFinal.STATE_STOP:
                        state = PlayerFinal.STATE_PLAY;
                        break;
                }
                // state改变
                stateChange = true;
            }
        }

        /**
         * 播放歌曲
         * @param musicVO 歌曲对象
         * @throws RemoteException
         */
        @Override
        public void playItem(MusicVO musicVO) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "playItem");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                for (int i = 0, count = serviceMusicList.size(); i < count; i++) {
                    MusicVO music = serviceMusicList.get(i);
                    if (music == null) {
                        continue;
                    }
                    if (musicVO.id > 0 && musicVO.id == music.id) {
                        servicePosition = i;
                        break;
                    }
                }
            }
            state = PlayerFinal.STATE_PLAY;
            stateChange = true;
        }

        /**
         * 播放位置
         * @param position 位置
         * @throws RemoteException
         */
        @Override
        public void playPosition(int position) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "playPosition:" + position);
            }
            servicePosition = position;
            stateChange = true;
        }

        /**
         * 数据变更
         * @param musicList 音乐列表
         * @param position 位置
         * @param mode 模式
         * @throws RemoteException
         */
        @Override
        public void dataChange(List<MusicVO> musicList, int position, int mode) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "dataChange");
            }
            if (musicList != null) {
                serviceMusicList = musicList;
            }
            if (position > -1) {
                servicePosition = position;
                MediaPlayerCompat playerCompat = PlayerHelper.getPlayer();
                if (playerCompat.isNativeMediaPlayer()){
                    //解决第一次  seekTo无效的问题(提前初始化)
                    if (!serviceMusicList.isEmpty()){
                        try {
                            playerCompat.play(serviceMusicList.get(servicePosition).url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (mode > -1) {
                playMode = mode;
            }
        }

        /**
         * 注册回调对象
         * @param callback 对象
         * @throws RemoteException
         */
        @Override
        public void registerCallback(IPlayerCallback callback) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "registerCallback");
            }
            if (callback == null) {
                return;
            }
            mCallbacks.register(callback);
        }

        /**
         * 取消注册对象
         * @param callback 对象
         * @throws RemoteException
         */
        @Override
        public void unRegisterCallback(IPlayerCallback callback) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "unRegisterCallback");
            }
            mCallbacks.unregister(callback);
        }

        /**
         * 暂停
         * @throws RemoteException
         */
        @Override
        public void pause() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "pause");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                switch (state) {
                    case PlayerFinal.STATE_PLAY:
                    case PlayerFinal.STATE_CONTINUE:
                        state = PlayerFinal.STATE_PAUSE;
                        break;
                }
                stateChange = true;
            }
        }

        /**
         * 恢复播放
         * @throws RemoteException
         */
        @Override
        public void resume() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "resume");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                switch (state) {
                    case PlayerFinal.STATE_PAUSE:
                        state = PlayerFinal.STATE_CONTINUE;
                        break;
                    case PlayerFinal.STATE_WAIT:
                    case PlayerFinal.STATE_STOP:
                        state = PlayerFinal.STATE_PLAY;
                        break;
                }
                stateChange = true;
            }
        }

        /**
         * 来电暂停
         * @throws RemoteException
         */
        @Override
        public void hold() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "hold");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                switch (state) {
                    case PlayerFinal.STATE_PLAY:
                    case PlayerFinal.STATE_CONTINUE:
                        state = PlayerFinal.STATE_PAUSE;
                        hold = true;
                        stateChange = true;
                        break;
                }
            }
        }

        /**
         * 取消暂停
         * @throws RemoteException
         */
        @Override
        public void unHold() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "unHold");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0 && hold) {
                switch (state) {
                    case PlayerFinal.STATE_PAUSE:
                        state = PlayerFinal.STATE_CONTINUE;
                        break;
                    case PlayerFinal.STATE_WAIT:
                    case PlayerFinal.STATE_STOP:
                        state = PlayerFinal.STATE_PLAY;
                        break;
                }
                stateChange = true;
            }
            hold = false;
        }

        /**
         * 静音
         * @throws RemoteException
         */
        @Override
        public void mute() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "mute");
            }
            if (mAudioManager == null) {
                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            }
            float volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            player.mute(volume / maxVolume);
        }

        /**
         * 下一首
         * @throws RemoteException
         */
        @Override
        public void next() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "next");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                switch (playMode) {
                    case PlayerFinal.MODE_SINGLE:
                    case PlayerFinal.MODE_LOOP:
                        if(serviceMusicList.size()==1){
                            servicePosition = 0;
                        }else{
                            if (servicePosition == serviceMusicList.size() - 1) {
                                servicePosition = 0;
                            } else {
                                servicePosition++;
                            }
                        }
                        state = PlayerFinal.STATE_PLAY;
                        break;
                    case PlayerFinal.MODE_RANDOM:
                        if(serviceMusicList.size()==1){
                            servicePosition = 0;
                            state = PlayerFinal.STATE_PLAY;
                        }else{
                            Random random = new Random();
                            int p = servicePosition;
                            while (true) {
                                servicePosition = random.nextInt(serviceMusicList.size());
                                if (p != servicePosition) {
                                    state = PlayerFinal.STATE_PLAY;
                                    break;
                                }
                            }
                        }
                        break;
                }
                stateChange = true;
            }
        }

        /**
         * 上一首
         * @throws RemoteException
         */
        @Override
        public void prev() throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "prev");
            }
            if (serviceMusicList != null && serviceMusicList.size() > 0) {
                switch (playMode) {
                    case PlayerFinal.MODE_SINGLE:
                    case PlayerFinal.MODE_LOOP:
                        if(serviceMusicList.size()==1){
                            servicePosition = 0;
                        }else{
                            if (servicePosition == 0) {
                                servicePosition = serviceMusicList.size() - 1;
                            } else {
                                servicePosition--;
                            }
                        }
                        state = PlayerFinal.STATE_PLAY;
                        break;
                    case PlayerFinal.MODE_RANDOM:
                        if(serviceMusicList.size()==1){
                            servicePosition = 0;
                            state = PlayerFinal.STATE_PLAY;
                        }else{
                            Random random = new Random();
                            int p = servicePosition;
                            while (true) {
                                servicePosition = random.nextInt(serviceMusicList.size());
                                if (p != servicePosition) {
                                    state = PlayerFinal.STATE_PLAY;
                                    break;
                                }
                            }
                        }

                        break;
                }
                stateChange = true;
            }
        }

        /**
         * 更改模式
         * @param mode 模式
         * @throws RemoteException
         */
        @Override
        public void changeMode(int mode) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "changeMode:" + mode);
            }
            if (mode > -1) {
                playMode = mode;
            } else {
                switch (playMode) {
                    case PlayerFinal.MODE_SINGLE:
                        playMode = PlayerFinal.MODE_LOOP;
                        break;
                    case PlayerFinal.MODE_LOOP:
                        playMode = PlayerFinal.MODE_RANDOM;
                        break;
                    case PlayerFinal.MODE_RANDOM:
                        playMode = PlayerFinal.MODE_SINGLE;
                        break;
                }
            }
            modeChange = true;
        }

        /**
         * 更改进度
         * @param seek 进度
         * @throws RemoteException
         */
        @Override
        public void changeSeek(int seek) throws RemoteException {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "changeSeek:" + seek);
            }
            state = PlayerFinal.STATE_CONTINUE;
            player.seekToMusic(seek);
            stateChange = true;
            seekChange = true;
        }

        /**
         * 获取音频会话id
         * @return 会话id
         * @throws RemoteException
         */
        @Override
        public int getAudioSessionId() throws RemoteException {
            MediaPlayer mediaPlayer = (MediaPlayer) player.getPlayer().getMediaPlayer();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "getAudioSessionId:" + mediaPlayer.getAudioSessionId());
            }
            return mediaPlayer.getAudioSessionId();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        isRun = true;
        // new歌曲播放类
        player = new PlayerHelper(this);

        //设置状态
        stateChange = true;
        // 开启常驻线程
        new Thread(this).start();
        startForeService();
    }

    private void startForeService() {
        String title = "正在运行";//
        String content = "";
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent nfIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nfIntent, 0);
        Notification notification  = null;
        //版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            String appName = Utils.getAppName(getApplicationContext());
            String id = appName+"_channelId";
            NotificationChannel mChannel = new NotificationChannel(id, appName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(mChannel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), id);
            builder
//                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)  //标题
                    .setContentText(content) //内容
                    .setWhen(System.currentTimeMillis())    //系统显示时间
                    .setSmallIcon(R.drawable.ic_launcher)     //收到信息后状态栏显示的小图标
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.ic_launcher));
            notification = builder.build();
        } else {
            Notification.Builder builder = new Notification.Builder(getApplicationContext());
            builder
//                    .setContentIntent(pendingIntent) // 设置PendingIntent
                    .setContentTitle(title) // 设置下拉列表里的标题
                    .setSmallIcon(R.drawable.ic_launcher) // 设置状态栏内的小图标
                    .setContentText(content) // 设置上下文内容
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
            notification = builder.build();
            notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        }
        startForeground(NOTIFICATION_ID, notification);
    }

    public static void cancelNotifiction(Context context){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }


    @Override
    public void run() {
        while (isRun) {
            if (stateChange) {
                switch (state) {
                    case PlayerFinal.STATE_WAIT:
                        break;
                    case PlayerFinal.STATE_PLAY:
                        boolean result = false;
                        if (serviceMusicList != null && serviceMusicList.size() > 0) {
                            MusicVO musicVO = serviceMusicList.get(servicePosition);
                            if (musicVO.internet == 0) {
                                result = player.play(serviceMusicList.get(servicePosition).url);
                            } else if (musicVO.internet == 1) {
//                                Uri uri = Uri.parse(serviceMusicList.get(servicePosition).url);
//                                result = player.playInternet(getApplicationContext(), uri);
                                result = player.play(serviceMusicList.get(servicePosition).url);
                            }
                        }
                        if (result) {
                            seekChange = true;
                        } else {
                            stateChange = false;
                            handler.sendEmptyMessage(ERROR_CHANGE);
                            continue;
                        }
                        break;
                    case PlayerFinal.STATE_PAUSE:
                        player.pause();
                        break;
                    case PlayerFinal.STATE_CONTINUE:
                        player.continuePlay();
                        seekChange = true;
                        break;
                    case PlayerFinal.STATE_STOP:
                        player.stop();
                        break;
                }
                stateChange = false;
                handler.sendEmptyMessage(STATE_CHANGE);
            }
            seekChange = player.isPlaying();

            if (seekChange) {
                // 得到当前播放时间，int，毫秒单位，也是进度条的当前进度
                progress = player.getPlayCurrentTime();
                // 得到歌曲播放总时长，为进度条的最大值
                max = player.getPlayDuration();

                int musicTime = (progress / 1000);
                String fen = musicTime / 60 + "";
                String miao = musicTime % 60 + "";
                if (miao.length() == 1) {
                    miao = "0" + musicTime % 60;
                }
                time = fen + ":" + miao;

                // seekChange改回false
                seekChange = false;
                if (mLastSeekTime == 0 || Math.abs(mLastSeekTime - progress) >= MAX_SEEK_TICKER) {
                    mLastSeekTime = progress;
                    // 发送相应消息给handler
                    handler.sendEmptyMessage(SEEK_CHANGE);
                }
            }
            // 如果歌曲播放模式改变，发送消息给handler，modeChange改回false
            if (modeChange) {
                handler.sendEmptyMessage(MODE_CHANGE);
                modeChange = false;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 结束service之前，取消通知栏
     */
    @Override
    public boolean stopService(Intent name) {

        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        isRun = false;
        player.stop();
        player = null;
        stopForeground(true);
        super.onDestroy();
    }
}
