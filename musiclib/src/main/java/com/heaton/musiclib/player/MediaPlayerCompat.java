package com.heaton.musiclib.player;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.heaton.musiclib.MusicManager;
import com.heaton.musiclib.player.callback.OnCompletionListener;
import com.heaton.musiclib.player.callback.OnDataCaptureListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MediaPlayerCompat implements IPlayer {

    private static final String TAG = "MediaPlayerCompat";

    private MediaPlayer customMediaPlayer = new MediaPlayer();
    private android.media.MediaPlayer nativeMediaPlayer = new android.media.MediaPlayer();
    private PlayerType playerType = PlayerType.NATIVE_PLAYER;

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.setVolume(leftVolume, rightVolume);
        }else {
            customMediaPlayer.setVolume(leftVolume, rightVolume);
        }
    }

    @Override
    public void start() {
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.start();
        }else {
            customMediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.stop();
        }else {
            customMediaPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.pause();
        }else {
            customMediaPlayer.pause();
        }
    }

    @Override
    public void seekTo(int seek) {
        Log.e(TAG, "seekTo: "+seek);
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.seekTo(seek*nativeMediaPlayer.getDuration()/100);
            nativeMediaPlayer.start();// 开始播放
        }else {
            customMediaPlayer.seekTo(seek);// 指定位置
        }
    }

    @Override
    public int getPlayCurrentTime() {
        if (isNativeMediaPlayer()){
            return nativeMediaPlayer.getCurrentPosition();
        }else {
            return customMediaPlayer.getCurrentPosition();
        }
    }

    @Override
    public int getPlayDuration() {
        if (isNativeMediaPlayer()){
            return nativeMediaPlayer.getDuration();
        }else {
            return customMediaPlayer.getDuration();
        }
    }

    @Override
    public boolean isPlaying() {
        if (isNativeMediaPlayer()){
            return nativeMediaPlayer.isPlaying();
        }else {
            return customMediaPlayer.isPlaying();
        }
    }

    @Override
    public void play(String path) throws IOException {
        Log.e(TAG, "play: "+path);
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.reset();
            nativeMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(path));
                nativeMediaPlayer.setDataSource(MusicManager.getInstance().getContext(),uri);
            }else {
                nativeMediaPlayer.setDataSource(path);
            }*/
            nativeMediaPlayer.setDataSource(path);
            nativeMediaPlayer.prepare();
        }else {
            customMediaPlayer.reset();
            customMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(path));
                customMediaPlayer.setDataSource(MusicManager.getInstance().getContext(), uri);
            }else {
                customMediaPlayer.setDataSource(path);
            }*/
            customMediaPlayer.setDataSource(path);
            customMediaPlayer.prepare();
        }

    }

    @Override
    public void setLooping(boolean looping) {
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.setLooping(looping);
        }else {
            customMediaPlayer.setLooping(looping);
        }
    }

    @Override
    public void setMediaPlayerType(PlayerType type){
        this.playerType = type;
    }

    public boolean isNativeMediaPlayer(){
        return playerType == PlayerType.NATIVE_PLAYER;
    }

    public Object getMediaPlayer() {
        if (playerType==PlayerType.NATIVE_PLAYER){
            return nativeMediaPlayer;
        }
        return customMediaPlayer;
    }

    public void setOnCompletionListener(final OnCompletionListener completionListener) {
        if (isNativeMediaPlayer()){
            nativeMediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(android.media.MediaPlayer mp) {
                    completionListener.onCompletion(mp);
                }
            });
        }else {
            customMediaPlayer.setOnCompletionListener(completionListener);
        }
    }

    private OnDataCaptureListener dataCaptureListener;
    public Visualizer visualizer;
    public void setDataCaptureListener(final OnDataCaptureListener dataCaptureListener) {
        if (isNativeMediaPlayer()){
            //有问题
            this.dataCaptureListener = dataCaptureListener;
        }else {
            customMediaPlayer.setDataCaptureListener(dataCaptureListener);
        }
    }

    private void startVisualizer(){
        visualizer = new Visualizer(nativeMediaPlayer.getAudioSessionId());
        Log.e(TAG, "AudioSessionId:"+nativeMediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);//采样 - 参数内必须是2的位数 - 如128,256,512,1024
        visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {

            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                int length = fft.length;
                short[] b = new short[length / 4];
                for (int i = 0; i < length; i += 4) {
                    int j = i / 4;
                    int le = length / 4;
                    if (j < le) {
                        b[j] = (short) (fft[i] & 0xff);
                        b[j] |= (short) ((fft[i + 1] & 0xff) << 8);
                    }
                }
                if (dataCaptureListener != null){
                    dataCaptureListener.onWaveDataCapture(b, samplingRate);
                }
            }
        }, Visualizer.getMaxCaptureRate()/2, false, true);
        visualizer.setEnabled(true);
    }

    private void stopVisualizer(){
        if (visualizer != null){
            visualizer.setEnabled(false);
            visualizer.release();
            visualizer = null;
        }
    }

    public void stopRhythm() {
        if (isNativeMediaPlayer()){
            stopVisualizer();
        }else {
            customMediaPlayer.setRhythming(false);
        }
    }

    public void startRhythm() {
        if (isNativeMediaPlayer()){
            startVisualizer();
        }else {
            customMediaPlayer.setRhythming(true);
        }
    }

    public enum PlayerType {
        NATIVE_PLAYER, CUSTOM_PLAYER
    }
}
