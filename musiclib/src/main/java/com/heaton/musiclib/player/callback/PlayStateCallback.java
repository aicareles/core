package com.heaton.musiclib.player.callback;

/**
 * description $desc$
 * created by jerry on 2019/7/25.
 */
public interface PlayStateCallback {

    /*void onPlayingState();
    void onContinueState();
    void onPauseState();
    void onStopState();*/
    void onStateChange(int state, int mode, int position);

    void onSeekChange(int progress, int max, String time, String duration);
    void onModeChange(int mode);
}
