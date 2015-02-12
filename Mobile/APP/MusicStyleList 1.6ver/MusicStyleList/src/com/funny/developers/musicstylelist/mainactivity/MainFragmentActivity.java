package com.funny.developers.musicstylelist.mainactivity;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener;
import com.funny.developers.musicstylelist.actionbar.listener.SpinnerListener;
import com.funny.developers.musicstylelist.baseactivity.BaseFragmentActivity;
import com.funny.developers.musicstylelist.definition.FragmentPagerAdapterType;
import com.funny.developers.musicstylelist.fragment.FragmentForMediaPlayer;
import com.funny.developers.musicstylelist.fragment.FragmentForMediaPlayerHandler;
import com.funny.developers.musicstylelist.fragment.FragmentForPlayListDetail;
import com.funny.developers.musicstylelist.fragment.FragmentForUserPlayListDetail;
import com.funny.developers.musicstylelist.fragment.pageradapter.PlayListFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.pageradapter.SearchFragmentPagerAdapter;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.view.CustomSlidingDrawer;
import com.funny.developers.musicstylelist.view.CustomSlidingDrawer.OnDrawerScrollListener;
import com.funny.developers.musicstylelist.view.SlidingTabLayout;

public class MainFragmentActivity extends BaseFragmentActivity implements OnDrawerScrollListener{

	//action bar
	protected Toolbar mainFragmentActivityToolBar = null;
	
	//navigation items
	private String[] navigationTitles = null;
	private DrawerLayout navigationLayout = null;
	private ListView navigationList = null;
	private ArrayAdapter<String> navigationAdapter = null;
	private ActionBarDrawerToggle navigationToggle = null;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	
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
	public FragmentForMediaPlayerHandler mediaPlayerHanderView = null;

	//play list detail
	public FragmentForPlayListDetail playListDetailView = null;
	
	//user play list detail
	public FragmentForUserPlayListDetail userPlayListDetailView = null;

	//FragmentTransaction
	private FragmentTransaction transaction = null;
	
	//Custom Sliding Drawer
	private CustomSlidingDrawer customSlidingDrawer = null;
	
	//Display
	Display display = null;
	
	//Viewtemp
	View tempMain_Vacancy_View = null;
	View tempPlaylist_Vacancy_View = null;
	
	@Override
	protected void settingView() {
		
		//display
		display = getWindowManager().getDefaultDisplay();
		
		mainFragmentActivityToolBar = (Toolbar) findViewById(R.id.mainfragmentactivity_toolbar);
		setSupportActionBar(mainFragmentActivityToolBar);
		
		//navigation setting
		mTitle = mDrawerTitle = getTitle();
		navigationTitles = getResources().getStringArray(R.array.navigation_titles_array);
		navigationLayout = (DrawerLayout) findViewById(R.id.navigation_layout);
		navigationList = (ListView) findViewById(R.id.navigation_drawer);
		
		navigationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		navigationAdapter.addAll(navigationTitles);

		//navigationLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		navigationList.setAdapter(navigationAdapter);
		navigationList.setOnItemClickListener(new navigationItemClickListener());

		navigationToggle = new ActionBarDrawerToggle(this, navigationLayout, R.string.app_name, R.string.app_name);
		navigationLayout.setDrawerListener(navigationToggle);
		
		//Setting search spinner in search view or in playlist view
		searchViewAdapter = ArrayAdapter.createFromResource(this, R.array.action_search_in_searchview_array, 
				android.R.layout.simple_spinner_dropdown_item);

		playlistViewAdapter = ArrayAdapter.createFromResource(this, R.array.action_search_in_playlistview_array, 
				android.R.layout.simple_spinner_dropdown_item);

		//Setting actionbar listener
		searchViewlistener = new SearchViewListener();
		spinnerListener = new SpinnerListener();
		spinnerListener.setSearchViewListener(searchViewlistener);

		//Setting viewpager
		baseViewpager = (ViewPager)findViewById(R.id.mainfragment_viewpager);
		baseViewpager.setPageMargin(3);
		baseViewpager.setPageMarginDrawable(R.color.Grey);
		
		baseSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.mainfragment_viewpager_tabindicator);
		//baseSlidingTabLayout.setDividerColors("#E57373");
		baseSlidingTabLayout.setDividerColors("#E0E0E0");
		baseSlidingTabLayout.setSelectedIndicatorColors("#B71C1C");
		baseSlidingTabLayout.setTabTextColor("#424242");
		
		registerFragmentPagerAdapter(FragmentPagerAdapterType.SEARCH_FRAGMENT_PAGER);
		
		//Setting playListDetailView
		playListDetailView = new FragmentForPlayListDetail(this);
		
		//Setting userPlayListDetailView
		userPlayListDetailView = new FragmentForUserPlayListDetail(this);
		
