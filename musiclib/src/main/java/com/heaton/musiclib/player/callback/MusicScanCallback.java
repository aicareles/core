package com.heaton.musiclib.player.callback;

import com.heaton.musiclib.vo.MusicVO;

import java.util.List;

/**
 * description $desc$
 * created by jerry on 2019/7/25.
 */
public interface MusicScanCallback {
    void onMusicScanResult(List<MusicVO> musicList);
}
