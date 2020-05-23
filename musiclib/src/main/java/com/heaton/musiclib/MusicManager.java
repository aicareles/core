package com.heaton.musiclib;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.heaton.musiclib.player.MediaPlayerCompat;
import com.heaton.musiclib.player.PlayerHelper;
import com.heaton.musiclib.player.callback.OnDataCaptureListener;
import com.heaton.musiclib.player.callback.RecordDataCallBack;
import com.heaton.musiclib.player.callback.MusicScanCallback;
import com.heaton.musiclib.player.ScanThread;
import com.heaton.musiclib.player.callback.PlayStateCallback;
import com.heaton.musiclib.player.callback.ServiceConnectedCallback;
import com.heaton.musiclib.player.constant.PlayerFinal;
import com.heaton.musiclib.player.db.DatabaseHelper;
import com.heaton.musiclib.player.recoder.SoundRecordHelper;
import com.heaton.musiclib.player.service.PlayerService;
import com.heaton.musiclib.vo.MusicVO;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * description $desc$
 * created by jerry on 2019/5/28.
 */
public class MusicManager {
    public final static int MSG_SCANNED_MUSIC = 0x303;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    @SuppressLint("StaticFieldLeak")
    private static MusicManager sMusicManager;
    private ScanThread mScanThread;
    private MusicScanCallback mMusicScanCallback;
    private PlayStateCallback mPlayStateCallback;
    private ServiceConnectedCallback mServiceConnectedCallback;
    private RecordDataCallBack mRecordDataCallBack;
    private MessageHandler mHandler = new MessageHandler();
    private ArrayList<MusicVO> mMusicList = new ArrayList<>();
    private Intent mServiceIntent;
    private MusicPlayer mMusicPlayer;
    private boolean isPlaying = false;
    private boolean isMusicRhythming = false;
    private SoundRecordHelper mSoundRecordHelper;

