package com.heaton.musiclib.player;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.heaton.musiclib.player.callback.OnCompletionListener;
import com.heaton.musiclib.player.callback.OnDataCaptureListener;

import java.io.IOException;
import java.nio.ByteBuffer;


public class MediaPlayer implements Runnable {
    public static final String LOG_TAG = "MediaPlayer";

    public static final long DEFALUT_DELAY = 80;//默认律动数据回调间隔(真实间隔= 设置间隔+20ms,设置80ms，实际为100ms)
    private MediaExtractor extractor;
    private MediaCodec codec;
    private AudioTrack audioTrack;
    private int mStreamType = AudioManager.STREAM_MUSIC;

    private ByteBuffer[] decodeInputBuffers;
    private ByteBuffer[] decodeOutputBuffers;
    private MediaCodec.BufferInfo decodeBufferInfo;

    private OnCompletionListener onCompletionListener;
    private OnDataCaptureListener onDataCaptureListener;
    private OnPreparedListener onPreparedListener;
    private OnStartListener onStartListener;
    private OnErrorListener onErrorListener;
    private PlayerStates state = new PlayerStates();
    private String sourcePath = null;
    private Uri sourceUri = null;
    private int sourceRawResId = -1;
    private Context mContext;
    private boolean stop = false;
    private boolean isRhythming = false;

    private String mime = null;
    private int sampleRate = 0, channels = 0, bitrate = 0;
    private long presentationTimeUs = 0, duration = 0;

    //default
    public MediaPlayer() {

    }

    public interface OnPreparedListener {
        void onPrepared(MediaPlayer player);
    }

    public interface OnStartListener {
        void onStart(String mime, int sampleRate, int channels, long duration);
    }

    public interface OnErrorListener {
        void onError(MediaPlayer player, String errorMsg);
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.onCompletionListener = listener;
    }

    public void setDataCaptureListener(OnDataCaptureListener listener) {
        this.onDataCaptureListener = listener;
    }

    public void setOnPreparedListener(OnPreparedListener listener) {
        this.onPreparedListener = listener;
    }

    public void setOnStartListener(OnStartListener listener) {
        this.onStartListener = listener;
    }

    public void setOnErrorListener(OnErrorListener listener) {
        this.onErrorListener = listener;
    }

    /**
     * For live streams, duration is 0
     *
     * @return
     */
    public boolean isLive() {
        return (duration == 0);
    }

    public void setVolume(float leftVolume, float rightVolume) {
        if (audioTrack != null) {
            audioTrack.setStereoVolume(leftVolume, rightVolume);
        }
    }

    public void setLooping(boolean looping) {
        if (audioTrack != null) {
//            audioTrack.setLoopPoints(0, )
        }
    }

    /**
     * set the data source, a file path or an url, or a file descriptor, to play encoded audio from
     *
     * @param src
     */
    public void setDataSource(String src) {
        sourcePath = src;
    }

    public void setDataSource(Context context, Uri uri){
        mContext = context;
        this.sourceUri = uri;
    }

    public void setDataSource(Context context, int resid) {
        mContext = context;
        sourceRawResId = resid;
    }

