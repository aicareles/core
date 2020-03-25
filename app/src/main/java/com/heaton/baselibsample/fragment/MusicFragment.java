package com.heaton.baselibsample.fragment;

import android.Manifest;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.heaton.baselib.base.BaseActivity;
import com.heaton.baselib.base.BaseFragment;
import com.heaton.baselibsample.adapter.LocalMusicAdapter;
import com.heaton.baselibsample.R;
import com.heaton.musiclib.FftConvertUtils;
import com.heaton.musiclib.MusicManager;
import com.heaton.musiclib.player.MediaPlayerCompat;
import com.heaton.musiclib.player.callback.MusicScanCallback;
import com.heaton.musiclib.player.callback.OnDataCaptureListener;
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
import cn.com.superLei.aoparms.annotation.Permission;
import cn.com.superLei.aoparms.annotation.PermissionDenied;
import cn.com.superLei.aoparms.annotation.PermissionNoAskDenied;
import cn.com.superLei.aoparms.common.permission.AopPermissionUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * description $desc$
 * created by jerry on 2019/7/25.
 */
public class MusicFragment extends BaseFragment {
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
    @BindView(R.id.btn_rhythm)
    Button btnRhythm;
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
        //需要audio权限
        mMusicManager.setMediaPlayerType(MediaPlayerCompat.PlayerType.CUSTOM_PLAYER);
        requestReadPermissions();
    }

    @Override
    protected void bindListener() {
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    Log.e(TAG, "onProgressChanged: "+progress);
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
                Log.e(TAG, "onStateChange: "+state);
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
                    case PlayerFinal.STATE_CONTINUE:
                        ivPlay.setImageResource(R.mipmap.pause);
                        break;
                    case PlayerFinal.STATE_PAUSE:
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
         * 音乐律动回调
         */
        mMusicManager.setDataCaptureCallback(new OnDataCaptureListener() {
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

    @OnClick({R.id.iv_pre, R.id.iv_play, R.id.iv_next, R.id.btn_record, R.id.btn_rhythm})
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
            case R.id.btn_rhythm:
                if (mMusicManager.isMusicRhythming()){
                    btnRhythm.setText("开启律动");
                    stopRhythm();
                }else {
                    btnRhythm.setText("停止律动");
                    startRhythm();
                }
                break;
        }
    }

    private void startRhythm(){
        mMusicManager.startRhythm();
    }

    private void stopRhythm(){
        mMusicManager.stopRhythm();
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

//    @Permission(value = {Manifest.permission.READ_EXTERNAL_STORAGE}, rationale = "音乐加载需要读取SD卡权限", requestCode = REQUEST_READ_PERMISSIONS)
    private void requestReadPermissions() {
        ((BaseActivity)mActivity)
                .requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        "音乐加载需要读取SD卡权限", new BaseActivity.GrantedResult() {
                            @Override
                            public void onResult(boolean granted) {
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
                            }
                        });
    }

//    @Permission(value = {Manifest.permission.RECORD_AUDIO}, rationale = "获取麦克风数据需要录制权限", requestCode = REQUEST_RECORD_AUDIO_PERMISSIONS)
    private void requestRecordPermissions(){
        ((BaseActivity)mActivity).requestPermission(new String[]{Manifest.permission.RECORD_AUDIO}, "", new BaseActivity.GrantedResult() {
            @Override
            public void onResult(boolean granted) {
                if (granted){
                    mMusicManager.startRecord();
                }
            }
        });

    }

    @PermissionDenied
    public void permissionDenied(int requestCode, List<String> denyList) {
        if (requestCode == REQUEST_READ_PERMISSIONS) {
            Log.e(TAG, "permissionDenied>>>:读取权限被拒 " + denyList.toString());
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSIONS) {
            Log.e(TAG, "permissionDenied>>>:麦克风权限被拒 " + denyList.toString());
        }
    }

    @PermissionNoAskDenied
    public void permissionNoAskDenied(int requestCode, List<String> denyNoAskList) {
        if (requestCode == REQUEST_READ_PERMISSIONS) {
            Log.e(TAG, "permissionNoAskDenied 读取权限被拒,不在提示>>>: " + denyNoAskList.toString());
        } else if (requestCode == REQUEST_RECORD_AUDIO_PERMISSIONS) {
            Log.e(TAG, "permissionDenied>>>:麦克风权限被拒,不在提示>>> " + denyNoAskList.toString());
        }
        AopPermissionUtils.showGoSetting(getActivity(), "为了更好的体验，建议前往设置页面打开权限");
    }

}
