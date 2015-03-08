package com.funny.developers.musicstylelist.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.SearchPlayListDetailAdapter;
import com.funny.developers.musicstylelist.baseactivity.BaseActivity;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.parameterurl.ParameterUrl;
import com.funny.developers.musicstylelist.parser.SearchListParser;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerViewUtils;
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.util.DigitUtils;
import com.funny.developers.musicstylelist.view.DetailHeaderView;
import com.funny.developers.musicstylelist.view.PullToRefreshView.OnPullToRefresh;
import com.funny.developers.musicstylelist.view.SearchPlayListDetailView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class PlayListDetailActivity extends BaseActivity implements OnPullToRefresh{

	private Toolbar playListDetailActivityToolBar;

	//media player fragment
	private MusicStyleListMediaPlayerViewUtils mediaPlayerViewUtils;
	private SlidingUpPanelLayout slidingUpPanel;
	
	private DetailHeaderView headerView;

	private SearchPlayListDetailView playListDetailView;
	private SearchPlayListDetailAdapter playListDetailAdapter;
	private SearchListParser playListDetailParser;
	private ProgressBar loadingBar;

	private int searchIndex;

	private String requestId;
	private int requestType;

	private String playlistTitle;
	private String playlistThumnailUrl;
	private int playlistTrackNum;

	private void getExtras(){
		Intent intent = getIntent();

		requestId = intent.getStringExtra(Define.PLAYLISTDETAIL_REQUEST_ID);
		requestType = intent.getIntExtra(Define.PLAYLISTDETAIL_REQUEST_TYPE, Define.YOUTUBE_SEARCH);

		playlistTitle = intent.getStringExtra(Define.PLAYLISTDETAIL_TRACK_LIST_TITLE);
		playlistThumnailUrl = intent.getStringExtra(Define.PLAYLISTDETAIL_THUMNAIL_URL);
		playlistTrackNum = intent.getIntExtra(Define.PLAYLISTDETAIL_TRACK_LIST_NUM, 0);
		
		indexOrganizer(Define.INDEX_INITIALIZE_COMMEND);
	}

	@Override
	protected void settingView() {
		getExtras();
		
		playListDetailActivityToolBar = (Toolbar)findViewById(R.id.playlistdetail_activity_toolbar);
		playListDetailActivityToolBar.setTitle("PlayList");

		setSupportActionBar(playListDetailActivityToolBar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		
		headerView = new DetailHeaderView(getApplicationContext());
		headerView.setLayoutParams(new AbsListView.LayoutParams(metrics.widthPixels, metrics.widthPixels));

		if(requestType == Define.YOUTUBE_SEARCH){
			playlistThumnailUrl = DigitUtils.changeImageUrl(playlistThumnailUrl, Define.YOUTUBE_THUMNAIL_DEFAULT, Define.YOUTUBE_THUMNAIL_HQDEFAULT);
		} else {
			playlistThumnailUrl = DigitUtils.changeImageUrl(playlistThumnailUrl, Define.SOUNDCLOUD_THUMNAIL_LARGE, Define.SOUNDCLOUD_THUMNAIL_T500X500);
		}
		headerView.settingDetailHeaderView(playlistTitle, playlistThumnailUrl, playlistTrackNum);

		playListDetailView = (SearchPlayListDetailView) findViewById(R.id.search_track_list_view);
		playListDetailView.addHeaderView(headerView);
		playListDetailView.setRefreshCallback(this);

		playListDetailAdapter = new SearchPlayListDetailAdapter(this);
		playListDetailParser = new SearchListParser();

		loadingBar = (ProgressBar)findViewById(R.id.loading_progressbar);
		loadingBar.setVisibility(View.VISIBLE);

		//Mediaplayer
		slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.media_player_drawer_layout);
		mediaPlayerViewUtils = new MusicStyleListMediaPlayerViewUtils();
		mediaPlayerViewUtils.initialMediaPlayerView(this, slidingUpPanel, 
				getSupportFragmentManager().findFragmentById(R.id.media_player_handle_fragment),
				getSupportFragmentManager().findFragmentById(R.id.media_player_content_fragment));

		onRequestStart(this, this);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandlerDisplay = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(!mediaPlayerViewUtils.displayMediaPlayer(getApplicationContext(), slidingUpPanel)){
				sendEmptyMessageDelayed(0, 1000);
			}
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		mHandlerDisplay.sendEmptyMessage(0);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandlerDisplay.removeMessages(0);
	}

	@Override
	protected int registerContentView() {
		return R.layout.activity_playlistdetail_activity;
	}

	@Override
	public int getRequestType() {
		return BaseConsts.REQUEST_TYPE_GET;
	}

	@Override
	public int getResultType() {
		return BaseConsts.RESULT_TYPE_JSON;
	}

	@Override
	public String onRequestGetParameter() {
		return ParameterUrl.SearchPlayListDetail(requestId, searchIndex);
	}

	@Override
	public String onRequestGetUrl() {
		if(requestType == Define.YOUTUBE_SEARCH){
			return RequestUrl.SearchYoutubePlayListDetailUrl();
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return RequestUrl.SearchSoundCloudPlayListDetailUrl();
		}

		return null;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			playListDetailParser.setJsonObject((JSONObject)result);
			playListDetailParser.parseJsonData();

			if(!playListDetailParser.getList().isEmpty()){

				if(playListDetailView.getAdapter() == null){
					playListDetailAdapter.setList(playListDetailParser.getList());
					playListDetailView.setAdapter(playListDetailAdapter);
				} else {
					playListDetailAdapter.notifyDataSetChanged();
				}
			}
		}

		playListDetailView.alertRefresh(false);
		loadingBar.setVisibility(View.GONE);
	}
	
	@Override
	public void onRequestError(int errorType) {
		super.onRequestError(errorType);
		
		if(errorType == BaseConsts.HTTP_STATUS_SERVICE_UNAVAILABLE){
			if(searchIndex > Define.SEARCH_INDEX){
				searchIndex--;
			}
		}
		
		loadingBar.setVisibility(View.GONE);
	}

	public void indexOrganizer(int command) {
		if(command == Define.INDEX_INITIALIZE_COMMEND){
			searchIndex = Define.SEARCH_INDEX;
		} else {
			searchIndex++;
		}
	}

	@Override
	public void onRefresh() {
		playListDetailView.alertRefresh(true);
		indexOrganizer(Define.INDEX_PLUS_COMMEND);
		onRequestStart(getApplicationContext(), this);
	}
	
	//setTrack MediaPlayer
	public void setTrack(ArrayList<BaseModel> trackList, int playTrackNum){
		mediaPlayerViewUtils.exPandPanel(slidingUpPanel);
		mediaPlayerViewUtils.setTrack(trackList, playTrackNum);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        this.finish();
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
}
