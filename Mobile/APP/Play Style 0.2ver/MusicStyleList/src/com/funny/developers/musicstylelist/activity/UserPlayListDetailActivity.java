package com.funny.developers.musicstylelist.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.adapter.UserPlayListTrackAdapter;
import com.funny.developers.musicstylelist.baseactivity.BaseActivity;
import com.funny.developers.musicstylelist.dao.UserPlayListDao;
import com.funny.developers.musicstylelist.definition.Define;
import com.funny.developers.musicstylelist.definition.NotifyUrlDefine;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerViewUtils;
import com.funny.developers.musicstylelist.util.DigitUtils;
import com.funny.developers.musicstylelist.util.NavigationUtils;
import com.funny.developers.musicstylelist.util.SettingUtils;
import com.funny.developers.musicstylelist.view.DetailHeaderView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class UserPlayListDetailActivity extends BaseActivity implements OnItemClickListener{

	private Toolbar userPlayListDetailActivityToolBar;

	private ContentObserver mediaPlayerEmptyObserver;

	//media player fragment
	private MusicStyleListMediaPlayerViewUtils mediaPlayerViewUtils;
	private SlidingUpPanelLayout slidingUpPanel;

	private DetailHeaderView headerView;

	private UserPlayListDao userPlayListDao;

	private ListView userPlayListDetailView;
	private UserPlayListTrackAdapter userPlayListDetailAdapter;

	private ArrayList<BaseModel> trackList;

	private int folderNo;
	private int requestType;
	private String playlistTitle;
	private String playlistThumnailUrl;
	private int playlistTrackNum;

	private ContentObserver datasetChangeObserver;

	//AdView
	private AdView adView;

	private void getExtras(){
		Intent intent = getIntent();

		folderNo = intent.getIntExtra(Define.USER_PLAYLISTDETAIL_FOLDER_NO, 0);
		requestType = intent.getIntExtra(Define.USER_PLAYLISTDETAIL_REQUEST_TYPE, Define.YOUTUBE_TRACK);
		playlistTitle = intent.getStringExtra(Define.USER_PLAYLISTDETAIL_TRACK_LIST_TITLE);
		playlistThumnailUrl = intent.getStringExtra(Define.USER_PLAYLISTDETAIL_THUMNAIL_URL);
		playlistTrackNum = intent.getIntExtra(Define.USER_PLAYLISTDETAIL_TRACK_LIST_NUM, 0);
	}

	@Override
	protected void settingView() {
		getExtras();

		// 리소스로 AdView를 검색하고 요청을 로드합니다.
		adView = (AdView)this.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);

		userPlayListDetailActivityToolBar = (Toolbar)findViewById(R.id.userplaylistdetail_activity_toolbar);
		userPlayListDetailActivityToolBar.setTitle("PlayList");

		setSupportActionBar(userPlayListDetailActivityToolBar);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);

		headerView = new DetailHeaderView(getApplicationContext());
		headerView.setLayoutParams(new AbsListView.LayoutParams(metrics.widthPixels, metrics.widthPixels));

		if(requestType == Define.YOUTUBE_SEARCH){
			playlistThumnailUrl = DigitUtils.changeImageUrl(playlistThumnailUrl, Define.YOUTUBE_THUMNAIL_DEFAULT, Define.YOUTUBE_THUMNAIL_HQDEFAULT);
		} else {
			playlistThumnailUrl = DigitUtils.changeImageUrl(playlistThumnailUrl, Define.SOUNDCLOUD_THUMNAIL_LARGE, Define.SOUNDCLOUD_THUMNAIL_T500X500);
		}

		headerView.settingDetailHeaderView(playlistTitle, playlistThumnailUrl, playlistTrackNum);

		userPlayListDetailView = (ListView)findViewById(R.id.track_list_view);
		userPlayListDetailView.addHeaderView(headerView);

		slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.media_player_drawer_layout);
		mediaPlayerViewUtils = new MusicStyleListMediaPlayerViewUtils();
		mediaPlayerViewUtils.initialMediaPlayerView(this, slidingUpPanel, 
				getSupportFragmentManager().findFragmentById(R.id.media_player_handle_fragment),
				getSupportFragmentManager().findFragmentById(R.id.media_player_content_fragment));

		datasetChangeObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				setList();
			}
		};

		getContentResolver().registerContentObserver(NotifyUrlDefine.URI_NOTIFY_DATASET_CHANGE, 
				false, datasetChangeObserver);

		setList();

		mediaPlayerEmptyObserver = new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				mHandlerDisplay.sendEmptyMessage(Define.HANDLER_WHAT);
			}
		};

		getContentResolver().registerContentObserver(NotifyUrlDefine.URI_EMPTY_MEDIAPLAYER, 
				false, mediaPlayerEmptyObserver);
	}

	public void setList() {
		String whereClause = "where " + UserPlayListDao.FOLDER_NO + " = " + folderNo;
		userPlayListDao = new UserPlayListDao(getApplicationContext());
		trackList = userPlayListDao.getData(whereClause);
		userPlayListDao.close();

		if(trackList != null) {
			if(userPlayListDetailView.getAdapter() == null) {
				userPlayListDetailAdapter = new UserPlayListTrackAdapter(this);
				userPlayListDetailAdapter.setList(trackList);
				userPlayListDetailView.setAdapter(userPlayListDetailAdapter);
				userPlayListDetailView.setOnItemClickListener(this);
			} else {
				userPlayListDetailAdapter.setList(trackList);
				userPlayListDetailAdapter.notifyDataSetChanged();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandlerDisplay = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(!mediaPlayerViewUtils.displayMediaPlayer(getApplicationContext(), slidingUpPanel)){
				sendEmptyMessageDelayed(Define.HANDLER_WHAT, Define.HANDLER_DISPLAY_DELAY_TIME);
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		mHandlerDisplay.sendEmptyMessage(Define.HANDLER_WHAT);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandlerDisplay.removeMessages(Define.HANDLER_WHAT);
		getContentResolver().unregisterContentObserver(datasetChangeObserver);
		getContentResolver().unregisterContentObserver(mediaPlayerEmptyObserver);

		if(adView != null){
			adView.destroy();
		}
	}

	@Override
	protected int registerContentView() {
		return R.layout.activity_userplaylistdetail_activity;
	}

	@Override
	public int getRequestType() {
		return 0;
	}

	@Override
	public int getResultType() {
		return 0;
	}

	@Override
	public String onRequestGetParameter() {
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		return null;
	}

	@Override
	public void onRequestResult(Object arg0, int arg1) {}

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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position == 0) {
			return;
		}

		ArrayList<BaseModel> trackList = userPlayListDetailAdapter.getList();
		SearchTrackListModel model = (SearchTrackListModel)trackList.get(position - 1);

		if(model.onlyYoutube == Define.ONLY_YOUTUBE_TYPE){
			NavigationUtils.goYoutubePlayer(getApplicationContext(), trackList, position - 1);
		} else {
			if(model.trackType == Define.YOUTUBE_TRACK){
				if(SettingUtils.getUseYTPlayerType(getApplicationContext())){
					NavigationUtils.goYoutubePlayer(this, trackList, position - 1);
				} else {
					setTrack(trackList, position - 1);
				}
			} else {
				setTrack(trackList, position - 1);
			}
		}
	}
}
