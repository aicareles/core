package com.heaton.musiclib.player.callback;

public interface OnDataCaptureListener {
    void onWaveDataCapture(short[] wave, int samplingRate);
}
