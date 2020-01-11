package com.heaton.musiclib.player;

import java.io.IOException;

public interface IPlayer<T> {

    void setVolume(float leftVolume, float rightVolume);

    void start();

    void stop();

    void pause();

    void seekTo(int seek);

    int getPlayCurrentTime();

    int getPlayDuration();

    boolean isPlaying();

    void play(String path) throws IOException;

    void setLooping(boolean looping);

    void setMediaPlayerType(MediaPlayerCompat.PlayerType type);

}
