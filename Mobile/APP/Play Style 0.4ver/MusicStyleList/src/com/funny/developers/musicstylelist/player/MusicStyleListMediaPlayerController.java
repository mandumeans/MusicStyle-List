package com.funny.developers.musicstylelist.player;

import java.util.Formatter;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.MediaPlayerFunctionDefine;
import com.funny.developers.musicstylelist.util.SettingUtils;

public class MusicStyleListMediaPlayerController extends RelativeLayout {

	private Context mContext;
	private MediaPlayerControl mPlayer;

	private StringBuilder mFormatBuilder = null;
	private Formatter mFormatter = null;

	private int repeatType;
	private int shuffleType;
	private boolean mDragging = false;

	private View.OnClickListener mPlayListener,
	mShuffleListener, mRepeatListener;
	//mNextListener, mPrevListener,

	private OnSeekBarChangeListener mSeekListener;

	private SeekBar		        mProgress;
	private TextView            mEndTime, mCurrentTime;
	private ImageButton         mPlayButton;
	/*private ImageButton         mNextButton;
	private ImageButton         mPrevButton;*/
	private ImageButton         mRepeatButton;
	private ImageButton         mShuffleButton;

	public MusicStyleListMediaPlayerController(Context context) {
		super(context);
		mContext = context;
		setListener();
		settingView();
	}

	public MusicStyleListMediaPlayerController(Context context, AttributeSet attrs){
		super(context, attrs);
		mContext = context;
		setListener();
		settingView();
	}
	
	public void destory(){
		mHandler.removeMessages(Define.HANDLER_WHAT);
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
	}

