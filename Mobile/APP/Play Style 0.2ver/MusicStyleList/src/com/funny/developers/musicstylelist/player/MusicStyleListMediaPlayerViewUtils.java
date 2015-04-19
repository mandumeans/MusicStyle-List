package com.funny.developers.musicstylelist.player;


import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import com.funny.developers.musicstylelist.fragment.FragmentForMediaPlayer;
import com.funny.developers.musicstylelist.fragment.FragmentForMediaPlayerHandler;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.util.ServiceUtils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;

public class MusicStyleListMediaPlayerViewUtils {

	private FragmentForMediaPlayer mediaPlayerFragment;
	private FragmentForMediaPlayerHandler mediaPlayerHandlerFragment;

	public MusicStyleListMediaPlayerViewUtils(){
		super();
	}

	public void initialMediaPlayerView(Context context, SlidingUpPanelLayout slidingUpPanel, Fragment handleFragment, Fragment contentFragment) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);

		slidingUpPanel.setPanelHeight(metrics.heightPixels / 8);

		mediaPlayerHandlerFragment = (FragmentForMediaPlayerHandler) handleFragment;
		ViewGroup.LayoutParams param = (LayoutParams) mediaPlayerHandlerFragment.getView().getLayoutParams();
		param.height = metrics.heightPixels / 8;
		mediaPlayerHandlerFragment.getView().setLayoutParams(param);
		mediaPlayerFragment = (FragmentForMediaPlayer) contentFragment;
		mediaPlayerFragment.setHandler(mediaPlayerHandlerFragment);
	}

	public void setTrack(ArrayList<BaseModel> trackList, int playTrackNum) {
		mediaPlayerFragment.setPlayTrackList(trackList, playTrackNum);
	}

	public boolean displayMediaPlayer(Context context, SlidingUpPanelLayout slidingUpPanel){
		if(ServiceUtils.isMyServiceRunning(context, MusicStyleListMediaPlayer.class)){
			if(MusicStyleListMediaPlayer.isMediaPlayerHasTrackList()){
				if(slidingUpPanel.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN){
					slidingUpPanel.setPanelState(PanelState.COLLAPSED);
					return false;
				}
			}
		} else {
			slidingUpPanel.setPanelState(PanelState.HIDDEN);
		}

		return true;
	}

	public void exPandPanel(SlidingUpPanelLayout slidingUpPanel) {
		slidingUpPanel.setPanelState(PanelState.EXPANDED);
	}
}
