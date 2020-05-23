package com.heaton.musiclib.utils;


import csh.tiro.cc.fft.int16FFT;

/**
 * description $desc$
 * created by jerry on 2019/8/8.
 */
public class FftConvertUtils {
    private short maxvalue = 0;
    private float mMaxLevel;
    private float mMinLevel;
    private short[] pcmbuffer = null;

    private static class FftConvertHolder {
        private static FftConvertUtils fftConvertUtils = new FftConvertUtils();
    }


    public static FftConvertUtils getInstance() {
        return FftConvertHolder.fftConvertUtils;
    }

    private FftConvertUtils (){}


    /**
     * 获取音乐数据的律动数据
     * @param wave
     * @return
     */
    public short getLevelByWaveData(short[] wave){
        if (wave.length > 128) {
            final short Real[] = new short[128];
            final short Image[] = new short[128];
            System.arraycopy(wave, 0, Real, 0, 128);
            maxvalue = 0;
            for (int i = 0; i < Real.length / 2; i++) {
                if (Real[i] > maxvalue) {
                    maxvalue = Real[i];
                }
            }
            int16FFT.WindowCalc(Real, (char) 0);
            int16FFT.BitReverse(Real);
            int16FFT.IntFFT(Real, Image);
            return computeLevel(maxvalue, Short.MAX_VALUE);
        }
        return -1;
    }

    /**
     * 获取麦克风数据的律动数据
     * @param buffer
     * @return
     */
    public short getLevelByRecordDate(short[] buffer){
        if (pcmbuffer == null || pcmbuffer.length != buffer.length) {
            pcmbuffer = new short[buffer.length];
        }
        System.arraycopy(buffer, 0, pcmbuffer, 0, buffer.length);
        maxvalue = 0;
        for (int i = 0; i < pcmbuffer.length; i++) {
            if (pcmbuffer[i] > maxvalue) {
                maxvalue = pcmbuffer[i];
            }
        }
        //计算FFT
        if (pcmbuffer.length > 128) {
            short Real[] = new short[128];
            short Image[] = new short[128];
            System.arraycopy(pcmbuffer, 0, Real, 0, 128);

            int16FFT.WindowCalc(Real, (char) 0);
            int16FFT.BitReverse(Real);
            int16FFT.IntFFT(Real, Image);
        }

        return computeLevel(maxvalue, Short.MAX_VALUE);
    }

    private short computeLevel(short rawLevel, short maxLevel) {
        float level;
        if (mMaxLevel > 0) {
            mMaxLevel = mMaxLevel - mMaxLevel * 0.1f;
        }
        if (mMinLevel > 0) {
            mMinLevel = mMinLevel + mMinLevel * 0.08f;
        }
        if (mMaxLevel == 0 || mMaxLevel <= rawLevel) {
            mMaxLevel = rawLevel;
        }
        if (mMinLevel == 0 || mMinLevel > rawLevel) {
            mMinLevel = rawLevel;
        }
        if (mMaxLevel < maxLevel * 0.2f) {
            mMaxLevel = maxLevel * 0.2f;
        }
        if (mMinLevel > maxLevel * 0.85f) {
            mMinLevel = maxLevel * 0.85f;
        }
        /**
         * 10%修正
         */
        float fixLevel = rawLevel * 0.1f;
        if (mMaxLevel != 0) {
            level = ((rawLevel - mMinLevel) / (mMaxLevel - mMinLevel)) * (maxLevel - fixLevel) + fixLevel;
        } else {
            level = rawLevel / maxLevel;
        }
        short result = (short) ((level / maxLevel) * 0xff);
        if (result < 2) {
            result = 2;
        }
        return result;
    }
}
