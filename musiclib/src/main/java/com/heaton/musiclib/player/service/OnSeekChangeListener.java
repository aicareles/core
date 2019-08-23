package com.heaton.musiclib.player.service;


public interface OnSeekChangeListener {

	void onSeekChange(int progress, int max, String time, String duration);
}
