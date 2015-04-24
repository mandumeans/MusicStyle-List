package com.funny.developers.musicstylelist.player;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
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
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.BackgroundPlayerListAdapter;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.notification.MediaNotification;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController.MediaPlayerControl;
import com.funny.developers.musicstylelist.util.SettingUtils;

public class MusicStyleListMediaPlayer extends Service implements OnPreparedListener, OnBufferingUpdateListener, 
OnCompletionListener, OnErrorListener, MediaPlayerControl{

	private View frameView;
	private TextView titleView;
	private TextView uploaderView;
	
	private ImageButton collapseButton;

	private ListView trackListView;

	private View lockView;
	private ProgressBar loadingBar;
	
	private MusicStyleListMediaPlayerController controller;

	private BackgroundPlayerListAdapter trackListAdapter;

	private LayoutInflater mInflater;

	private WindowManager.LayoutParams mParams;
	private WindowManager mWindowManager;

	private float START_X, START_Y;							//움직이기 위해 터치한 시작 점
	private int PREV_X, PREV_Y;								//움직이기 이전에 뷰가 위치한 점
	private int MAX_X = -1, MAX_Y = -1;					//뷰의 위치 최대 값

	private static ArrayList<BaseModel> trackList;
	private static int playTrackNum;

	private boolean resetCheck = false;
	private int repeatType;
	private int shuffleType;
	private int buffering;

	private SearchTrackListModel trackModel;

	private MediaPlayer mediaPlayer;

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
				if(intent.getAction().equals(MediaPlayerFunctionDefine.NOTIFICATION_TRACKLIST_INTENT_ID)){
					getExtras(intent);
				}
				
				if(intent.getAction().equals(MediaPlayerFunctionDefine.NOTIFICATION_SHOW_INTENT_ID)){
					if(frameView.isShown()){
						frameView.setVisibility(View.GONE);
					} else {
						frameView.setVisibility(View.VISIBLE);
					}
					
				}
				
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
	@SuppressWarnings("unchecked")
	private void getExtras(Intent intent){
		trackList = (ArrayList<BaseModel>)intent.getSerializableExtra(Define.PLAYER_TRACK_LIST_INTENT);

		trackListAdapter.setList(trackList);
		trackListView.setAdapter(trackListAdapter);

		if(trackList.size() > 0){
			playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
		}
	}

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

		createFrameView();
	}

	private void createFrameView(){
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		frameView = mInflater.inflate(R.layout.layout_for_background_player, null);
		frameView.setOnTouchListener(mViewTouchListener);
		
		collapseButton = (ImageButton) frameView.findViewById(R.id.collapse_button);
		collapseButton.setOnClickListener(mButtonClickListener);
		
		titleView = (TextView) frameView.findViewById(R.id.text_for_title);
		uploaderView = (TextView) frameView.findViewById(R.id.text_for_author);

		lockView = (View) frameView.findViewById(R.id.lock_layout);
		loadingBar = (ProgressBar) frameView.findViewById(R.id.loading_progressbar);
		
		controller = (MusicStyleListMediaPlayerController) frameView.findViewById(R.id.media_player_controller);
		controller.setMediaPlayer(this);

		trackListView = (ListView) frameView.findViewById(R.id.track_list_view);
		trackListView.setOnItemClickListener(mItemClickListener);
		
		trackListAdapter = new BackgroundPlayerListAdapter(getApplicationContext());

		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);  //윈도우 매니저

		DisplayMetrics displayMetrics = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
		int deviceWidth = displayMetrics.widthPixels;
		int deviceHeight =displayMetrics.heightPixels;

		mParams = new WindowManager.LayoutParams(
				deviceWidth / 2, deviceHeight / 2,
				WindowManager.LayoutParams.TYPE_PHONE,//항상 최 상위. 터치 이벤트 받을 수 있음.
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  //포커스를 가지지 않음
				PixelFormat.TRANSLUCENT);
		mParams.gravity = Gravity.LEFT | Gravity.TOP;

		mWindowManager.addView(frameView, mParams); //윈도우에 뷰 넣기. permission 필요.
	}

	/**
	 * 뷰의 위치가 화면 안에 있게 최대값을 설정한다
	 */
	private void setMaxPosition() {
		DisplayMetrics matrix = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(matrix);		//화면 정보를 가져와서

		MAX_X = matrix.widthPixels - frameView.getWidth();			//x 최대값 설정
		MAX_Y = matrix.heightPixels - frameView.getHeight();			//y 최대값 설정
	}

	/**
	 * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
	 */
	private void optimizePosition() {
		//최대값 넘어가지 않게 설정
		if(mParams.x > MAX_X) mParams.x = MAX_X;
		if(mParams.y > MAX_Y) mParams.y = MAX_Y;
		if(mParams.x < 0) mParams.x = 0;
		if(mParams.y < 0) mParams.y = 0;
	}

	private OnTouchListener mViewTouchListener = new OnTouchListener() {
		@Override public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:                //사용자 터치 다운이면
				if(MAX_X == -1)
					setMaxPosition();
				START_X = event.getRawX();                    //터치 시작 점
				START_Y = event.getRawY();                    //터치 시작 점
				PREV_X = mParams.x;                            //뷰의 시작 점
				PREV_Y = mParams.y;                            //뷰의 시작 점
				break;
			case MotionEvent.ACTION_MOVE:
				int x = (int)(event.getRawX() - START_X);    //이동한 거리
				int y = (int)(event.getRawY() - START_Y);    //이동한 거리

				//터치해서 이동한 만큼 이동 시킨다
				mParams.x = PREV_X + x;
				mParams.y = PREV_Y + y;

				optimizePosition();        //뷰의 위치 최적화
				mWindowManager.updateViewLayout(frameView, mParams);    //뷰 업데이트
				break;
			}

			return true;
		}
	};
	
	private OnClickListener mButtonClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			frameView.setVisibility(View.GONE);
		}
	};
	
	private OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			
			playTrackNum = position;
			playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
		}
	};

	@Override
	public void onDestroy() {

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
		
		//서비스 종료시 뷰 제거
		if(mWindowManager != null) {
			if(frameView != null) mWindowManager.removeView(frameView);
		}
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
		lockView.setVisibility(View.VISIBLE);
		loadingBar.setVisibility(View.VISIBLE);
		
		setWifiLock();

		mediaPlayerReset();

		if((shuffleType == MediaPlayerFunctionDefine.SHUFFLE_ON) && (trackList.size() != 1) && (pos != MediaPlayerFunctionDefine.TRACK_SELF)) {
			playTrackNum = random.nextInt(trackList.size());
		}

		setPlayTrackNum(pos);
		trackModel = (SearchTrackListModel)trackList.get(playTrackNum);
		
		titleView.setText(trackModel.title);
		uploaderView.setText(trackModel.uploader);

		startForeground(MediaPlayerFunctionDefine.NOTIFICATION_ID,
				mediaNotification.getCustomNotification(trackModel.title, trackModel.uploader).build());

		playSoundCloud(trackModel);
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
			mediaPlayer.seekTo(0);
			mediaPlayer.start();
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
	@Override
	public void start() {
		
		controller.updateViewController();
		
		if(mediaPlayer != null){
			mediaPlayer.start();
		}
		
		lockView.setVisibility(View.GONE);
		loadingBar.setVisibility(View.GONE);
	}

	@Override
	public void pause() {
		if(mediaPlayer != null){
			mediaPlayer.pause();
		}
	}

	@Override
	public int getDuration() {
		if(mediaPlayer != null){
			return mediaPlayer.getDuration();
		}

		return 0;
	}

	@Override
	public int getBuffering(){
		return buffering;
	}

	@Override
	public int getCurrentPosition() {
		if(mediaPlayer != null){
			return mediaPlayer.getCurrentPosition();
		}

		return 0;
	}

	@Override
	public void seekTo(int pos) {
		if(mediaPlayer != null){
			mediaPlayer.seekTo(pos);
		}
	}

	@Override
	public boolean isPlaying() {
		if(mediaPlayer != null){
			return mediaPlayer.isPlaying();
		}

		return false;
	}

	@Override
	public void next() {
		playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
	}

	@Override
	public void prev() {
		playTracks(MediaPlayerFunctionDefine.TRACK_PREV);
	}

	@Override
	public void shuffle(int shuffleType) {
		this.shuffleType = shuffleType;
	}

	@Override
	public void repeat(int repeatType) {
		this.repeatType = repeatType;
	}
}
