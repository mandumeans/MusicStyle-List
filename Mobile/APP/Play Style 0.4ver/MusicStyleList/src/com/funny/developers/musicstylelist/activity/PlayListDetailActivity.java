package com.funny.developers.musicstylelist.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.funny.developers.musicstylelist.requesturl.RequestUrl;
import com.funny.developers.musicstylelist.util.DigitUtils;
import com.funny.developers.musicstylelist.util.NavigationUtils;
import com.funny.developers.musicstylelist.view.DetailHeaderView;
import com.funny.developers.musicstylelist.view.PullToRefreshView.OnPullToRefresh;
import com.funny.developers.musicstylelist.view.SearchPlayListDetailView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PlayListDetailActivity extends BaseActivity implements OnPullToRefresh{

	private Toolbar playListDetailActivityToolBar;

	private DetailHeaderView headerView;

	private SearchPlayListDetailView playListDetailView;
	private SearchPlayListDetailAdapter playListDetailAdapter;
	private SearchListParser playListDetailParser;
	private ProgressBar loadingBar;

	private String requestId;
	private int requestType;

	private String playlistTitle;
	private String playlistThumnailUrl;
	private int playlistTrackNum;

	//AdView
	private AdView adView;

	private void getExtras(){
		Intent intent = getIntent();

		requestId = intent.getStringExtra(Define.PLAYLISTDETAIL_REQUEST_ID);
		requestType = intent.getIntExtra(Define.PLAYLISTDETAIL_REQUEST_TYPE, Define.YOUTUBE_SEARCH);

		playlistTitle = intent.getStringExtra(Define.PLAYLISTDETAIL_TRACK_LIST_TITLE);
		playlistThumnailUrl = intent.getStringExtra(Define.PLAYLISTDETAIL_THUMNAIL_URL);
		playlistTrackNum = intent.getIntExtra(Define.PLAYLISTDETAIL_TRACK_LIST_NUM, 0);
	}

	@Override
	protected void settingView() {

		// 리소스로 AdView를 검색하고 요청을 로드합니다.
		adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

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
		headerView.settingPlayButtonClickListener(mPlayButtonClickListener);

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

		onRequestStart(getApplicationContext(), this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if(adView != null){
			adView.destroy();
		}
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
		if(requestType == Define.YOUTUBE_SEARCH){
			return ParameterUrl.SearchYoutubePlayListDetail(requestId, playListDetailParser.getPageToken());
		} else if(requestType == Define.SOUND_CLOUD_SEARCH){
			return ParameterUrl.SearchSoundCloudPlayListDetail(requestId, playListDetailParser.getPageToken());
		}

		return null;
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
		Log.d("Error", "onRequestCancle");

		super.onRequestError(errorType);
		loadingBar.setVisibility(View.GONE);
	}

	@Override
	public void onRefresh() {
		playListDetailView.alertRefresh(true);
		onRequestStart(getApplicationContext(), this);
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
	
	private void playTrackList(ArrayList<BaseModel> trackList, int position){
		NavigationUtils.goPlayerActivity(this, trackList, position);
	}
	
	private OnClickListener mPlayButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			ArrayList<BaseModel> trackList = playListDetailAdapter.getList();
			playTrackList(trackList, 0);
		}
	};
}
