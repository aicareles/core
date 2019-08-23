// MusicPlayer.aidl
package com.heaton.musiclib;
import com.heaton.musiclib.vo.MusicVO;
import com.heaton.musiclib.IPlayerCallback;

// Declare any non-default types here with import statements

interface MusicPlayer {
    void playOrPause();
    void playItem(in MusicVO musicVO);
    void playPosition(int position);
    void dataChange(in List<MusicVO> musicList,int position,int mode);
    void registerCallback(IPlayerCallback callback);
    void unRegisterCallback(IPlayerCallback callback);
    void pause();
    void resume();
    void hold();
    void unHold();
    void mute();
    void next();
    void prev();
    void changeMode(int mode);
    void changeSeek(int seek);
    int getAudioSessionId();
}
