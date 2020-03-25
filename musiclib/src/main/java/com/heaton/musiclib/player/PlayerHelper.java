package com.heaton.musiclib.player;

import java.io.IOException;
import java.util.Random;

import android.media.AudioManager;

import com.heaton.musiclib.player.callback.OnCompletionListener;
import com.heaton.musiclib.player.constant.PlayerFinal;
import com.heaton.musiclib.player.service.PlayerService;

/**
 * 播放歌曲帮助类
 * 
 * @author Wangyan
 * 
 */
public class PlayerHelper {

    private static final String TAG = "PlayerHelper";
    /**
	 * 单例模式，让MediaPlayer对象只声明一次，多次调用。
	 */
	private static MediaPlayerCompat player;
	private boolean mute;
	private float volume;
	private PlayerService mPlayerService;

	public PlayerHelper(PlayerService playerService){
		mPlayerService = playerService;
		if (player == null) {
			player = new MediaPlayerCompat();
		}
	}

	/**
	 * 获取当前Media player的实例
	 * @return
     */
	public static MediaPlayerCompat getPlayer() {
		if (player == null) {
			player = new MediaPlayerCompat();
		}
		return player;
	}

	public void mute(float volume){
		mute = !mute;
		if(mute){
			this.volume = volume;
			player.setVolume(0,0);
		}else{
			player.setVolume(this.volume,this.volume);
		}
	}


	/**
	 * 播放函数
	 */
	public boolean play(final String path) {
		try {
			player.play(path);
			player.start();
			//设置播放完成之后的回调函数接口
			player.setOnCompletionListener(new OnCompletionListener()
			{
				@Override
				public void onCompletion(Object mediaPlay) {
					// 歌曲播放完毕，根据播放模式选择下一首播放歌曲的position
					// 播放模式在service中存放
					// 歌曲播放列表和位置都在service中，在这直接更改service中的position和state
					switch (mPlayerService.playMode)
					{
					// 单曲循环
					case PlayerFinal.MODE_SINGLE:
						player.setLooping(true);
						mPlayerService.state = PlayerFinal.STATE_PLAY;
						break;
					// 全部循环
					case PlayerFinal.MODE_LOOP:
						if (mPlayerService.servicePosition == mPlayerService.serviceMusicList.size() - 1) {
							mPlayerService.servicePosition = 0;
						} else {
							mPlayerService.servicePosition++;
						}
						mPlayerService.state = PlayerFinal.STATE_PLAY;
						break;
					// 随机播放
					case PlayerFinal.MODE_RANDOM:
						Random random = new Random();
						int p = mPlayerService.servicePosition;
						while (true) {
							mPlayerService.servicePosition = random.nextInt(mPlayerService.serviceMusicList.size());
							if (p != mPlayerService.servicePosition) {
								mPlayerService.state = PlayerFinal.STATE_PLAY;
								break;
							}
						}
						break;
					// 顺序播放
					/*case PlayerFinal.MODE_ORDER:
						if (PlayerService.servicePosition == PlayerService.serviceMusicList.size() - 1) {
							PlayerService.state = PlayerFinal.STATE_STOP;
						} else {
							PlayerService.servicePosition++;
							PlayerService.state = PlayerFinal.STATE_PLAY;
						}
						break;*/
					}
					mPlayerService.stateChange = true;
				}
			});
			return true;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 暂停函数
	 */
	public void pause() {
		player.pause();
	}

	/**
	 * 歌曲继续播放
	 */
	public void continuePlay() {
        player.start();// 歌曲继续播放
	}

	/**
	 * 歌曲停止
	 */
	public void stop() {
		if(isPlaying())
		player.stop();// 歌曲停止
		player = null;//销毁音乐线程
	}

	/**
	 * 得到歌曲当前播放位置
	 * 
	 * @return int 歌曲时长
	 */
	public int getPlayCurrentTime() {
		return player.getPlayCurrentTime();
	}

	/**
	 * 得到歌曲时长
	 * 
	 * @return int 歌曲时长
	 */
	public int getPlayDuration() {
		return player.getPlayDuration();
	}

	/**
	 * 指定播放位置
	 */
	public void seekToMusic(final int seek) {
		if (player.getMediaPlayer() instanceof MediaPlayer){
			if(((MediaPlayer)player.getMediaPlayer()).getPlayState() == PlayerStates.STOPPED){
				//第一次未初始化
				mPlayerService.state = PlayerFinal.STATE_PLAY;
				return;
			}
		}
  		player.seekTo(seek);
	}

	/**
	 * 判断当前是否在播放
	 * 
	 * @return
	 */
	public Boolean isPlaying() {
		return player.isPlaying();
	}

}
