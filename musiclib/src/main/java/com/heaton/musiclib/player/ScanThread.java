package com.heaton.musiclib.player;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.heaton.musiclib.BuildConfig;
import com.heaton.musiclib.MusicManager;
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
    private static final String TAG = "ScanThread";
    private Context mContext;
    private Handler mHandler;
    private ArrayList<MusicVO> mMusicList;
    private String where;
    private String sortOrder = MediaStore.Audio.Media.TITLE + " asc";

    public ScanThread(Context context, Handler handler, ArrayList<MusicVO> musicList, String where, String sortOrder) {
        this.mContext = context;
        this.mHandler = handler;
        this.mMusicList = musicList;
        this.where = where;
        if (!TextUtils.isEmpty(sortOrder)){
            this.sortOrder = sortOrder;
        }
    }

    @Override
    public void run() {
        // while (isRun) {
        // Looper.prepare();
//        String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') and bucket_display_name <> 'audio' and is_music > 0 " ;
        ContentResolver conRes = mContext.getContentResolver();
        Cursor cur = conRes.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where, null, sortOrder);
//        Cursor cur = conRes.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.AudioColumns.IS_MUSIC);

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
                Log.i(TAG,"music_url>>>>>"+url);
//                Log.e(TAG,"music_info>>>>>"+url+"----title:>>"+cur.getString(titleIndex)+"----artist:>>"+cur.getString(artistIndex));
//                String artist0 = cur.getString(artistIndex);
//                if (TextUtils.isEmpty(artist0) || "<unknown>".equals(artist0)){
//                    Log.i(TAG, "run: artist0："+artist0);
//                    continue;
//                }
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

                //Android Q 公有目录只能通过Content Uri + id的方式访问，以前的File路径全部无效，如果是Video，记得换成MediaStore.Videos
                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                    url = cur.getString(musicId);
                    *//*url = MediaStore.Audio.Media
                            .EXTERNAL_CONTENT_URI
                            .buildUpon()
                            .appendPath(String.valueOf(id)).build().toString();
                    Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
                    //url = FileUtils.getAndroidQPathByUri(MusicManager.getInstance().getContext(), uri);*//*
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
                mMusicList.add(music);
            }
        }
        cur.close();
        // Looper.loop();
        mHandler.obtainMessage(MusicManager.MSG_SCANNED_MUSIC).sendToTarget();
    }

}
