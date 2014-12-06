package com.funny.developers.musicstylelist.activity;


import android.widget.ListView;
import android.widget.VideoView;

import com.funny.developers.musicstylelist.adapter.PlayListAdapter;
import com.funny.developers.musicstylelist.baseactivity.BaseActivity;
import com.funny.developers.musicstylelist.baseactivity.R;

public class PlayListDetailActivity extends BaseActivity{

	VideoView playVideo = null;
	ListView playListView = null;
	PlayListAdapter playListAdapter = null;
	
	@Override
	protected void settingView() {
		playVideo = (VideoView) findViewById(R.id.play_video);
		playListView = (ListView) findViewById(R.id.play_list_view);
		playListAdapter = new PlayListAdapter(this);
		
		playListAdapter.setList(null);
		playListView.setAdapter(playListAdapter);
		
	}

	@Override
	protected int registerContentView() {
		// TODO Auto-generated method stub
		return R.layout.play_list_detail_activity;
	}
}
