package com.heaton.musiclib.player;

import java.util.Random;

import android.media.AudioManager;
import android.util.Log;

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
	public static MediaPlayer myMedia = getMyMedia();
	private boolean mute;
	private float volume;
	private PlayerService mPlayerService;

	public PlayerHelper(PlayerService playerService){
		mPlayerService = playerService;
		if (myMedia == null) {
			myMedia = new MediaPlayer();
		}
	}

	/**
	 * 获取当前Media player的实例
	 * @return
     */
	public static MediaPlayer getMyMedia() {
		if (myMedia == null) {
			myMedia = new MediaPlayer();
		}
		return myMedia;
	}

	public void mute(float volume){
		mute = !mute;
		if(mute){
			this.volume = volume;
			myMedia.setVolume(0,0);
		}else{
			myMedia.setVolume(this.volume,this.volume);
		}
	}


	/**
	 * 播放函数
	 */
	public boolean play(final String path) {
		try {
			myMedia.reset();
			myMedia.setAudioStreamType(AudioManager.STREAM_MUSIC);
			myMedia.setDataSource(path);
			myMedia.prepare();
			myMedia.start();
			//设置播放完成之后的回调函数接口
			myMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{
				@Override
				public void onCompletion(MediaPlayer mp) {
					// 歌曲播放完毕，根据播放模式选择下一首播放歌曲的position
					// 播放模式在service中存放
					// 歌曲播放列表和位置都在service中，在这直接更改service中的position和state
					switch (mPlayerService.playMode)
					{
					// 单曲循环
					case PlayerFinal.MODE_SINGLE:
						myMedia.setLooping(true);
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
		}
		return false;
	}

	/**
	 * 暂停函数
	 */
	public void pause() {
		myMedia.pause();
	}

	/**
	 * 歌曲继续播放
	 */
	public void continuePlay() {
        myMedia.start();// 歌曲继续播放
	}

	/**
	 * 歌曲停止
	 */
	public void stop() {
//		if(isPlaying())
		myMedia.stop();// 歌曲停止
		myMedia = null;//销毁音乐线程
	}

	/**
	 * 得到歌曲当前播放位置
	 * 
	 * @return int 歌曲时长
	 */
	public int getPlayCurrentTime() {
		return myMedia.getCurrentPosition();
	}

	/**
	 * 得到歌曲时长
	 * 
	 * @return int 歌曲时长
	 */
	public int getPlayDuration() {
		return myMedia.getDuration();
	}

	/**
	 * 指定播放位置
	 */
	public void seekToMusic(final int seek) {
        if(myMedia.getPlayState() == PlayerStates.STOPPED){
            //第一次未初始化
            mPlayerService.state = PlayerFinal.STATE_PLAY;
        }else {
            myMedia.seekTo(seek);// 指定位置
        }
//		myMedia.start();// 开始播放
	}

	/**
	 * 判断当前是否在播放
	 * 
	 * @return
	 */
	public Boolean isPlaying() {
		return myMedia.isPlaying();
	}

}
