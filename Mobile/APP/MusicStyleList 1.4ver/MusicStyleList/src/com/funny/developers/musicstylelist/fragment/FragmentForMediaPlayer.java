package com.funny.developers.musicstylelist.fragment;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.asynctask.GetYoutubeUrl;
import com.funny.developers.musicstylelist.asynctask.GetYoutubeUrl.onRequestCallBack;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController.MediaPlayerControl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class FragmentForMediaPlayer extends Fragment implements onRequestCallBack, SurfaceHolder.Callback, 
																OnPreparedListener, OnBufferingUpdateListener, 
																OnCompletionListener, MediaPlayerControl {

	private static final int TRACK_PREV = 0;
	private static final int TRACK_NEXT = 1;
	private static final int TRACK_SELF = 2;

	private static final int 	REPEAT_NO = 0;
	private static final int	REPEAT_ONE = 1;
	private static final int 	REPEAT_ALL = 2;

	private int playTrackNum = 0;

	private int repeatType = REPEAT_NO;
	private boolean shuffleType = false;
	
	private Random random = null;

	private SurfaceView surfaceViewPlayer = null;
	private SurfaceHolder surfaceHolder = null;

	private MediaPlayer mediaPlayer = null;
	private MusicStyleListMediaPlayerController mediaController = null;

	private ImageLoader imageLoader = null;
	private ImageLoaderConfiguration imageConfig = null;

	private GetYoutubeUrl getYoutubeUrl = null;

	private ArrayList<BaseModel> trackList = null;

	public void setPlayTrackList(ArrayList<BaseModel> trackList, int playTrackNum){
		this.trackList = trackList;
		this.playTrackNum = playTrackNum;
		playTracks(TRACK_SELF);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View contentView = inflater.inflate(R.layout.fragment_for_mediaplayer, container, false);

		surfaceViewPlayer = (SurfaceView)contentView.findViewById(R.id.play_surface_view);
		surfaceHolder = surfaceViewPlayer.getHolder();
		surfaceHolder.addCallback(this);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);

		mediaController = (MusicStyleListMediaPlayerController)contentView.findViewById(R.id.media_player_controller);
		mediaController.setMediaPlayer(this);

		return contentView;		
	}

	@Override
	public void onStart() {
		super.onStart();
		
		imageLoader = ImageLoader.getInstance();
		imageConfig = new ImageLoaderConfiguration.Builder(getActivity()).build();
		imageLoader.init(imageConfig);
		
		random = new Random();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		//When app pause, mediaPlayer's display set null otherwise the app will be destoryed
		mediaPlayer.setDisplay(null);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	//***************** Media Player Interface *****************
	@Override
	public void onPrepared(MediaPlayer mp) {
		mediaController.setProgressInfo(true);
		Log.d("Error", "onPrepared is ready");
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		mediaController.setBuffering(percent);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		//Repeat 범위를 정한다.
		switch(repeatType) {
		case REPEAT_NO:
			break;

		case REPEAT_ONE:
			playTracks(TRACK_SELF);
			break;

		case REPEAT_ALL:
			playTracks(TRACK_NEXT);
			break;
		}
	}
	
	public void mediaPlayerReset() {
		mediaPlayer.stop();
		mediaPlayer.reset();
	}

	//***************** Surface *****************
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mediaPlayer.setDisplay(holder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

	//***************** getYoutubeUrl Interface *****************
	@Override
	public void getYoutubeUrl(String youtubeUrl) {
		try {
			surfaceViewPlayer.setBackgroundDrawable(null);
			
			mediaPlayer.setDataSource(URLDecoder.decode(youtubeUrl, "UTF-8"));
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (Exception e) {
			e.toString();
		}
	}

	//***************** SoundCloud Play function *****************
	public void playSoundCloud(SearchTrackListModel trackModel) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.playstyle_icon)
		.showImageForEmptyUri(R.drawable.playstyle_icon)
		.build();
		
		imageLoader.loadImage(trackModel.thumbnail, options, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap thumnail) {
				surfaceViewPlayer.setBackgroundDrawable(new BitmapDrawable(thumnail));
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {}
		});

		try{
			mediaPlayer.setDataSource(trackModel.id+"?client_id="+Define.SOUND_CLOUD_API_KEY);
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch(Exception e){

		}
	}

	//***************** Play Function *****************
	private void playTracks(int pos){
		mediaController.setProgressInfo(false);
		Log.d("Error", "playTracks is ready");
		
		SearchTrackListModel trackModel = null;
		
		//Shuffle Type을 확인한다.
		if(shuffleType && (trackList.size() != 1) && (pos != TRACK_SELF)) {
			playTrackNum = random.nextInt(trackList.size());
		}

		setPlayTrackNum(pos);
		mediaPlayerReset();
		trackModel = (SearchTrackListModel)trackList.get(playTrackNum);

		switch(trackModel.trackType){
		case Define.YOUTUBE_TRACK:
			getYoutubeUrl = new GetYoutubeUrl();
			getYoutubeUrl.setOnRequestCallBack(this);
			getYoutubeUrl.execute(trackModel.id,"18");
			break;

		case Define.SOUNDCLOUD_TRACK:
			playSoundCloud(trackModel);
			break;
		}

	}
	
	private void setPlayTrackNum(int pos){
		switch(pos){
		case TRACK_NEXT:
			playTrackNum++;
			if(this.trackList.size()-1 < playTrackNum){
				playTrackNum = 0;
			}
			break;
		
		case TRACK_SELF:
			break;
			
		case TRACK_PREV:
			playTrackNum--;
			if(playTrackNum < 0){
				playTrackNum = this.trackList.size() - 1;
			}
			break;
		}
	}

	//***************** Media Controller Interface *****************
	@Override
	public void start() {
		mediaPlayer.start();
	}

	@Override
	public void pause() {
		mediaPlayer.pause();
	}

	@Override
	public int getDuration() {
		return mediaPlayer.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayer.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		mediaPlayer.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayer.isPlaying();
	}

	@Override
	public void next() {
		playTracks(TRACK_NEXT);
	}

	@Override
	public void prev() {
		playTracks(TRACK_PREV);
	}

	@Override
	public void shuffle(boolean shuffleType) {
		this.shuffleType = shuffleType;
	}

	@Override
	public void repeat(int repeatType) {
		this.repeatType = repeatType;
	}
}
