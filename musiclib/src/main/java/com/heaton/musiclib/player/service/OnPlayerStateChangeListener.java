package com.heaton.musiclib.player.service;

import com.heaton.musiclib.vo.MusicVO;

import java.util.List;

public interface OnPlayerStateChangeListener {
	void onStateChange(int state, int mode, List<MusicVO> musicList, int position);
}
