package com.funny.developers.musicstylelist.player;

import java.util.Formatter;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;

public class MusicStyleListMediaPlayerController extends RelativeLayout {

	private Context mContext = null;
	private MediaPlayerControl mPlayer = null;

	StringBuilder mFormatBuilder = null;
	Formatter mFormatter = null;

	private static final int 	REPEAT_NO = 0;
	private static final int	REPEAT_ONE = 1;
	private static final int 	REPEAT_ALL = 2;

	private int repeatType = REPEAT_NO;
	private boolean shuffleType = false;
	private boolean mDragging = false;
	
	private View.OnClickListener mNextListener, mPrevListener, mPlayListener,
	mShuffleListener, mRepeatListener;
	private OnSeekBarChangeListener mSeekListener;

	private ProgressBar         mProgress;
	private TextView            mEndTime, mCurrentTime;
	private ImageButton         mPlayButton;
	private ImageButton         mNextButton;
	private ImageButton         mPrevButton;
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

		mNextListener = new View.OnClickListener() {
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
		};

		mRepeatListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doRepeatType();
				mPlayer.repeat(repeatType);
			}
		};

		mShuffleListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				doShuffleType();
				mPlayer.shuffle(shuffleType);
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
				
				mHandler.sendEmptyMessage(0);
			}
		};
	}

	//Play Listener
	private void doPauseResume() {
		if (mPlayer.isPlaying()) {
			mPlayer.pause();
		} else {
			mHandler.sendEmptyMessage(0);
			mPlayer.start();
		}
		updatePausePlay();
	}

	private void updatePausePlay() {
		if (mPlayer.isPlaying()) {
			mPlayButton.setImageResource(R.drawable.ic_action_compose);
		} else {
			mPlayButton.setImageResource(R.drawable.ic_action_search);
		}
	}

	//Repeat Listener
	private void doRepeatType(){
		switch (repeatType) {
		case REPEAT_NO:
			repeatType = REPEAT_ONE;
			mRepeatButton.setImageResource(R.drawable.ic_action_compose);
			break;

		case REPEAT_ONE:
			repeatType = REPEAT_ALL;
			mRepeatButton.setImageResource(R.drawable.playstyle_icon);
			break;

		case REPEAT_ALL:
			repeatType = REPEAT_NO;
			mRepeatButton.setImageResource(R.drawable.ic_action_search);
			break;
		}
	}

	//Shuffle Listener
	private void doShuffleType(){
		if(shuffleType){
			shuffleType = false;
			mShuffleButton.setImageResource(R.drawable.ic_action_search);
		} else {
			shuffleType = true;
			mShuffleButton.setImageResource(R.drawable.ic_action_compose);
		}
	}

	//Progress Listener
	private int setProgress() {
		if (mPlayer == null) {
			return 0;
		}
		
		int position = mPlayer.getCurrentPosition();
		int duration = mPlayer.getDuration();
		if (mProgress != null) {
			if (duration > 0) {
				// use long to avoid overflow
				long pos = 1000L * position / duration;
				mProgress.setProgress( (int) pos);
			}
		}

		if (mEndTime != null)
			mEndTime.setText(stringForTime(duration));
		if (mCurrentTime != null)
			mCurrentTime.setText(stringForTime(position));

		return position;
	}
	
	//Setting Progress
	public void setProgressInfo(boolean loading){
		
		mProgress.setEnabled(loading);
		mProgress.setProgress(0);
		mEndTime.setText("");
		mCurrentTime.setText("");
		updatePausePlay();
		
		if(loading){	
			mHandler.sendEmptyMessage(0);
		}
	}
	
	public void setBuffering(int percent) {
		mProgress.setSecondaryProgress(percent * 10);
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
			mPlayButton.setImageResource(R.drawable.ic_action_search);
		}

		mPrevButton = (ImageButton) v.findViewById(R.id.imagebutton_prevbutton);
		if (mPrevButton != null) {
			mPrevButton.setOnClickListener(mPrevListener);
			mPrevButton.setImageResource(R.drawable.ic_action_search);
		}

		mNextButton = (ImageButton) v.findViewById(R.id.imagebutton_nextbutton);
		if (mNextButton != null) {
			mNextButton.setOnClickListener(mNextListener);
			mNextButton.setImageResource(R.drawable.ic_action_search);
		}

		mRepeatButton = (ImageButton) v.findViewById(R.id.imagebutton_repeatbutton);
		if(mRepeatButton != null) {
			mRepeatButton.setOnClickListener(mRepeatListener);
			mRepeatButton.setImageResource(R.drawable.ic_action_search);
		}

		mShuffleButton = (ImageButton) v.findViewById(R.id.imagebutton_sufflebutton);
		if (mShuffleButton != null) {
			mShuffleButton.setOnClickListener(mShuffleListener);
			mShuffleButton.setImageResource(R.drawable.ic_action_search);
		}

		mProgress = (ProgressBar) v.findViewById(R.id.progressbar_play_control);
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

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int pos;
			pos = setProgress();
			if (!mDragging && mPlayer.isPlaying()) {
				msg = obtainMessage(0);
				sendMessageDelayed(msg, 1000 - (pos % 1000));
			}
		}
	};

	public interface MediaPlayerControl {
		void    start();
		void    pause();
		void	next();
		void	prev();
		void	shuffle(boolean shuffleType);
		void	repeat(int repeatType);
		int     getDuration();
		int     getCurrentPosition();
		void    seekTo(int pos);
		boolean isPlaying();
	}
}
