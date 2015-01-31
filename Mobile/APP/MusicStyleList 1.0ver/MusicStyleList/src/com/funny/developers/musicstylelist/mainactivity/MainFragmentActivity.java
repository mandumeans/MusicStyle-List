package com.funny.developers.musicstylelist.mainactivity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener;
import com.funny.developers.musicstylelist.actionbar.listener.SpinnerListener;
import com.funny.developers.musicstylelist.baseactivity.BaseFragmentActivity;
import com.funny.developers.musicstylelist.baseactivity.R;
import com.funny.developers.musicstylelist.definition.FragmentPagerAdapterType;
import com.funny.developers.musicstylelist.fragment.FragmentForMediaPlayer;
import com.funny.developers.musicstylelist.fragment.FragmentForPlayListDetail;
import com.funny.developers.musicstylelist.fragment.pageradapter.SearchFragmentPagerAdapter;
import com.funny.developers.musicstylelist.model.BaseModel;

public class MainFragmentActivity extends BaseFragmentActivity{

	//action bar
	protected ActionBar mainFragmentActivityActionBar = null;
	
	//menu items
	protected Spinner menuSpinner = null;
	protected SearchView menuSearch = null;
	
	//spinner
	protected SpinnerAdapter searchViewAdapter = null;
	protected SpinnerAdapter playlistViewAdapter = null;
	
	//actionbar listener
	public SearchViewListener searchViewlistener = null;
	public SpinnerListener spinnerListener = null;
	
	//media player fragment
	public FragmentForMediaPlayer mediaPlayerView = null;
	
	//play list detail
	public FragmentForPlayListDetail playListDetailView = null;
	
	private FragmentTransaction transaction = null;
	
	@Override
	protected void settingView() {
		
		//setting search spinner in search view or in playlist view
		searchViewAdapter = ArrayAdapter.createFromResource(this, R.array.action_search_in_searchview_array, 
				android.R.layout.simple_spinner_dropdown_item);
		
		playlistViewAdapter = ArrayAdapter.createFromResource(this, R.array.action_search_in_playlistview_array, 
				android.R.layout.simple_spinner_dropdown_item);
	
		//setting actionbar listener
		searchViewlistener = new SearchViewListener();
		spinnerListener = new SpinnerListener();
		spinnerListener.setSearchViewListener(searchViewlistener);
		
		//setting action bar
		mainFragmentActivityActionBar = getActionBar();
		mainFragmentActivityActionBar.setTitle("Icon");
		
		//setting viewpager
		baseViewpager = (ViewPager)findViewById(R.id.mainfragment_viewpager);
		registerFragmentPagerAdapter(FragmentPagerAdapterType.SEARCH_FRAGMENT_PAGER);
		baseViewpager.setAdapter(baseFragmentPagerAdapter);
		
		//미리 생성
		playListDetailView = new FragmentForPlayListDetail(this);
		
		mediaPlayerView = new FragmentForMediaPlayer();
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.media_player_layout, mediaPlayerView);
		transaction.commit();
	}
	
	public void settingPlayListDetailView(String requestId, int requestType){
		
		playListDetailView.setInfoId(requestId, requestType);
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.play_list_detail_layout, playListDetailView);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void settingMediaPlayerView(ArrayList<BaseModel> trackList, int playTrackNum){
		
		mediaPlayerView.setPlayTrackList(trackList, playTrackNum);
	}

	@Override
	protected int registerContentView() {
		return R.layout.activity_mainfragmet_activity;
	}
	
	//프레그먼트 페이져 어뎁터 타입을 정해준다.
	public void registerFragmentPagerAdapter(int fragmentPagerAdapterType){
		
		switch(fragmentPagerAdapterType){
		case FragmentPagerAdapterType.SEARCH_FRAGMENT_PAGER:
			baseFragmentPagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager());
			break;
			
		default :
			baseFragmentPagerAdapter = null;
			break;
		}
		
		baseFragmentPagerAdapter.setSearchListener(searchViewlistener);
	}
	
	//setting actionbar menu(action item)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    
	    //Menu Spinner setting
	    menuSpinner = (Spinner) menu.findItem(R.id.menu_spinner).getActionView();
	    menuSpinner.setAdapter(searchViewAdapter);
	    menuSpinner.setOnItemSelectedListener(spinnerListener);
	    
	    //SearchView set listener
	    menuSearch = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    menuSearch.setOnQueryTextListener(searchViewlistener);
	    
	    return super.onCreateOptionsMenu(menu);
	}

}
