package com.heaton.musiclib.player.recoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.util.Log;

import com.heaton.musiclib.player.AudioFx.BaseVisualizerView;


public class SoundRecordHelper {
	//获取录音的采样率
	private int recSampleRate = 16000;
	//单通道
	private int recChannel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	//16bit PCM
	private int recAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
	//录音源头为MIC
	private int audioSource = MediaRecorder.AudioSource.MIC;
	//正在录音标志
	private boolean recordFlag = false;
	//录音
	AudioRecord audioRecord = null;
	//录音线程
	RecordThread recThread = null;

	SoundRecoderHelperCallbackData callbackData = null;
	//layout
	private Visualizer mVisualizer;
	private BaseVisualizerView mVisualizerView;
	//获取最小的Buffer尺寸
	public int minRecBufSize = AudioRecord.getMinBufferSize(recSampleRate, recChannel, recAudioFormat);

	//检查是否正在录音
	public boolean isRecording(){
		return recordFlag;
	}

	public int getAudioSessionid()
	{
		return audioRecord.getAudioSessionId();
	}
	//构造器
	public SoundRecordHelper(){
//		audioRecord = new AudioRecord(audioSource, recSampleRate, recChannel, recAudioFormat, minRecBufSize);
	}

	public SoundRecordHelper(int sampleRate,int channel,int format){
		recSampleRate = sampleRate;
		recChannel = channel;
		recAudioFormat = format;
		minRecBufSize = AudioRecord.getMinBufferSize(recSampleRate, recChannel, recAudioFormat);
		audioRecord = new AudioRecord(audioSource, recSampleRate, recChannel, recAudioFormat, minRecBufSize);
	}

	//启动录音
	public void start(){
		prepare();
		audioRecord.startRecording();
		recThread = new RecordThread(audioRecord, minRecBufSize);
		recordFlag = true;
		recThread.start();
	}

	private void prepare() {
		if (audioRecord == null){
			audioRecord = new AudioRecord(audioSource, recSampleRate, recChannel, recAudioFormat, minRecBufSize);
		}
	}

	//停止录音
	public void stop(){
		if (audioRecord != null){
			audioRecord.stop();
		}
		recordFlag = false;
		recThread = null;
	}

	public interface SoundRecoderHelperCallbackData {
		 void reportdata(short[] buffer);
	}

	public void setDatareportCallBack(SoundRecoderHelperCallbackData  callback) {
		callbackData= callback;
	}

	//录音线程
	public class RecordThread extends Thread
	{
		private AudioRecord ar;
		private int bufSize;
		
		public RecordThread(AudioRecord audioRecord, int bufferSize){
			this.ar = audioRecord;
			this.bufSize = bufferSize;
		}
		public void run()
		{
			try{ 
				short[] buffer = new short[bufSize];

				while(recordFlag)
				{
					int ret = ar.read(buffer, 0, bufSize);
					//录音错误
					if(ret == AudioRecord.ERROR_BAD_VALUE){
						recordFlag = false;
					}
					else{
						callbackData.reportdata(buffer);
					}
					
				}
				ar.stop();
			}
			//捕捉异常
			catch (Exception e) {
				Log.e("Receive message E",e.toString());
			}
		}
	}
}