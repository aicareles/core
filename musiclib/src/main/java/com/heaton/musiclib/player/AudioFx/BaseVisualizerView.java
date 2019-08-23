package com.heaton.musiclib.player.AudioFx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

/**
 * A simple class that draws waveform data received from a
 * {@link
 */
public class BaseVisualizerView extends View {
    private static final String TAG = "BaseVisualizerView";
    private byte[] mBytes;
    private float[] mPoints;

    private Paint mForePaint = new Paint();
    private int mSpectrumNum = 64;

    final public int typeFFT = 0;
    final public int typeWave = 1;

    private int Drawtype = typeFFT;

    /**
     * 三个必要的初始化函数
     * @param context
     */
    public BaseVisualizerView(Context context) {
        super(context);
        init();
    }

    public BaseVisualizerView(Context context, AttributeSet attrs)
    {
        super(context,attrs);
        init();
    }

    public BaseVisualizerView(Context context, AttributeSet attrs,int defStyle)
    {
        super(context,attrs,defStyle);
        init();
    }

    public void setPaint(Paint paint)
    {
        mForePaint = new Paint(paint);
    }

    public void setPaintStock(float stock)
    {
        mForePaint.setStrokeWidth(stock);
    }

    private void init() {
        mBytes = null;

        mForePaint.setStrokeWidth(8f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public byte updateVisualizer(short[] real,short[] image)
    {
        Drawtype = typeFFT;
        if (real.length!=mSpectrumNum*2 || image.length != mSpectrumNum*2 || real.length != image.length)
            return 0;
        //返回电平
        long level = 0;
        //新建数组
        byte[] model = new byte[mSpectrumNum];

        for (int i = 0; i < mSpectrumNum; i++) {
            //计算功率谱
            model[i] = (byte) (Math.sqrt(real[i]*real[i] + image[i] * image[i])/32);
            //model[i] = (byte) (Math.abs(real[i]) /32767.0 * 127.0);
            level += model[i];
        }

        Log.e(TAG, "onWaveDataCapture+++: "+ Arrays.toString(model));

        //更新到数据缓冲区
        mBytes = model;
        //重绘
        invalidate();
        //返回FFT的电平值
        return (byte)(level/model.length);
    }

    /**
     * 更新数据显示
     * @param fft
     */
    public void updateVisualizer(byte[] fft) {

        Drawtype = typeFFT;
        byte[] model = new byte[fft.length / 2 + 1];
        //第一个值为fft的
        model[0] = (byte) Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mSpectrumNum; ) {
            //计算功率谱
            model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);

            i += 2;
            j++;
        }
        //更新到数据缓冲区
        mBytes = model;
        //重绘
        invalidate();
    }

    /**
     * 更新数据显示
     * @param wave
     */
    public void updateVisualizer(short[] wave) {

        Drawtype = typeWave;

        int width = getWidth();
        int height = getHeight()/2;
        byte[] model = new byte[width];

        int waveindex = 0;
        for (int i = 0;i<width; i++) {
            //计算功率谱
            model[i] = (byte) (wave[waveindex]/16392.0*height);
            waveindex++;
            if (waveindex>=wave.length){
                waveindex = 0;
            }
        }
        //更新到数据缓冲区
        mBytes = model;
        //重绘
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //检查
        if (mBytes == null || mBytes.length == 0) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length * 4) {
            mPoints = new float[mBytes.length * 4];
        }
        //
        //Log.d("BaseVisuallizerView","mPoints:"+mPoints.length + "\t " + "mBytes:" + mBytes.length);
        switch (Drawtype) {
            //显示FFT
            case typeFFT:


            //间隔平分
            final float baseX = getWidth() / mSpectrumNum;
            //高度
            final int height = getHeight();
            mForePaint.setStrokeWidth(baseX*3/4);
            for (int i = 0; i < mSpectrumNum; i++) {
                if (mBytes[i] < 0) {
                    mBytes[i] = 127;
                }

                final int xi = (int)(getWidth() * i / mSpectrumNum  + getWidth() / mSpectrumNum / 2);

                mPoints[i * 4] = xi;
                mPoints[i * 4 + 1] = height;

                mPoints[i * 4 + 2] = xi;
                mPoints[i * 4 + 3] = height - (height / 127.0f) * mBytes[i];
            }break;
            //显示波形
            case typeWave:
                for (int i = 0; i < getWidth(); i++) {

                    mPoints[i * 4] = i;
                    mPoints[i * 4 + 1] = getHeight()/2;

                    mPoints[i * 4 + 2] = i;
                    mPoints[i * 4 + 3] = getHeight()/2 + mBytes[i];
                }
                break;
        }
        canvas.drawLines(mPoints, mForePaint);
    }
}
