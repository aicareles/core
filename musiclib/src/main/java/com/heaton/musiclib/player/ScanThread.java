package com.heaton.musiclib.player;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.heaton.musiclib.BuildConfig;
import com.heaton.musiclib.MusicManager;
import com.heaton.musiclib.player.constant.PlayerFinal;
import com.heaton.musiclib.vo.MusicVO;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地音乐扫描线程
 * Created by jerry on 2018/7/12.
 */

public class ScanThread extends Thread {
    private Context mContext;
    private Handler mHandler;
    private ArrayList<MusicVO> mMusicList;

    public ScanThread(Context context, Handler handler, ArrayList<MusicVO> musicList) {
        this.mContext = context;
        this.mHandler = handler;
        this.mMusicList = musicList;
    }

    @Override
    public void run() {
        // while (isRun) {
        // Looper.prepare();
        ContentResolver conRes = mContext.getContentResolver();
        Cursor cur = conRes.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.TITLE + " asc");

        Dao<MusicVO, Integer> musicDao = null;
        try {
            musicDao = MusicManager.getInstance().getDatabaseHelper().getDao();
        } catch (SQLException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
        if (musicDao == null) {
            return;
        }
        int titleIndex = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int artistIndex = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int albumIndex = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int pathIndex = cur.getColumnIndex(MediaStore.Audio.Media.DATA);
        int durationIndex = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int sizeIndex = cur.getColumnIndex(MediaStore.Audio.Media.SIZE);
        if (cur.getCount() != 0) {
            while (cur.moveToNext()) {
                String url = cur.getString(pathIndex);
                Log.e("music_url", url);
                try {
                    Map<String, Object> map = new HashMap<>();
                    map.put("url", url);
                    List<MusicVO> list = musicDao.queryForFieldValuesArgs(map);
                    if (list != null && list.size() > 0) {
                        continue;
                    }
                } catch (SQLException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }

                /*//Android Q 公有目录只能通过Content Uri + id的方式访问，以前的File路径全部无效，如果是Video，记得换成MediaStore.Videos
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    int id = cur.getInt(cur.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    url = MediaStore.Audio.Media
                            .EXTERNAL_CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(id)).build().toString();
                    Log.i("ScanThread", "Android10.0: url:"+url);
                }*/

                long duration = cur.getLong(durationIndex);
//                if (TextUtils.isEmpty(url) || duration < 3000 || !url.endsWith(".mp3") && !url.endsWith(".ogg") || !new File(url).exists()){
                if (TextUtils.isEmpty(url) || !new File(url).exists() || duration < 3000){
                    continue;
                }

                MusicVO music = new MusicVO();
                music.title = cur.getString(titleIndex);
//                music.artist = cur.getString(artistIndex);
                String artist = cur.getString(artistIndex);
                music.artist = TextUtils.isEmpty(artist) ? "<unknown>" : artist;
                music.album = cur.getString(albumIndex);
                music.url = cur.getString(pathIndex);

                music.duration = cur.getLong(durationIndex);
                music.fileSize = cur.getLong(sizeIndex);
                music.addDate = System.currentTimeMillis();

                try {
                    musicDao.create(music);
                } catch (SQLException e) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace();
                    }
                }
                music.setSort(music.id);

                Log.i(PlayerFinal.TAG, "insert---->" + music.id);
                mMusicList.add(music);
            }
        }
        cur.close();
        // Looper.loop();
        mHandler.obtainMessage(MusicManager.MSG_SCANNED_MUSIC).sendToTarget();
    }
}
