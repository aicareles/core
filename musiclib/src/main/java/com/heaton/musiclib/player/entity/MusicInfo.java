package com.heaton.musiclib.player.entity;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfo implements Serializable, Parcelable {
	/**
	 * 歌曲实体类
	 */
	private static final long serialVersionUID = 1L;
	private int id;	//ID
	private String title;//标题
	private String artist;	//艺术家
	private String album;	//封面
	private String path;	//路径
	private Long duration;	//时长
	private Long size;	//长度
	private String album_img_path;//封面图像路径
	private String lyric_file_name;	//歌词文件名称
	//获取名称
	public String getTitle() {
		return title;
	}
	//设置名称
	public void setTitle(String title) {
		this.title = title;
	}
	//获取艺术家
	public String getArtist() {
		return artist;
	}
	//设置艺术家
	public void setArtist(String artist) {
		this.artist = artist;
	}
	//获取封面
	public String getAlbum() {
		return album;
	}
	//设置封面
	public void setAlbum(String album) {
		this.album = album;
	}
	//获取时长
	public Long getDuration() {
		return duration;
	}
	//设置时长
	public void setDuration(Long duration) {
		this.duration = duration;
	}
	//获取长度
	public Long getSize() {
		return size;
	}
	//设置长度
	public void setSize(Long size) {
		this.size = size;
	}
	//设置路径
	public void setPath(String path) {
		this.path = path;
	}
	//获取路径
	public String getPath() {
		return path;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	//序列化内容.用来保存数据
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.title);
		dest.writeString(this.artist);
		dest.writeString(this.album);
		dest.writeString(this.path);
		dest.writeLong(this.duration);
		dest.writeLong(this.size);
		dest.writeString(this.album_img_path);
		dest.writeString(this.lyric_file_name);
	}

	public void setAlbum_img_path(String album_img_path) {
		this.album_img_path = album_img_path;
	}

	public String getAlbum_img_path(){
			return album_img_path;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setLyric_file_name(String lyric_file_name) {
		this.lyric_file_name = lyric_file_name;
	}

	public String getLyric_file_name() {
		return lyric_file_name;
	}

	public static final Creator<MusicInfo> CREATOR = new Creator<MusicInfo>() {

		@Override
		public MusicInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			MusicInfo music = new MusicInfo();
			music.setTitle(source.readString());
			music.setArtist(source.readString());
			music.setAlbum(source.readString());
			music.setPath(source.readString());
			music.setDuration(source.readLong());
			music.setSize(source.readLong());
			music.setAlbum_img_path(source.readString());
			music.setLyric_file_name(source.readString());
			return music;
		}

		@Override
		public MusicInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new MusicInfo[size];
		}
	};

}