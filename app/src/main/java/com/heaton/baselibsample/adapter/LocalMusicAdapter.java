package com.heaton.baselibsample.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.heaton.baselibsample.R;
import com.heaton.musiclib.vo.MusicVO;

import java.util.ArrayList;

/**
 * 本地音乐适配器
 */
public class LocalMusicAdapter extends BaseAdapter {
	// 上下文
	private Context context;
	// 正在播放的歌曲位置
	private int playPosition = -1;
	private ArrayList<MusicVO> mData;
	/**
	 * 构造方法
	 * @param context
	 * @param data
	 */
	public LocalMusicAdapter(Context context, ArrayList<MusicVO> data) {
		this.context = context;
		this.mData = data;
	}

	public void setPlayPosition(int playPosition) {
		this.playPosition = playPosition;
	}

	public int getPlayPosition() {
		return playPosition;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public MusicVO getItem(int position) {
		if (mData != null && position >= 0 && position < mData.size()) {
			return mData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_local_music, null);
			holder.title = (TextView) convertView
					.findViewById(R.id.tv_local_song_name);
			holder.artist = (TextView) convertView
					.findViewById(R.id.tv_local_artist);
			holder.indicator = (ImageView) convertView
					.findViewById(R.id.iv_local_indicator);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == playPosition) {
			holder.indicator.setVisibility(View.VISIBLE);
			holder.title.setTextColor(Color.parseColor("#2ba8ff"));
			holder.artist.setTextColor(Color.parseColor("#22639d"));
		} else {
			holder.indicator.setVisibility(View.GONE);
			holder.title.setTextColor(Color.parseColor("#2ba8ff"));
			holder.artist.setTextColor(Color.parseColor("#22639d"));
		}
		MusicVO musicVO = getItem(position);

		holder.title.setText(musicVO.title);
		if(musicVO.artist.equals("<unknown>")){
			holder.artist.setText("- " + context.getResources().getString(R.string.unknown));
		}else{
			holder.artist.setText("- "+musicVO.artist);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView title;
		TextView artist;
		ImageView indicator;
	}

}
