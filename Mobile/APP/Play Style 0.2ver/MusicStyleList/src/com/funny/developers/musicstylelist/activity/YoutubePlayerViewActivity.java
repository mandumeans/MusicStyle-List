package com.funny.developers.musicstylelist.activity;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.YoutubePlayerListAdapter;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController.MediaPlayerControl;
import com.funny.developers.musicstylelist.util.SettingUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubePlayerViewActivity extends YouTubeFailureRecoveryActivity implements MediaPlayerControl, 
PlayerStateChangeListener, OnItemClickListener, OnClickListener{

	private int playTrackNum = 0;

	private int repeatType;
	private int shuffleType;

	private Random random;

	private ArrayList<BaseModel> trackList;

	private ImageButton backbutton;
	private ImageButton lockbutton;
	
	private View lockView;

	private YouTubePlayer mPlayer;
	private MusicStyleListMediaPlayerController controller;

	private ListView trackListView;
	private YoutubePlayerListAdapter trackListAdapter;

	//AdView
	private AdView adView;

	@SuppressWarnings("unchecked")
	private void getExtras(){
		Intent intent = getIntent();
		trackList = (ArrayList<BaseModel>)intent.getSerializableExtra(Define.YOUTUBE_TRACK_LIST_INTENT);
		playTrackNum = intent.getIntExtra(Define.YOUTUBE_TRACK_LIST_POSITION, 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youtubeplayer_activity);

		getExtras();

		// 리소스로 AdView를 검색하고 요청을 로드합니다.
		adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		repeatType = SettingUtils.getMediaplayerRepeatType(this);
		shuffleType = SettingUtils.getMediaplayerShuffleType(this);

		random = new Random();

		backbutton = (ImageButton) findViewById(R.id.back_button);
		backbutton.setOnClickListener(this);
		
		lockbutton = (ImageButton) findViewById(R.id.lock_button);
		lockbutton.setOnClickListener(this);
		
		lockView = (View) findViewById(R.id.lock_layout);

		controller = (MusicStyleListMediaPlayerController) findViewById(R.id.media_player_controller);
		controller.settingController(Define.ONLY_YOUTUBE_TYPE);
		controller.setMediaPlayer(this);

		trackListView = (ListView) findViewById(R.id.track_list_view);
		trackListAdapter = new YoutubePlayerListAdapter(this);
		trackListAdapter.setList(trackList);
		trackListView.setAdapter(trackListAdapter);
		trackListView.setOnItemClickListener(this);

		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(Define.YOUTUBE_API_KEY, this);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		if(adView != null){
			adView.destroy();
		}
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

		//Shuffle Type
		if((shuffleType == MediaPlayerFunctionDefine.SHUFFLE_ON) && (trackList.size() != 1) && (pos != MediaPlayerFunctionDefine.TRACK_SELF)) {
			playTrackNum = random.nextInt(trackList.size());
		}

		setPlayTrackNum(pos);
		SearchTrackListModel trackModel = (SearchTrackListModel)trackList.get(playTrackNum);

		mPlayer.loadVideo(trackModel.id);
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

	@Override
	public void start() {
		mPlayer.play();
	}

	@Override
	public void pause() {
		mPlayer.pause();
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
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		return 0;
	}

	@Override
	public int getBuffering() {
		return 0;
	}

	@Override
	public void seekTo(int pos) {}

	@Override
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	@Override
	public boolean isMediaPlayerServiceRunning() {
		if(mPlayer != null){
			return true;
		} else {
			return false;
		}
	}

	//******************** Youtube Interface ************************

	@Override
	public void onAdStarted() {}

	@Override
	public void onError(ErrorReason arg0) {}

	@Override
	public void onLoaded(String arg0) {}

	@Override
	public void onLoading() {}

	@Override
	public void onVideoEnded() {
		switch(repeatType) {
		case MediaPlayerFunctionDefine.REPEAT_NO:
			break;

		case MediaPlayerFunctionDefine.REPEAT_ONE:
			playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
			break;

		case MediaPlayerFunctionDefine.REPEAT_ALL:
			playTracks(MediaPlayerFunctionDefine.TRACK_NEXT);
			break;
		}
	}

	@Override
	public void onVideoStarted() {}

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
		}
	}
}
