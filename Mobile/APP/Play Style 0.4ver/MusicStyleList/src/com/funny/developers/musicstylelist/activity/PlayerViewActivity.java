package com.funny.developers.musicstylelist.activity;

import java.util.ArrayList;
import java.util.Random;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.PlayerListAdapter;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayer;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController.MediaPlayerControl;
import com.funny.developers.musicstylelist.util.DigitUtils;
import com.funny.developers.musicstylelist.util.ImageUtils;
import com.funny.developers.musicstylelist.util.ServiceUtils;
import com.funny.developers.musicstylelist.util.SettingUtils;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayerViewActivity extends YouTubeFailureRecoveryActivity implements MediaPlayerControl, 
PlayerStateChangeListener, OnPreparedListener, OnCompletionListener, OnBufferingUpdateListener, OnErrorListener, OnItemClickListener, OnClickListener{

	private int playTrackNum = 0;

	private int repeatType;
	private int shuffleType;

	private Random random;

	private ArrayList<BaseModel> trackList;

	private ImageButton backgoundPlayButton;
	private ImageButton backbutton;
	private ImageButton lockbutton;

	private ProgressBar loadingView;
	private YouTubePlayerView youtubeView;
	private ImageView thumNailView;
	private View lockView;

	private YouTubePlayer mPlayer;
	private MediaPlayer mediaPlayer;
	private MusicStyleListMediaPlayerController controller;

	private ListView trackListView;
	private PlayerListAdapter trackListAdapter;

	private int buffering = 0;

	//PlayTrack
	private SearchTrackListModel playTrack;

	@SuppressWarnings("unchecked")
	private void getExtras(){
		Intent intent = getIntent();
		trackList = (ArrayList<BaseModel>)intent.getSerializableExtra(Define.PLAYER_TRACK_LIST_INTENT);
		playTrackNum = intent.getIntExtra(Define.PLAYER_TRACK_LIST_POSITION, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_activity);

		if(ServiceUtils.isMyServiceRunning(getApplicationContext(), MusicStyleListMediaPlayer.class)){
			stopService(new Intent(this, MusicStyleListMediaPlayer.class));
		}
		
		getExtras();

		repeatType = SettingUtils.getMediaplayerRepeatType(this);
		shuffleType = SettingUtils.getMediaplayerShuffleType(this);

		random = new Random();

		backgoundPlayButton = (ImageButton) findViewById(R.id.background_play_button);
		backgoundPlayButton.setOnClickListener(this);

		backbutton = (ImageButton) findViewById(R.id.back_button);
		backbutton.setOnClickListener(this);

		lockbutton = (ImageButton) findViewById(R.id.lock_button);
		lockbutton.setOnClickListener(this);

		lockView = (View) findViewById(R.id.lock_layout);

		loadingView = (ProgressBar) findViewById(R.id.loading_progressbar);

		controller = (MusicStyleListMediaPlayerController) findViewById(R.id.media_player_controller);
		controller.setMediaPlayer(this);

		trackListView = (ListView) findViewById(R.id.track_list_view);
		trackListAdapter = new PlayerListAdapter(this);
		trackListAdapter.setList(trackList);
		trackListView.setAdapter(trackListAdapter);
		trackListView.setOnItemClickListener(this);

		thumNailView= (ImageView) findViewById(R.id.thumnail_view);

		youtubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youtubeView.initialize(Define.YOUTUBE_API_KEY, this);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		controller.destory();

		if(mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		try{
			if(mPlayer != null) {
				mPlayer.pause();
				mPlayer.release();
			}
		} catch(Exception e){}


		mErrorHandler.removeMessages(Define.HANDLER_WHAT);
	}

	@Override
	public void onSaveInstanceState (Bundle outState){
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
			boolean wasRestored) {
		mPlayer = player;
		mPlayer.setPlayerStateChangeListener(this);
		playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	private void playTracks(int pos){
		buffering = 0;

		mPlayer.pause();

		mediaPlayer.stop();
		mediaPlayer.reset();

		//Shuffle Type
		if((shuffleType == MediaPlayerFunctionDefine.SHUFFLE_ON) && (trackList.size() != 1) && (pos != MediaPlayerFunctionDefine.TRACK_SELF)) {
			playTrackNum = random.nextInt(trackList.size());
		}

		setPlayTrackNum(pos);
		playTrack = (SearchTrackListModel)trackList.get(playTrackNum);

		switch(playTrack.trackType){
		case Define.YOUTUBE_TRACK:
			youtubeView.setVisibility(View.VISIBLE);
			thumNailView.setVisibility(View.INVISIBLE);
			loadingView.setVisibility(View.GONE);
			mPlayer.loadVideo(playTrack.id);
			break;

		case Define.SOUNDCLOUD_TRACK:
			youtubeView.setVisibility(View.INVISIBLE);
			loadingView.setVisibility(View.VISIBLE);
			thumNailView.setVisibility(View.VISIBLE);
			ImageUtils.displayUrlImage(thumNailView, 
					DigitUtils.changeImageUrl(playTrack.thumbnail, Define.SOUNDCLOUD_THUMNAIL_LARGE, Define.SOUNDCLOUD_THUMNAIL_T300X300), R.drawable.no_image);
			playSoundCloud(playTrack);
			break;
		}
	}

	private void setPlayTrackNum(int pos){
		switch(pos){
		case MediaPlayerFunctionDefine.TRACK_NEXT:
			playTrackNum++;
			if(this.trackList.size()-1 < playTrackNum){
				playTrackNum = 0;
			}
			break;

		case MediaPlayerFunctionDefine.TRACK_SELF:
			break;

		case MediaPlayerFunctionDefine.TRACK_PREV:
			playTrackNum--;
			if(playTrackNum < 0){
				playTrackNum = this.trackList.size() - 1;
			}
			break;
		}
	}

	//***************** SoundCloud Play function *****************
	public void playSoundCloud(SearchTrackListModel trackModel) {
		try{
			mediaPlayer.setDataSource(Define.SOUNDCLOUD_PLAY_URL + trackModel.id+"/stream?client_id="+Define.SOUND_CLOUD_API_KEY);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepareAsync();
		} catch(Exception e){

		}
	}

	//***************** Controller function *****************
	@Override
	public void start() {

		loadingView.setVisibility(View.GONE);

		controller.updateViewController();

		if(playTrack.trackType == Define.YOUTUBE_TRACK){
			mPlayer.play();
		} else {
			mediaPlayer.start();
		}
	}

	@Override
	public void pause() {

		if(playTrack.trackType == Define.YOUTUBE_TRACK){
			mPlayer.pause();
		} else {
			mediaPlayer.pause();
		}
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

	@Override
	public int getDuration() {
		if(playTrack.trackType == Define.YOUTUBE_TRACK){
			return mPlayer.getDurationMillis();
		} else {
			return mediaPlayer.getDuration();
		}

	}

	@Override
	public int getCurrentPosition() {

		if(playTrack.trackType == Define.YOUTUBE_TRACK){
			return mPlayer.getCurrentTimeMillis();
		} else {
			return mediaPlayer.getCurrentPosition();
		}
	}

	@Override
	public int getBuffering() {
		return buffering;
	}

	@Override
	public void seekTo(int pos) {
		if(playTrack.trackType == Define.YOUTUBE_TRACK){
			mPlayer.seekToMillis(pos);
		} else {
			mediaPlayer.seekTo(pos);
		}
	}

	@Override
	public boolean isPlaying() {
		if(playTrack.trackType == Define.YOUTUBE_TRACK){
			return mPlayer.isPlaying();
		} else {
			return mediaPlayer.isPlaying();
		}
	}

	public void endTrack(){
		switch(repeatType) {
		case MediaPlayerFunctionDefine.REPEAT_NO:
			break;

		case MediaPlayerFunctionDefine.REPEAT_ONE:
			if(playTrack.trackType == Define.YOUTUBE_TRACK){
				mPlayer.seekToMillis(0);
			} else {
				mediaPlayer.seekTo(0);
			}

			start();

			break;

		case MediaPlayerFunctionDefine.REPEAT_ALL:
			playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
			break;
		}
	}

	//******************** Youtube Interface ************************
	@Override
	public void onAdStarted() {}

	@Override
	public void onError(ErrorReason arg0) {}

	@Override
	public void onLoaded(String arg0) {
		start();
	}

	@Override
	public void onLoading() {}

	@Override
	public void onVideoEnded() {
		endTrack();
	}

	@Override
	public void onVideoStarted() {}

	//******************** Mediaplayer Interface ************************
	@Override
	public void onCompletion(MediaPlayer mp) {
		endTrack();
	}

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
	public boolean onError(MediaPlayer mp, int what, int extra) {
		runErrorHandler();
		return true;
	}

	private void runErrorHandler(){
		if(mErrorHandler.hasMessages(Define.HANDLER_WHAT)){
			mErrorHandler.removeMessages(Define.HANDLER_WHAT);
		}
		mErrorHandler.sendEmptyMessageDelayed(Define.HANDLER_WHAT, Define.HANDLER_MEDIAPLAYER_ERROR_DELAY_TIME);
	}

	@SuppressLint("HandlerLeak")
	private Handler mErrorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(!mediaPlayer.isPlaying()){
				playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
			}
		}
	};

	//Item Click
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		view.setSelected(true);
		playTrackNum = position;
		playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
	}

	@Override
	public void onClick(View v) {

		switch(v.getId()){
		case R.id.back_button:
			finish();
			break;
		case R.id.lock_button:
			if(lockbutton.isSelected()){
				lockbutton.setSelected(false);
				lockView.setVisibility(View.GONE);
			} else {
				lockbutton.setSelected(true);
				lockView.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.background_play_button:
			Intent intent = new Intent(this, MusicStyleListMediaPlayer.class); 
			intent.setAction(MediaPlayerFunctionDefine.NOTIFICATION_TRACKLIST_INTENT_ID);
			intent.putExtra(Define.PLAYER_TRACK_LIST_INTENT, getBackgroundPlayerList());
			startService(intent);
			finish();
			break;
		}
	}
	
	public ArrayList<BaseModel> getBackgroundPlayerList(){
		
		ArrayList<BaseModel> tempList = new ArrayList<BaseModel>();
		
		for (BaseModel baseModel : trackList) {
			if(((SearchTrackListModel)baseModel).trackType == Define.SOUNDCLOUD_TRACK){
				tempList.add(baseModel);
			}
		}
		
		return tempList;
	}
}