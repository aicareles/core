package com.heaton.baselibsample.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselibsample.adapter.LocalMusicAdapter;
import com.heaton.baselibsample.R;
import com.heaton.musiclib.FftConvertUtils;
import com.heaton.musiclib.MusicManager;
import com.heaton.musiclib.player.MediaPlayer;
import com.heaton.musiclib.player.callback.MusicScanCallback;
import com.heaton.musiclib.player.callback.PlayStateCallback;
import com.heaton.musiclib.player.callback.RecordDataCallBack;
import com.heaton.musiclib.player.callback.ServiceConnectedCallback;
import com.heaton.musiclib.player.constant.PlayerFinal;
import com.heaton.musiclib.vo.MusicVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * description $desc$
 * created by jerry on 2019/7/25.
 */
public class MusicFragment extends BaseFragment implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = "MusicFragment";
    private static final int REQUEST_READ_PERMISSIONS = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSIONS = 2;
    @BindView(R.id.lv_local)
    ListView lvLocal;
    @BindView(R.id.iv_pre)
    ImageView ivPre;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_artist)
    TextView tvArtist;
    @BindView(R.id.sb)
    SeekBar sb;
    @BindView(R.id.tv_sb_time)
    TextView tvSbTime;
    @BindView(R.id.tv_sb_duration)
    TextView tvSbDuration;
    @BindView(R.id.btn_record)
    Button btnRecord;
    private MusicManager mMusicManager;
    private ArrayList<MusicVO> mMusicList;
    private LocalMusicAdapter mLocalMusicAdapter;

    public static MusicFragment newInstance() {
        Bundle args = new Bundle();
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_music;
    }

    @Override
    protected void bindData() {
        mMusicList = new ArrayList<>();
        mLocalMusicAdapter = new LocalMusicAdapter(getContext(), mMusicList);
        lvLocal.setAdapter(mLocalMusicAdapter);
        mMusicManager = MusicManager.getInstance();
        mMusicManager.init(getContext());
        requestReadPermissions();
    }

    @Override
    protected void bindListener() {
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mMusicManager.changeSeek(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        /**
         * 音乐服务开启成功回调(需要时可以设置)
         */
        mMusicManager.setServiceConnectedCallback(new ServiceConnectedCallback() {
            @Override
            public void onServiceConnected() {
            }
        });

        /**
         * 音乐播放回调
         */
        mMusicManager.setPlayStateCallback(new PlayStateCallback() {

            @Override
            public void onStateChange(int state, int mode, int position) {
                if (mMusicList.size() > 0){
                    MusicVO musicVO = mMusicList.get(position);
                    tvTitle.setText(musicVO.title);
                    if (musicVO.artist.equals("<unknown>")) {
                        tvArtist.setText(R.string.unknown);
                    } else {
                        tvArtist.setText(musicVO.artist);
                    }
                    mLocalMusicAdapter.setPlayPosition(position);
                    lvLocal.smoothScrollToPosition(mLocalMusicAdapter.getPlayPosition());
                    mLocalMusicAdapter.notifyDataSetChanged();
                }
                switch (state) {
                    case PlayerFinal.STATE_PLAY:
                        ivPlay.setImageResource(R.mipmap.pause);
                        break;
                    case PlayerFinal.STATE_CONTINUE:
                        ivPlay.setImageResource(R.mipmap.pause);
                        break;
                    case PlayerFinal.STATE_PAUSE:
                        ivPlay.setImageResource(R.mipmap.play);
                        break;
                    case PlayerFinal.STATE_STOP:
                        ivPlay.setImageResource(R.mipmap.play);
                        break;
                }
            }

            @Override
            public void onSeekChange(int progress, int max, String time, String duration) {
                if (max <= 0)return;
                sb.setProgress(progress*100/max);
                tvSbTime.setText(time);
                long l = max - progress;
                int musicTime = (int) (l / 1000);
                String fen = musicTime / 60 + "";
                String miao = musicTime % 60 + "";
                if (miao.length() == 1) {
                    miao = "0" + musicTime % 60;
                }
                tvSbDuration.setText(fen + ":" + miao);
            }

            @Override
            public void onModeChange(int mode) {

            }
        });

        /**
         * 音乐律动数据回调
         */
        mMusicManager.setDataCaptureCallBack(new MediaPlayer.OnDataCaptureListener() {
            @Override
            public void onWaveDataCapture(short[] wave, int samplingRate) {
                if (mMusicManager.isPlaying()){
                    if (wave.length > 128){
                        short level = FftConvertUtils.getInstance().getLevelByWaveData(wave);
                        Log.e(TAG, "onWaveDataCapture: level>>>>" + level);
                    }
                }
            }
        });

        /**
         * 麦克风律动回调数据
         */
        mMusicManager.setRecordDataCallBack(new RecordDataCallBack() {
            @Override
            public void onRecordData(short[] buffer) {
                short level = FftConvertUtils.getInstance().getLevelByRecordDate(buffer);
                Log.e(TAG, "onRecordData: >>>>" + level);
            }
        });

    }

    @OnClick({R.id.iv_pre, R.id.iv_play, R.id.iv_next, R.id.btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_pre:
                mMusicManager.pre();
                break;
            case R.id.iv_play:
                mMusicManager.playOrPause();
                break;
            case R.id.iv_next:
                mMusicManager.next();
                break;
            case R.id.btn_record:
                requestRecordPermissions();
                break;
        }
    }

    @OnItemClick(R.id.lv_local)
    public void onItemClick(int position) {
        mMusicManager.playItem(mLocalMusicAdapter.getItem(position));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMusicManager.destory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsGranted: ");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "onPermissionsDenied: ");
    }

    @AfterPermissionGranted(REQUEST_READ_PERMISSIONS)
    private void requestReadPermissions() {
        String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            mMusicManager.startScanMusic(new MusicScanCallback() {
                @Override
                public void onMusicScanResult(List<MusicVO> musicList) {
                    mMusicList.addAll(musicList);
                    mLocalMusicAdapter.notifyDataSetChanged();
                    MusicVO musicVO = mLocalMusicAdapter.getItem(0);
                    if (musicVO != null){
                        mLocalMusicAdapter.setPlayPosition(0);
                        mLocalMusicAdapter.notifyDataSetChanged();
                        tvTitle.setText(musicVO.title);
                        long l = musicVO.duration;
                        int musicTime = (int) (l / 1000);
                        String fen = musicTime / 60 + "";
                        String miao = musicTime % 60 + "";
                        if (miao.length() == 1) {
                            miao = "0" + musicTime % 60;
                        }
                        tvSbDuration.setText(fen + ":" + miao);
                        if (musicVO.artist.equals("<unknown>")) {
                            tvArtist.setText(R.string.unknown);
                        } else {
                            tvArtist.setText(musicVO.artist);
                        }
                    }
                }
            });
        } else {
            EasyPermissions.requestPermissions(this, "音乐加载需要读取SD卡权限", REQUEST_READ_PERMISSIONS, perms);
        }
    }

    @AfterPermissionGranted(REQUEST_RECORD_AUDIO_PERMISSIONS)
    private void requestRecordPermissions() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            mMusicManager.startRecord();
        } else {
            EasyPermissions.requestPermissions(this, "获取麦克风数据需要录制权限", REQUEST_RECORD_AUDIO_PERMISSIONS, perms);
        }
    }

    @Override
    public boolean onBackPressed() {
        FragmentHold.showFragment(getFragmentManager(), HomeFragment.newInstance());
        return true;//处理完返回true表示该返回事件Fragment处理掉了
    }

}