	//setting Listener function
	private void setListener(){

		mPlayListener = new View.OnClickListener() {
			public void onClick(View v) {
				doPauseResume();
			}
		};

		/*mNextListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlayer.next();
			}
		};

		mPrevListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPlayer.prev();
			}
		};*/

		mRepeatListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doRepeatType();
				mPlayer.repeat(repeatType);
				SettingUtils.setMediaplayerRepeatType(mContext, repeatType);
			}
		};

		mShuffleListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doShuffleType();
				mPlayer.shuffle(shuffleType);
				SettingUtils.setMediaplayerShuffleType(mContext, shuffleType);
			}		
		};

		//seekbarlistener
		mSeekListener = new OnSeekBarChangeListener() {
			public void onStartTrackingTouch(SeekBar bar) {
				mDragging = true;
			}

			public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
				if (!fromuser) {
					return;
				}

				long duration = mPlayer.getDuration();
				long newposition = (duration * progress) / 1000L;
				mPlayer.seekTo( (int) newposition);
				if (mCurrentTime != null)
					mCurrentTime.setText(stringForTime( (int) newposition));
			}

			public void onStopTrackingTouch(SeekBar bar) {
				mDragging = false;
				setProgress();
				updatePausePlay();

				mHandler.sendEmptyMessage(Define.HANDLER_WHAT);
			}
		};
	}

	//Play Listener
	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mHandler.sendEmptyMessage(Define.HANDLER_WHAT);
			mPlayer.start();
		}
		updatePausePlay();
	}

	private void updatePausePlay() {
		if (mPlayer.isPlaying()) {
			mPlayButton.setImageResource(R.drawable.pause);
		} else {
			mPlayButton.setImageResource(R.drawable.play);
		}
	}

	//Repeat Listener
	private void doRepeatType(){
		switch (repeatType) {
		case MediaPlayerFunctionDefine.REPEAT_NO:
			repeatType = MediaPlayerFunctionDefine.REPEAT_ONE;
			mRepeatButton.setImageResource(R.drawable.replay_one_on);
			break;

		case MediaPlayerFunctionDefine.REPEAT_ONE:
			repeatType = MediaPlayerFunctionDefine.REPEAT_ALL;
			mRepeatButton.setImageResource(R.drawable.replay_on);
			break;

		case MediaPlayerFunctionDefine.REPEAT_ALL:
			repeatType = MediaPlayerFunctionDefine.REPEAT_NO;
			mRepeatButton.setImageResource(R.drawable.replay_off);
			break;
		}
	}

	//Shuffle Listener
	private void doShuffleType(){
		switch(shuffleType){
		case MediaPlayerFunctionDefine.SHUFFLE_OFF:
			shuffleType = MediaPlayerFunctionDefine.SHUFFLE_ON;
			mShuffleButton.setImageResource(R.drawable.suffle_on);
			break;
		case MediaPlayerFunctionDefine.SHUFFLE_ON:
			shuffleType = MediaPlayerFunctionDefine.SHUFFLE_OFF;
			mShuffleButton.setImageResource(R.drawable.suffle_off);
			break;
		}
	}

	//Progress Listener
	private int setProgress() {
		if (mPlayer == null) {
			return 0;
		}

		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		int buffering = mPlayer.getBuffering();
		
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress((int) pos);
			}
			
			mProgress.setSecondaryProgress(buffering);
		}

		if (mEndTime != null)
			mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}

	public void playTrack(){
		mHandler.sendEmptyMessage(Define.HANDLER_WHAT);
	}

	//Setting View
	public void settingView(){
		RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
				);

		View v = makeControllerView();
		addView(v, frameParams);
	}

	protected View makeControllerView() {
		LayoutInflater inflate = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View mRoot = inflate.inflate(R.layout.layout_media_controller, null);

		initControllerView(mRoot);

		return mRoot;
	}

	private void initControllerView(View v) {

		mPlayButton = (ImageButton) v.findViewById(R.id.imagebutton_playbutton);
		if (mPlayButton != null) {
			mPlayButton.requestFocus();
			mPlayButton.setOnClickListener(mPlayListener);
			mPlayButton.setImageResource(R.drawable.play);
		}

		/*mPrevButton = (ImageButton) v.findViewById(R.id.imagebutton_prevbutton);
		if (mPrevButton != null) {
			mPrevButton.setOnClickListener(mPrevListener);
			mPrevButton.setImageResource(R.drawable.prev);
		}

		mNextButton = (ImageButton) v.findViewById(R.id.imagebutton_nextbutton);
		if (mNextButton != null) {
			mNextButton.setOnClickListener(mNextListener);
			mNextButton.setImageResource(R.drawable.next);
		}*/

		repeatType = SettingUtils.getMediaplayerRepeatType(mContext);
		shuffleType = SettingUtils.getMediaplayerShuffleType(mContext);

		mRepeatButton = (ImageButton) v.findViewById(R.id.imagebutton_repeatbutton);
		if(mRepeatButton != null) {
			mRepeatButton.setOnClickListener(mRepeatListener);
			switch (repeatType) {
			case MediaPlayerFunctionDefine.REPEAT_NO:
				mRepeatButton.setImageResource(R.drawable.replay_off);
				break;

			case MediaPlayerFunctionDefine.REPEAT_ONE:
				mRepeatButton.setImageResource(R.drawable.replay_one_on);
				break;

			case MediaPlayerFunctionDefine.REPEAT_ALL:
				mRepeatButton.setImageResource(R.drawable.replay_on);
				break;
			}
		}

		mShuffleButton = (ImageButton) v.findViewById(R.id.imagebutton_sufflebutton);
		if (mShuffleButton != null) {
			mShuffleButton.setOnClickListener(mShuffleListener);
			switch(shuffleType){
			case MediaPlayerFunctionDefine.SHUFFLE_OFF:
				mShuffleButton.setImageResource(R.drawable.suffle_off);
				break;
			case MediaPlayerFunctionDefine.SHUFFLE_ON:
				mShuffleButton.setImageResource(R.drawable.suffle_on);
				break;
			}
		}

		mProgress = (SeekBar) v.findViewById(R.id.progressbar_play_control);
		if (mProgress != null) {
			if (mProgress instanceof SeekBar) {
				SeekBar seeker = (SeekBar) mProgress;
				seeker.setOnSeekBarChangeListener(mSeekListener);
			}
			mProgress.setMax(1000);
		}

		mEndTime = (TextView) v.findViewById(R.id.textview_end_time);
		mCurrentTime = (TextView) v.findViewById(R.id.textview_current_time);
		mFormatBuilder = new StringBuilder();
		mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
	}
	
	public void updateViewController() {
		mHandler.sendEmptyMessage(Define.HANDLER_WHAT);
	}

	private String stringForTime(int timeMs) {
		int totalSeconds = timeMs / 1000;

		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours   = totalSeconds / 3600;

		mFormatBuilder.setLength(0);
		if (hours > 0) {
			return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
		} else {
			return mFormatter.format("%02d:%02d", minutes, seconds).toString();
		}
	}

	//**************************** Handler ***********************************
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mPlayer != null){
				int pos;
				pos = setProgress();
				
				if(mPlayer.isPlaying()){
					mPlayButton.setImageResource(R.drawable.pause);
				} else {
					mPlayButton.setImageResource(R.drawable.play);
				}

				mProgress.setSecondaryProgress(mPlayer.getBuffering() * 10);
				if (!mDragging) {
					msg = obtainMessage(Define.HANDLER_WHAT);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
				} 
			}
		}
	};
	
	public interface MediaPlayerControl {
		void    start();
		void    pause();
		void	next();
		void	prev();
		void	shuffle(int shuffleType);
		void	repeat(int repeatType);
		int     getDuration();
		int     getCurrentPosition();
		int 	getBuffering();
		void    seekTo(int pos);
		boolean isPlaying();
	}
}