		//Setting CustomSlidingDrawer
		customSlidingDrawer = (CustomSlidingDrawer)findViewById(R.id.media_player_drawer_layout);
		customSlidingDrawer.setOnDrawerScrollListener(this);
		customSlidingDrawer.setVisibility(View.INVISIBLE);

		//Setting Media player
		mediaPlayerView = new FragmentForMediaPlayer();
		mediaPlayerHanderView = new FragmentForMediaPlayerHandler();
		mediaPlayerView.setMediaPlayerHandler(mediaPlayerHanderView);
		
		
		FrameLayout.LayoutParams framelayoutParamsMediaplayerHandler = new FrameLayout.LayoutParams(display.getWidth(),
				display.getHeight() / 8);
		FrameLayout framelayoutMediaplayerHandler = (FrameLayout) findViewById(R.id.media_palyer_handle);
		framelayoutMediaplayerHandler.setLayoutParams(framelayoutParamsMediaplayerHandler);
		
		//Temp For List overlay
		LinearLayout.LayoutParams tempViewlayoutParams = new LinearLayout.LayoutParams(display.getWidth(),
				display.getHeight() / 8);
		tempMain_Vacancy_View = (View)findViewById(R.id.temp_main_vacancy_view);
		tempMain_Vacancy_View.setLayoutParams(tempViewlayoutParams);
		
		tempPlaylist_Vacancy_View = (View)findViewById(R.id.temp_play_list_vacancy_view);
		tempPlaylist_Vacancy_View.setLayoutParams(tempViewlayoutParams);
		
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.media_player_layout, mediaPlayerView);
		transaction.commit();
		
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.media_palyer_handle, mediaPlayerHanderView);
		transaction.commit();
	}

	//View controller
	public void settingPlayListDetailView(String requestId, int requestType){
		playListDetailView.setInfoId(requestId, requestType);	
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.play_list_detail_layout, playListDetailView);
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	public void settingUserPlayListDetailView(ArrayList<BaseModel> trackList){
		userPlayListDetailView.setTrackList(trackList);
		transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.play_list_detail_layout, userPlayListDetailView);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	public void settingMediaPlayerView(ArrayList<BaseModel> trackList, int playTrackNum){
		
		if(!customSlidingDrawer.isShown()){
			customSlidingDrawer.setVisibility(View.VISIBLE);
			tempMain_Vacancy_View.setVisibility(View.VISIBLE);
			tempPlaylist_Vacancy_View.setVisibility(View.VISIBLE);
		}
		
		mediaPlayerView.setPlayTrackList(trackList, playTrackNum);
	}
	
	  /* The click listner for ListView in the navigation drawer */
    private class navigationItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
    		navigationLayout.closeDrawer(navigationList);
    		registerFragmentPagerAdapter(position);
        }
    }

	@Override
	protected int registerContentView() {
		return R.layout.activity_mainfragment_activity;
	}
	
	public void registerFragmentPagerAdapter(int fragmentPagerAdapterType){

		if(baseFragmentPagerAdapter != null){
			switch(fragmentPagerAdapterType){
			case FragmentPagerAdapterType.SEARCH_FRAGMENT_PAGER:
				if(baseFragmentPagerAdapter instanceof SearchFragmentPagerAdapter){
					return;
				}
				break;
				
			case FragmentPagerAdapterType.PLAYLIST_FRAGMENT_PAGER:
				if(baseFragmentPagerAdapter instanceof PlayListFragmentPagerAdapter){
					return;
				}
				break;
			}
			
			baseFragmentPagerAdapter = null;
		}
		
		switch(fragmentPagerAdapterType){
		case FragmentPagerAdapterType.SEARCH_FRAGMENT_PAGER:
			baseFragmentPagerAdapter = new SearchFragmentPagerAdapter(getSupportFragmentManager());
			break;
			
		case FragmentPagerAdapterType.PLAYLIST_FRAGMENT_PAGER:
			baseFragmentPagerAdapter = new PlayListFragmentPagerAdapter(getSupportFragmentManager());
			break;

		default :
			baseFragmentPagerAdapter = null;
			break;
		}

		baseFragmentPagerAdapter.setSearchListener(searchViewlistener);
		baseViewpager.setAdapter(baseFragmentPagerAdapter);
		baseSlidingTabLayout.setViewPager(baseViewpager);
	}

	//setting actionbar menu(action item)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);

		//Menu Spinner setting
		menuSpinner = (Spinner) MenuItemCompat.getActionView(menu.findItem(R.id.menu_spinner));
		menuSpinner.setAdapter(searchViewAdapter);
		menuSpinner.setOnItemSelectedListener(spinnerListener);

		//SearchView set listener
		menuSearch = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
		menuSearch.setOnQueryTextListener(searchViewlistener);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (navigationToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		navigationToggle.syncState();
	}

	@Override
	public void onScrollStarted() {}

	@Override
	public void onScrollEnded() {}

	@Override
	public void onScrollCheck(boolean check) {
		if(check){
			
			getSupportActionBar().show();
		} else {
			getSupportActionBar().hide();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
}