    public void reset() {
        state.set(PlayerStates.STOPPED);
        stop = true;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void prepare() {
        if (sourcePath == null && sourceUri == null) {
            throw new IllegalArgumentException("sourcePath can't be null");
        }
        initMediaDecode();
        initAudioTrack();
    }

    public void setAudioStreamType(int streamtype) {
        mStreamType = streamtype;
    }

    public int getAudioStreamType() {
        return mStreamType;
    }

    public void start() {
        if (state.get() == PlayerStates.STOPPED) {
            stop = false;
            new Thread(this).start();
        }
        if (state.get() == PlayerStates.READY_TO_PLAY) {
            state.set(PlayerStates.PLAYING);
            syncNotify();
        }
    }

    /**
     * Call notify to control the PAUSE (waiting) state, when the state is changed
     */
    public synchronized void syncNotify() {
        notify();
    }

    public void stop() {
        stop = true;
    }

    public void pause() {
        state.set(PlayerStates.READY_TO_PLAY);
    }

    public void seekTo(long pos) {
        if (extractor != null) {
            extractor.seekTo(pos, MediaExtractor.SEEK_TO_CLOSEST_SYNC);
        }
    }

    public void seekTo(int percent) {
        long pos = percent * duration / 100;
        seekTo(pos);
    }

    public int getCurrentPosition() {
        return (int) (presentationTimeUs / 1000);
    }

    public int getDuration() {
        return (int) (duration / 1000);
    }

    public boolean isPlaying() {
        return state.isPlaying();
    }

    public int getPlayState() {
        return state.get();
    }


    /**
     * A pause mechanism that would block current thread when pause flag is set (READY_TO_PLAY)
     */
    private synchronized void waitPlay() {
        // if (duration == 0) return;
        while (state.get() == PlayerStates.READY_TO_PLAY) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化解码器
     */
    public void initMediaDecode() {
        // extractor gets information about the stream
        extractor = new MediaExtractor();
        // try to set the source, this might fail
        try {
            if (sourcePath != null) extractor.setDataSource(this.sourcePath);
            if (sourceUri != null) extractor.setDataSource(mContext, this.sourceUri, null);
            if (sourceRawResId != -1) {
                AssetFileDescriptor fd = mContext.getResources().openRawResourceFd(sourceRawResId);
                extractor.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
                fd.close();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "exception:" + e.getMessage());
            if (onErrorListener != null) {
                onErrorListener.onError(this, e.getMessage());
            }
            return;
        }

        // Read track header
        MediaFormat format = null;
        try {
            format = extractor.getTrackFormat(0);
            mime = format.getString(MediaFormat.KEY_MIME);
            sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE);
            channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
            // if duration is 0, we are probably playing a live stream
            duration = format.getLong(MediaFormat.KEY_DURATION);
            bitrate = format.getInteger(MediaFormat.KEY_BIT_RATE);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Reading format parameters exception:" + e.getMessage());
            // don't exit, tolerate this error, we'll fail later if this is critical
        }
        Log.e(LOG_TAG, "Track info: mime:" + mime + " sampleRate:" + sampleRate + " channels:" + channels + " bitrate:" + bitrate + " duration:" + duration);

        // check we have audio content we know
        if (format == null || !mime.startsWith("audio/")) {
            if (onErrorListener != null) {
                onErrorListener.onError(this, "Not an audio file or format is null");
            }
            return;
        }
        // create the actual decoder, using the mime to select
        try {
            codec = MediaCodec.createDecoderByType(mime);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // check we have a valid codec instance
        if (codec == null) {
            if (onErrorListener != null) {
                onErrorListener.onError(this, "Encoder is null");
            }
            return;
        }

        //state.set(PlayerStates.READY_TO_PLAY);
        if (onStartListener != null) {
            onStartListener.onStart(mime, sampleRate, channels, duration);
        }
        codec.configure(format, null, null, 0);
        codec.start();
        decodeInputBuffers = codec.getInputBuffers();
        decodeOutputBuffers = codec.getOutputBuffers();
        decodeBufferInfo = new MediaCodec.BufferInfo();
    }

    public void initAudioTrack() {
//        FileUtils.init();
        // configure AudioTrack
        int channelConfiguration = channels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
        int minSize = AudioTrack.getMinBufferSize(sampleRate, channelConfiguration, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(mStreamType, sampleRate, channelConfiguration,
                AudioFormat.ENCODING_PCM_16BIT, minSize, AudioTrack.MODE_STREAM);

        // start playing, we will feed the AudioTrack later
        audioTrack.play();
        extractor.selectTrack(0);
        if (onPreparedListener != null) {
            onPreparedListener.onPrepared(this);
        }
    }

    private long lastTime = System.currentTimeMillis(); //上一次的时间戳

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        // start decoding
        final long kTimeOutUs = 1000;
        boolean sawInputEOS = false;
        boolean sawOutputEOS = false;
        int noOutputCounter = 0;
        int noOutputCounterLimit = 10;

        state.set(PlayerStates.PLAYING);
        while (!sawOutputEOS && noOutputCounter < noOutputCounterLimit && !stop) {
            // pause implementation
            waitPlay();

            noOutputCounter++;
            // read a buffer before feeding it to the decoder
            if (!sawInputEOS) {
                int inputBufIndex = codec.dequeueInputBuffer(kTimeOutUs);
                if (inputBufIndex >= 0) {
                    ByteBuffer dstBuf = decodeInputBuffers[inputBufIndex];
                    int sampleSize = extractor.readSampleData(dstBuf, 0);
                    if (sampleSize <= 0) {
                        Log.d(LOG_TAG, "saw input EOS. Stopping playback");
                        sawInputEOS = true;
                        sampleSize = 0;
                        presentationTimeUs = duration;
                    } else {
                        presentationTimeUs = extractor.getSampleTime();
                    }

                    codec.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);

                    if (!sawInputEOS) extractor.advance();

                } else {
                    Log.e(LOG_TAG, "inputBufIndex " + inputBufIndex);
                }
            } // !sawInputEOS

            // decode to PCM and push it to the AudioTrack player
            int res = codec.dequeueOutputBuffer(decodeBufferInfo, kTimeOutUs);

            if (res >= 0) {
                if (decodeBufferInfo.size > 0) noOutputCounter = 0;

                int outputBufIndex = res;
                ByteBuffer buf = decodeOutputBuffers[outputBufIndex];

                final byte[] chunk = new byte[decodeBufferInfo.size];
                buf.get(chunk);
                buf.clear();
                if (chunk.length > 0) {
                    audioTrack.write(chunk, 0, chunk.length);
                    if (onDataCaptureListener != null && isRhythming) {
                        //单通道正常取值   双通道则只取奇数或者偶数位的byte
                        int length = chunk.length;
                        short[] b = new short[length / 4];
                        for (int i = 0; i < length; i += 4) {
                            int j = i / 4;
                            int le = length / 4;
                            if (j < le) {
                                b[j] = (short) (chunk[i] & 0xff);
                                b[j] |= (short) ((chunk[i + 1] & 0xff) << 8);
                            }
                        }
                        if (System.currentTimeMillis() - lastTime > DEFALUT_DELAY) {
                            lastTime = System.currentTimeMillis();
                            onDataCaptureListener.onWaveDataCapture(b, sampleRate);
                        }
                    }
                }
                codec.releaseOutputBuffer(outputBufIndex, false);
                if ((decodeBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d(LOG_TAG, "saw output EOS.");
                    sawOutputEOS = true;
                }
            } else if (res == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                decodeOutputBuffers = codec.getOutputBuffers();
                Log.d(LOG_TAG, "output buffers have changed.");
            } else if (res == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat oformat = codec.getOutputFormat();
                Log.d(LOG_TAG, "output format has changed to " + oformat);
            } else {
                Log.d(LOG_TAG, "dequeueOutputBuffer returned " + res);
            }
        }

        if (onCompletionListener != null && presentationTimeUs == duration) {
            onCompletionListener.onCompletion(MediaPlayer.this);
        }

        Log.d(LOG_TAG, "stopping...");

        state.set(PlayerStates.STOPPED);
        stop = true;

        if (noOutputCounter >= noOutputCounterLimit) {
            if (onErrorListener != null) {
                onErrorListener.onError(MediaPlayer.this, "noOutputCounter more than the noOutputCounterLimit");
            }
        }

        release();
    }

    public void setRhythming(boolean rhythming) {
        isRhythming = rhythming;
    }

    /**
     * 释放资源
     */
    public void release() {
        if (codec != null) {
            codec.stop();
            codec.release();
            codec = null;
        }
        if (audioTrack != null) {
            audioTrack.flush();
            audioTrack.release();
            audioTrack = null;
        }
        // clear source and the other globals
        sourcePath = null;
        sourceUri = null;
        sourceRawResId = -1;
        duration = 0;
        mime = null;
        sampleRate = 0;
        channels = 0;
        bitrate = 0;
        presentationTimeUs = 0;
        duration = 0;
    }

    public static String listCodecs() {
        String results = "";
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            // grab results and put them in a list
            String name = codecInfo.getName();
            boolean isEncoder = codecInfo.isEncoder();
            String[] types = codecInfo.getSupportedTypes();
            String typeList = "";
            for (String s : types) typeList += s + " ";
            results += (i + 1) + ". " + name + " " + typeList + "\n\n";
        }
        return results;
    }

}
