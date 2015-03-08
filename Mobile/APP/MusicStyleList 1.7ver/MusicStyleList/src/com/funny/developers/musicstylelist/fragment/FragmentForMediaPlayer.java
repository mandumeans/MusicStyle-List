package com.funny.developers.musicstylelist.fragment;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayer;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayer.LocalBinder;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerController.MediaPlayerControl;
import com.funny.developers.musicstylelist.util.ServiceUtils;

public class FragmentForMediaPlayer extends Fragment implements SurfaceHolder.Callback, MediaPlayerControl{

	private ContentObserver mediaPlayerReadyObserver;
	private ContentObserver mediaPlayerPlayObserver;
	private ContentObserver mediaPlayerLoadingObserver;
	private ContentObserver mediaPlayerEmptyObserver;

	private FragmentForMediaPlayerHandler mediaPlayerHandler;
	private MusicStyleListMediaPlayerController mediaController;

	private MusicStyleListMediaPlayer mediaPlayerService;

	private ProgressBar loadingBar;
	private ImageView thumnailView;
	private SurfaceView surfaceView;

	private SurfaceHolder surfaceHolder;

	private ServiceConnection mConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;
			mediaPlayerService = binder.getService();
			mediaPlayerService.sendReadyMesseage();
		}

		@Override
		public void onServiceDisconnected(ComponentName name){
			mediaPlayerService = null;
		}
	};

	public void setPlayTrackList(ArrayList<BaseModel> trackList, int playTrackNum){
		MusicStyleListMediaPlayer.setPlayTrackList(trackList, playTrackNum);
		mHandlerPlayTrack.sendEmptyMessage(0);
	}

	public void checkBoundService() {
		if(!ServiceUtils.isMyServiceRunning(getActivity(), MusicStyleListMediaPlayer.class)){
			Intent intent = new Intent(getActivity(), MusicStyleListMediaPlayer.class);
			getActivity().startService(intent);
			getActivity().bindService(intent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
		} else if(mediaPlayerService == null){
			Intent intent = new Intent(getActivity(), MusicStyleListMediaPlayer.class);
			getActivity().bindService(intent, mConnection, Context.BIND_ADJUST_WITH_ACTIVITY);
		} else {
			mediaPlayerService.sendReadyMesseage();
		}
	}

	public void setHandler(FragmentForMediaPlayerHandler mediaPlayerHandler){
		this.mediaPlayerHandler = mediaPlayerHandler;
	}

	//**************************** Handler ***********************************
	@SuppressLint("HandlerLeak")
	private Handler mHandlerDisplay = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mediaPlayerService != null) {
				if(surfaceHolder != null){
					mediaPlayerService.setMediaPlayerDisplay(surfaceHolder);
				} else {
					sendEmptyMessageDelayed(0, 1000);
				}
			} else {
				sendEmptyMessageDelayed(0, 1000);
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mHandlerPlayTrack = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mediaPlayerService != null) {
				Log.d("Error", "mHandlerPlayTrack mediaPlayerService is not null");
				mediaPlayerService.playTracks(MediaPlayerFunctionDefine.TRACK_SELF);
			} else {
				sendEmptyMessageDelayed(0, 1000);
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		mediaPlayerReadyObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				if(mediaPlayerService != null){
					if(mediaPlayerService.isMediaPlayerHasTrackList()){
						setPlayingTrackInfo();
					}
					
					if(mediaPlayerService.isPlaying()){
						mediaController.playTrack();
					}
				}	
			}
		};

		mediaPlayerPlayObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				if(mediaPlayerService != null){
					loadingBar.setVisibility(View.GONE);
					mediaController.setMediaPlayerInfo(true);
					mediaController.playTrack();
				}
			}
		};

		mediaPlayerLoadingObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				if(mediaPlayerService != null){
					loadingBar.setVisibility(View.VISIBLE);
					mediaController.setMediaPlayerInfo(false);
					setPlayingTrackInfo();
				}
			}
		};

		mediaPlayerEmptyObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				mediaPlayerService = null;
			}
		};

		getActivity().getContentResolver().registerContentObserver(NotifyUrlDefine.URI_READY_MEDIAPLAYER, 
				false, mediaPlayerReadyObserver);

		getActivity().getContentResolver().registerContentObserver(NotifyUrlDefine.URI_PLAY_MEDIAPLAYER, 
				false, mediaPlayerPlayObserver);

		getActivity().getContentResolver().registerContentObserver(NotifyUrlDefine.URI_LOAD_MEDIAPLAYER, 
				false, mediaPlayerLoadingObserver);

		getActivity().getContentResolver().registerContentObserver(NotifyUrlDefine.URI_EMPTY_MEDIAPLAYER, 
				false, mediaPlayerEmptyObserver);
	}

	private void setPlayingTrackInfo(){
		SearchTrackListModel tempTrackModel = mediaPlayerService.getTrackInPlay();
		mediaPlayerHandler.setPlayTrack(tempTrackModel);
		if(tempTrackModel.trackType == Define.YOUTUBE_TRACK){
			thumnailView.setVisibility(View.INVISIBLE);
		} else {
			thumnailView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View contentView = inflater.inflate(R.layout.fragment_for_mediaplayer, container, false);

		loadingBar = (ProgressBar)contentView.findViewById(R.id.loading_progressbar);

		surfaceView = (SurfaceView)contentView.findViewById(R.id.play_view);
		surfaceView.getHolder().addCallback(this);

		thumnailView = (ImageView)contentView.findViewById(R.id.thumnail_view);
		thumnailView.setImageResource(R.drawable.no_image);
		thumnailView.setVisibility(View.INVISIBLE);

		mediaController = (MusicStyleListMediaPlayerController)contentView.findViewById(R.id.media_player_controller);
		mediaController.settingController(Define.NOT_ONLY_YOUTUBE_TYPE);
		mediaController.setMediaPlayer(this);

		return contentView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		checkBoundService();
		mHandlerDisplay.sendEmptyMessage(0);
		mediaController.runControllerHandler();
	}

	@Override
	public void onPause() {
		super.onPause();

		if(mediaPlayerService != null){
			mediaPlayerService.setMediaPlayerDisplay(null);
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(ServiceUtils.isMyServiceRunning(getActivity(), MusicStyleListMediaPlayer.class)){
			getActivity().unbindService(mConnection);
		}

		getActivity().getContentResolver().unregisterContentObserver(mediaPlayerReadyObserver);
		getActivity().getContentResolver().unregisterContentObserver(mediaPlayerPlayObserver);
		getActivity().getContentResolver().unregisterContentObserver(mediaPlayerLoadingObserver);
		getActivity().getContentResolver().unregisterContentObserver(mediaPlayerEmptyObserver);

		mHandlerDisplay.removeMessages(0);
		mHandlerPlayTrack.removeMessages(0);
	}

	//********************* SurfaceView ****************************
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceHolder = null;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

	//********************* MediaPlayerView ****************************
	@Override
	public void start() {
		mediaPlayerService.start();
	}

	@Override
	public void pause() {
		mediaPlayerService.pause();
	}

	@Override
	public void next() {
		mediaPlayerService.next();
	}

	@Override
	public void prev() {
		mediaPlayerService.prev();
	}

	@Override
	public void shuffle(int shuffleType) {
		mediaPlayerService.shuffle(shuffleType);
	}

	@Override
	public void repeat(int repeatType) {
		mediaPlayerService.repeat(repeatType);
	}

	@Override
	public int getDuration() {
		return mediaPlayerService.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return mediaPlayerService.getCurrentPosition();
	}

	@Override
	public int getBuffering() {
		return mediaPlayerService.getBuffering();
	}

	@Override
	public void seekTo(int pos) {
		mediaPlayerService.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return mediaPlayerService.isPlaying();
	}

	@Override
	public boolean isMediaPlayerServiceRunning() {
		if(mediaPlayerService != null) {
			return true;
		} else {
			return false;
		}
	}
}
