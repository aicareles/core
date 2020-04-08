package com.heaton.musiclib.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.io.Serializable;

/**
 * 音乐对象
 * Created by DDL on 2016/5/20.
 */
@DatabaseTable(tableName = "music")
public class MusicVO implements Serializable,Parcelable {
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField(canBeNull = false, unique = true)
	public String url;//音乐地址
	@DatabaseField(canBeNull = true)
	public String title;//音乐标题
	@DatabaseField(canBeNull = false, defaultValue = "0")
	public int    playCount;//播放次数
	@DatabaseField(canBeNull = false, defaultValue = "0")
	public long   addDate;//添加时间
	@DatabaseField(canBeNull = true, defaultValue = "0.00")
	public long  duration;//总时长
	@DatabaseField(canBeNull = true)
	public String artist;//艺术家
	@DatabaseField(canBeNull = true, defaultValue = "0")
	private int    sort;//自定义排序
	@DatabaseField(canBeNull = true, defaultValue = "")
	public String album;	//封面
	@DatabaseField(canBeNull = true, defaultValue = "0")
	public long fileSize;	//文件大小
	@DatabaseField(canBeNull = true, defaultValue = "0")
	public int internet;	//网络歌曲


	public boolean fileExists;//文件是否存在
	public boolean sortChanged;

	public void checkFile(){
		File file = new File(url);
		fileExists = !file.isDirectory() && file.exists() && file.canRead();
	}

	/**
	 * Describe the kinds of special objects contained in this Parcelable's
	 * marshalled representation.
	 *
	 * @return a bitmask indicating the set of special object types marshalled
	 * by the Parcelable.
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Flatten this object in to a Parcel.
	 *
	 * @param dest  The Parcel in which the object should be written.
	 * @param flags Additional flags about how the object should be written.
	 *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.url);
		dest.writeString(this.title);
		dest.writeInt(this.playCount);
		dest.writeLong(this.addDate);
		dest.writeLong(this.duration);
		dest.writeString(this.artist);
		dest.writeInt(this.sort);
		dest.writeString(this.album);
		dest.writeLong(this.fileSize);
		dest.writeInt(this.internet);
	}

	public static final Creator<MusicVO> CREATOR = new Creator<MusicVO>() {

		@Override
		public MusicVO createFromParcel(Parcel source) {
			MusicVO musicVO = new MusicVO();
			musicVO.id = source.readInt();
			musicVO.url = source.readString();
			musicVO.title = source.readString();
			musicVO.playCount = source.readInt();
			musicVO.addDate = source.readLong();
			musicVO.duration = source.readLong();
			musicVO.artist = source.readString();
			musicVO.sort = source.readInt();
			musicVO.album = source.readString();
			musicVO.fileSize = source.readLong();
			musicVO.internet = source.readInt();
			return musicVO;
		}

		@Override
		public MusicVO[] newArray(int size) {
			return new MusicVO[size];
		}
	};

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		if(this.sort == sort){
			return;
		}
		this.sort = sort;
		sortChanged = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPlayCount() {
		return playCount;
	}

	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}

	public long getAddDate() {
		return addDate;
	}

	public void setAddDate(long addDate) {
		this.addDate = addDate;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public boolean isFileExists() {
		return fileExists;
	}

	public void setFileExists(boolean fileExists) {
		this.fileExists = fileExists;
	}

	public boolean isSortChanged() {
		return sortChanged;
	}

	public void setSortChanged(boolean sortChanged) {
		this.sortChanged = sortChanged;
	}

	public int getInternet() {
		return internet;
	}

	public void setInternet(int internet) {
		this.internet = internet;
	}

	//	public static void main(String[] args) throws SQLException, IOException {
//		OrmLiteConfigUtil.writeConfigFile(new File("F:\\db_config.txt"), new Class[]{MusicVO.class});
//	}


	@Override
	public String toString() {
		return "MusicVO{" +
				"id=" + id +
				", url='" + url + '\'' +
				", title='" + title + '\'' +
				", playCount=" + playCount +
				", addDate=" + addDate +
				", duration=" + duration +
				", artist='" + artist + '\'' +
				", sort=" + sort +
				", album='" + album + '\'' +
				", fileSize=" + fileSize +
				", internet=" + internet +
				", fileExists=" + fileExists +
				", sortChanged=" + sortChanged +
				'}';
	}
}
