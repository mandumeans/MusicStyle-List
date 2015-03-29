package com.funny.developers.musicstylelist.player;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.view.SurfaceHolder;

import com.funny.developers.musicstylelist.asynctask.CheckYoutubeVideoUrl;
import com.funny.developers.musicstylelist.asynctask.CheckYoutubeVideoUrl.OnCheckYoutubeUrlRequestCallback;
import com.funny.developers.musicstylelist.asynctask.GetYoutubeVideoUrl;
import com.funny.developers.musicstylelist.asynctask.GetYoutubeVideoUrl.OnYoutubeRTSPRequestCallBack;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.notification.MediaNotification;
import com.funny.developers.musicstylelist.util.SettingUtils;

public class MusicStyleListMediaPlayer extends Service implements OnPreparedListener, OnBufferingUpdateListener, 
																OnCompletionListener, OnErrorListener,
																OnYoutubeRTSPRequestCallBack, OnCheckYoutubeUrlRequestCallback {

	private static ArrayList<BaseModel> trackList;
	private static int playTrackNum;

	private boolean resetCheck = false;
	private int repeatType;
	private int shuffleType;
	private int buffering;

	private String mConnectType;

	private SearchTrackListModel trackModel;

	private MediaPlayer mediaPlayer;

	private GetYoutubeVideoUrl getYoutubeBetterUrl;
	private GetYoutubeVideoUrl getYoutubeRTSPUrl;
	private CheckYoutubeVideoUrl checkYoutubeUrl;

	private Random random;

	private WifiLock wifiLock;
	private MediaNotification mediaNotification;

	//****************Setting Binder****************
	private final IBinder mBinder = new LocalBinder();  

	public class LocalBinder extends Binder {
		public MusicStyleListMediaPlayer getService(){
			return MusicStyleListMediaPlayer.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);

		if(intent != null){
			if(intent.getAction() != null){
				if(intent.getAction().equals(MediaPlayerFunctionDefine.NOTIFICATION_CLOSE_INTENT_ID)){
					stopForeground(true);
					stopSelf();
				}
			}
		}
		return START_NOT_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent){
		super.onUnbind(intent);
		return true;
	}

	//****************Setting Service****************

	@Override
	public void onCreate(){
		super.onCreate();

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		
		random = new Random();

		repeatType = SettingUtils.getMediaplayerRepeatType(getApplicationContext());
		shuffleType = SettingUtils.getMediaplayerShuffleType(getApplicationContext());

		mediaNotification = new MediaNotification(getApplicationContext());
	}

	@Override
	public void onDestroy() {

		sendEmptyMediaPlayerMesseage();

		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		
		if(wifiLock != null){
			wifiLock.release();
		}
		
		mErrorHandler.removeMessages(Define.HANDLER_WHAT);

		trackList = null;
		playTrackNum = 0;
	}
	
	public void setWifiLock(){
		if(SettingUtils.getWiFilock(getApplicationContext())){
			if(wifiLock == null){
				wifiLock = ((WifiManager) getSystemService(Context.WIFI_SERVICE))
						.createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
				wifiLock.acquire();
			}
		} else {
			if(wifiLock != null){
				wifiLock.release();
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mErrorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(isMediaPlayerHasTrackList()){
				if(!mediaPlayer.isPlaying()){
					playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
				}
			}
		}
	};
	
	private void runErrorHandler(){
		if(mErrorHandler.hasMessages(Define.HANDLER_WHAT)){
			mErrorHandler.removeMessages(Define.HANDLER_WHAT);
		}
		mErrorHandler.sendEmptyMessageDelayed(Define.HANDLER_WHAT, Define.HANDLER_MEDIAPLAYER_ERROR_DELAY_TIME);
	}

	//***************** Play Function *****************
	public void playTracks(int pos){
		setWifiLock();
		
		mediaPlayerReset();
		
		if((shuffleType == MediaPlayerFunctionDefine.SHUFFLE_ON) && (trackList.size() != 1) && (pos != MediaPlayerFunctionDefine.TRACK_SELF)) {
			playTrackNum = random.nextInt(trackList.size());
		}

		setPlayTrackNum(pos);
		trackModel = (SearchTrackListModel)trackList.get(playTrackNum);

		sendLoadingMesseage();

		startForeground(MediaPlayerFunctionDefine.NOTIFICATION_ID,
				mediaNotification.getCustomNotification(trackModel.title, trackModel.uploader).build());

		switch(trackModel.trackType){
		case Define.YOUTUBE_TRACK:

			if(trackModel.onlyYoutube != Define.ONLY_YOUTUBE_TYPE){
				getYoutubeBetterUrl();
			} else {
				playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
			}

			break;

		case Define.SOUNDCLOUD_TRACK:
			playSoundCloud(trackModel);
			break;
		}
	}

	private void setPlayTrackNum(int pos){
		switch(pos){
		case MediaPlayerFunctionDefine.TRACK_NEXT:
			playTrackNum++;
			if(trackList.size()-1 < playTrackNum){
				playTrackNum = 0;
			}
			break;

		case MediaPlayerFunctionDefine.TRACK_SELF:
			break;

		case MediaPlayerFunctionDefine.TRACK_PREV:
			playTrackNum--;
			if(playTrackNum < 0){
				playTrackNum = trackList.size() - 1;
			}
			break;
		}
	}

	//***************** Media Player View Func *****************
	public static void setPlayTrackList(ArrayList<BaseModel> instanceTrackList, int instancePlayTrackNum){
		trackList = instanceTrackList;
		playTrackNum = instancePlayTrackNum;
	}

	public static boolean isMediaPlayerHasTrackList(){
		if(trackList != null){
			return true;
		} else {
			return false;
		}
	}

	public SearchTrackListModel getTrackInPlay() {
		SearchTrackListModel playTrackModel = (SearchTrackListModel)trackList.get(playTrackNum);
		return playTrackModel;
	}

	public void setMediaPlayerDisplay(SurfaceHolder surfaceHolder){
		mediaPlayer.setDisplay(surfaceHolder);
	}

	//***************** Media Player Interface *****************
	@Override
	public void onPrepared(MediaPlayer mp) {
		if(mp.getDuration() == 0){
			runErrorHandler();
			return;
		}
		
		start();
		sendPlayMesseage();
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		buffering = percent;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if(trackList == null){
			return;
		}

		switch(repeatType) {
		case MediaPlayerFunctionDefine.REPEAT_NO:
			break;

		case MediaPlayerFunctionDefine.REPEAT_ONE:
			if(trackModel.trackType == Define.YOUTUBE_TRACK){
				if(mConnectType == Define.YOUTUBE_RTSP_URL){
					playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
				} else {
					mediaPlayer.seekTo(0);
					mediaPlayer.start();
				}
			} else {
				mediaPlayer.seekTo(0);
				mediaPlayer.start();
			}
			break;

		case MediaPlayerFunctionDefine.REPEAT_ALL:
			playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
			break;
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		runErrorHandler();
		return true;
	}

	public void mediaPlayerReset() {
		if(resetCheck){
			mediaPlayer.stop();
			mediaPlayer.reset();
		}
	}

	//***************** getYoutubeUrl Interface *****************

	public void getYoutubeBetterUrl(){
		if(getYoutubeBetterUrl != null){
			getYoutubeBetterUrl.cancel(true);
		}

		getYoutubeBetterUrl = new GetYoutubeVideoUrl();
		getYoutubeBetterUrl.setOnRequestCallBack(this);
		getYoutubeBetterUrl.execute(trackModel.id,"18", Define.YOUTUBE_NOT_RTSP_URL);
	}

	public void getYoutubeRtspUrl(){
		if(getYoutubeRTSPUrl != null){
			getYoutubeRTSPUrl.cancel(true);
		}

		getYoutubeRTSPUrl = new GetYoutubeVideoUrl();
		getYoutubeRTSPUrl.setOnRequestCallBack(this);
		getYoutubeRTSPUrl.execute(trackModel.id, null, Define.YOUTUBE_RTSP_URL);
	}

	public void checkYoutubeUrl(String youtubeUrl){
		if(checkYoutubeUrl != null){
			checkYoutubeUrl.cancel(true);
		}

		checkYoutubeUrl = new CheckYoutubeVideoUrl();
		checkYoutubeUrl.setOnRequestCallBack(this);
		checkYoutubeUrl.execute(youtubeUrl);
	}

	public void prepareYoutube(String youtubeUrl){
		try {
			mediaPlayer.setDataSource(URLDecoder.decode(youtubeUrl, "UTF-8"));
			mediaPlayer.prepareAsync();
			
			if(!resetCheck){
				resetCheck = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getYoutubeUrl(String youtubeUrl, String connectType) {
		try {
			mConnectType = connectType;
			if(youtubeUrl == Define.YOUTUBE_URL_NOT_FIND){
				playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
			} else {
				if(connectType == Define.YOUTUBE_NOT_RTSP_URL){
					if(youtubeUrl == null){
						getYoutubeRtspUrl();
					} else {
						checkYoutubeUrl(URLDecoder.decode(youtubeUrl, "UTF-8"));
					}
				} else {
					prepareYoutube(youtubeUrl);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void checkYoutubeUrl(String youtubeUrl, boolean check) {
		if(check){
			prepareYoutube(youtubeUrl);
		} else {
			getYoutubeRtspUrl();
		}
	}

	//***************** SoundCloud Play function *****************
	public void playSoundCloud(SearchTrackListModel trackModel) {
		try{
			mediaPlayer.setDataSource(Define.SOUNDCLOUD_PLAY_URL + trackModel.id+"/stream?client_id="+Define.SOUND_CLOUD_API_KEY);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepareAsync();
			
			if(!resetCheck){
				resetCheck = true;
			}
		} catch(Exception e){

		}
	}

	//***************** Media Controller Interface *****************
	public void start() {
		if(mediaPlayer != null){
			mediaPlayer.start();
		}
	}

	public void pause() {
		if(mediaPlayer != null){
			mediaPlayer.pause();
		}
	}

	public int getDuration() {
		if(mediaPlayer != null){
			return mediaPlayer.getDuration();
		}

		return 0;
	}

	public int getBuffering(){
		return buffering;
	}

	public int getCurrentPosition() {
		if(mediaPlayer != null){
			return mediaPlayer.getCurrentPosition();
		}

		return 0;
	}

	public void seekTo(int pos) {
		if(mediaPlayer != null){
			mediaPlayer.seekTo(pos);
		}
	}

	public boolean isPlaying() {
		if(mediaPlayer != null){
			return mediaPlayer.isPlaying();
		}

		return false;
	}

	public void next() {
		playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
	}

	public void prev() {
		playTracks(MediaPlayerFunctionDefine.TRACK_PREV);
	}

	public void shuffle(int shuffleType) {
		this.shuffleType = shuffleType;
	}

	public void repeat(int repeatType) {
		this.repeatType = repeatType;
	}

	//********************** send messeage ****************************

	public void sendReadyMesseage(){
		getApplicationContext().getContentResolver().notifyChange(NotifyUrlDefine.URI_READY_MEDIAPLAYER, null);
	}

	public void sendPlayMesseage(){
		getApplicationContext().getContentResolver().notifyChange(NotifyUrlDefine.URI_PLAY_MEDIAPLAYER, null);
	}

	public void sendLoadingMesseage(){
		getApplicationContext().getContentResolver().notifyChange(NotifyUrlDefine.URI_LOAD_MEDIAPLAYER, null);
	}

	public void sendEmptyMediaPlayerMesseage(){
		getApplicationContext().getContentResolver().notifyChange(NotifyUrlDefine.URI_EMPTY_MEDIAPLAYER, null);
	}
}
