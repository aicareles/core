// IPlayerCallback.aidl
package com.heaton.musiclib;

// Declare any non-default types here with import statements

interface IPlayerCallback {
    void onStateChange(int state, int mode, int position);
    void onSeekChange(int progress, int max, String time, String duration);
    void onModeChange(int mode);
}