    @SuppressLint("HandlerLeak")
    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SCANNED_MUSIC:
                    if (mMusicScanCallback != null) {
                        mMusicScanCallback.onMusicScanResult(mMusicList);
                    }
                    if (mScanThread != null && mScanThread.isAlive()) {
                        mScanThread.interrupt();
                        mScanThread = null;
                        return;
                    }
                    break;
            }
        }
    }

    public static MusicManager getInstance() {
        if (sMusicManager == null) {
            sMusicManager = new MusicManager();
        }
        return sMusicManager;
    }

    /**
     * 务必初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mSoundRecordHelper = new SoundRecordHelper();
        //绑定音乐服务
        mServiceIntent = new Intent(context, PlayerService.class);
        //开启前台服务(能够提高app的存活时间)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(mServiceIntent);
        } else {
            context.startService(mServiceIntent);
        }
        context.bindService(mServiceIntent, mServiceConnection, Service.BIND_AUTO_CREATE);
    }

    public void startForegroundService(){

    }

    public void setMediaPlayerType(MediaPlayerCompat.PlayerType type) {
        getMediaPlayer().setMediaPlayerType(type);
    }

    /**
     * 退出时销毁
     */
    public void destory() {
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
            mContext.stopService(mServiceIntent);
        }
        stopRecord();
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMusicPlayer = MusicPlayer.Stub.asInterface(service);
            if (mServiceConnectedCallback != null) {
                mServiceConnectedCallback.onServiceConnected();
            }
            try {
                mMusicPlayer.registerCallback(mIPlayerCallback);
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
            try {
                mMusicPlayer.dataChange(mMusicList, 0,  PlayerFinal.MODE_LOOP);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    // 状态改变监听事件,改变UI
    private IPlayerCallback mIPlayerCallback = new IPlayerCallback.Stub() {
        @Override
        public void onStateChange(int state, int mode, int position) {
            mPlayStateCallback.onStateChange(state, mode, position);
            switch (state) {
                case PlayerFinal.STATE_PLAY:
                case PlayerFinal.STATE_CONTINUE:
                    isPlaying = true;
                    break;
                case PlayerFinal.STATE_PAUSE:
                case PlayerFinal.STATE_STOP:
                    isPlaying = false;
                    break;
            }
        }

        @Override
        public void onSeekChange(int progress, int max, String time, String duration) throws RemoteException {
            if (mPlayStateCallback != null) {
                mPlayStateCallback.onSeekChange(progress, max, time, duration);
            }
        }

        @Override
        public void onModeChange(int mode) throws RemoteException {
            if (mPlayStateCallback != null) {
                mPlayStateCallback.onModeChange(mode);
            }
        }
    };

    public boolean isPlaying() {
        return isPlaying;
    }

    public Context getContext() {
        if (mContext == null) {
            throw new IllegalStateException("please init MusicManager");
        }
        return mContext;
    }

    public MediaPlayerCompat getMediaPlayer() {
        return PlayerHelper.getPlayer();
    }

    public DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(mContext);
        }
        return mDatabaseHelper;
    }

    /**
     * 开启扫描本地音乐
     *
     * @param musicScanCallback
     */
    public void startScanMusic(MusicScanCallback musicScanCallback) {
        startScanMusic(null, null, musicScanCallback);
    }

    /**
     * 开启扫描本地音乐
     * @param where 过滤条件
     * @param sortOrder 歌曲排序条件
     * @param musicScanCallback
     */
    public void startScanMusic(String where, String sortOrder, MusicScanCallback musicScanCallback) {
        this.mMusicScanCallback = musicScanCallback;
        mScanThread = new ScanThread(mContext, mHandler, mMusicList, where, sortOrder);
        mScanThread.start();
        findByDB();
    }

    /**
     * 去数据库查询数据
     */
    private void findByDB() {
        try {
            Dao<MusicVO, Integer> dao = getDatabaseHelper().getDao();
            ArrayList<MusicVO> queryList = (ArrayList<MusicVO>) dao.queryBuilder().orderBy("title", true).query();
            //去除不存在的歌曲
            for (int i = 0; i < queryList.size(); i++) {
                //Log.e("music_Q", queryList.get(i).getTitle()+"|"+queryList.get(i).getDuration()+"|"+queryList.get(i).getUrl()+"|");
                int fileState = 1;
                File file = new File(queryList.get(i).getUrl());
                if (!file.exists())
                    fileState = 0;

//                if (queryList.get(i).duration < 3000 || !queryList.get(i).getUrl().endsWith(".mp3") && !queryList.get(i).getUrl().endsWith(".ogg") || fileState == 0) {
                if (fileState == 0) {
                    queryList.remove(i);
                    i--;
                }
            }
            synchronized (mMusicList) {
                mMusicList.clear();
                mMusicList.addAll(queryList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 录音律动数据回调
     *
     * @param recordDataCallBack
     */
    public void setRecordDataCallBack(RecordDataCallBack recordDataCallBack) {
        this.mRecordDataCallBack = recordDataCallBack;
        mSoundRecordHelper.setDatareportCallBack(new SoundRecordHelper.SoundRecoderHelperCallbackData() {
            @Override
            public void reportdata(short[] buffer) {
                if (mRecordDataCallBack != null) {
                    mRecordDataCallBack.onRecordData(buffer);
                }
            }
        });
    }

    /**
     * 开启录音
     */
    public void startRecord() {
        mSoundRecordHelper.start();
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mSoundRecordHelper.stop();
    }


    /**
     * 音乐播放律动数据回调
     *
     * @param onDataCaptureListener
     */
    public void setDataCaptureCallback(OnDataCaptureListener onDataCaptureListener) {
        getMediaPlayer().setDataCaptureListener(onDataCaptureListener);
    }

    public void stopRhythm() {
        if (isMusicRhythming) {
            isMusicRhythming = false;

            getMediaPlayer().stopRhythm();
        }
    }

    public void startRhythm() {
        if (!isMusicRhythming) {
            isMusicRhythming = true;
            getMediaPlayer().startRhythm();
        }
    }

    public boolean isMusicRhythming() {
        return isMusicRhythming;
    }

    /**
     * 音乐播放状态回调
     *
     * @param playStateCallback
     */
    public void setPlayStateCallback(PlayStateCallback playStateCallback) {
        this.mPlayStateCallback = playStateCallback;
    }

    /**
     * 音乐服务开启成功的回调
     *
     * @param serviceConnectedCallback
     */
    public void setServiceConnectedCallback(ServiceConnectedCallback serviceConnectedCallback) {
        this.mServiceConnectedCallback = serviceConnectedCallback;
    }

    public MusicPlayer getMusicPlayer() {
        return mMusicPlayer;
    }

    /**
     * 播放/暂停
     */
    public void playOrPause() {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.playOrPause();
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 播放列表音乐
     *
     * @param musicVO
     */
    public void playItem(MusicVO musicVO) {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.playItem(musicVO);
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 上一首
     */
    public void pre() {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.prev();
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下一首
     */
    public void next() {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.next();
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void changeSeek(int seek) {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.changeSeek(seek);
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void changeMode(int mode) {
        if (mMusicPlayer != null) {
            try {
                mMusicPlayer.changeMode(mode);
            } catch (RemoteException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

}
