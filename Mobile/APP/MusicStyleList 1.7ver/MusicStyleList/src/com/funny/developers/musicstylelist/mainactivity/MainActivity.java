package com.funny.developers.musicstylelist.mainactivity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener;
import com.funny.developers.musicstylelist.actionbar.listener.SpinnerListener;
import com.funny.developers.musicstylelist.adapter.NavigationMenuAdapter;
import com.funny.developers.musicstylelist.baseactivity.BaseFragmentActivity;
import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.definition.FragmentPagerAdapterTypeDefine;
import com.funny.developers.musicstylelist.fragment.pageradapter.PlayListFragmentPagerAdapter;
import com.funny.developers.musicstylelist.fragment.pageradapter.SearchFragmentPagerAdapter;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.player.MusicStyleListMediaPlayerViewUtils;
import com.funny.developers.musicstylelist.util.ImageUtils;
import com.funny.developers.musicstylelist.util.NavigationUtils;
import com.funny.developers.musicstylelist.view.NaviHeaderView;
import com.funny.developers.musicstylelist.view.SlidingTabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends BaseFragmentActivity{

	//action bar
	protected Toolbar mainActivityToolBar;

	//navigation items
	private String[] navigationTitles;
	private DrawerLayout navigationLayout;
	private ListView navigationList;
	private NavigationMenuAdapter navigationAdapter;
	private ActionBarDrawerToggle navigationToggle;

	//menu type
	private int menuType = FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER;

	//spinner
	private SpinnerAdapter searchViewAdapter;
	private SpinnerAdapter playlistViewAdapter;

	//actionbar listener
	private SearchViewListener searchViewlistener;
	private SpinnerListener spinnerListener;

	//Media Player
	private MusicStyleListMediaPlayerViewUtils mediaPlayerViewUtils;
	private SlidingUpPanelLayout slidingUpPanel;
	
	@Override
	public void onStart() {
		super.onStart();
		
		ImageUtils.initialImageLoader(getApplicationContext());
	}

	@Override
	protected void settingView() {

		//toolbar
		mainActivityToolBar = (Toolbar) findViewById(R.id.mainactivity_toolbar);
		setSupportActionBar(mainActivityToolBar);

		//navigation setting
		navigationTitles = getResources().getStringArray(R.array.navigation_titles_array);
		getSupportActionBar().setTitle(navigationTitles[0]);
		
		navigationLayout = (DrawerLayout) findViewById(R.id.navigation_layout);
		navigationList = (ListView) findViewById(R.id.navigation_drawer);

		navigationAdapter = new NavigationMenuAdapter(getApplicationContext());
		navigationAdapter.setList(navigationTitles);

		//navigationLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		navigationList.setAdapter(navigationAdapter);
		navigationList.setOnItemClickListener(new navigationItemClickListener());
		navigationList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		navigationList.setItemChecked(0, true);

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
		baseViewpager.setPageMarginDrawable(R.color.grey);

		baseSlidingTabLayout = (SlidingTabLayout)findViewById(R.id.mainfragment_viewpager_tabindicator);
		baseSlidingTabLayout.setDividerColors("#E0E0E0");
		baseSlidingTabLayout.setSelectedIndicatorColors("#B71C1C");
		baseSlidingTabLayout.setTabTextColor("#424242");

		registerFragmentPagerAdapter(FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER);

		//Setting Media player
		slidingUpPanel = (SlidingUpPanelLayout)findViewById(R.id.media_player_drawer_layout);
		mediaPlayerViewUtils = new MusicStyleListMediaPlayerViewUtils();
		mediaPlayerViewUtils.initialMediaPlayerView(this, slidingUpPanel, 
				getSupportFragmentManager().findFragmentById(R.id.media_player_handle_fragment),
				getSupportFragmentManager().findFragmentById(R.id.media_player_content_fragment));
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
		ImageUtils.destoryImageLoader();
		mHandlerDisplay.removeMessages(0);
	}

	//setTrack MediaPlayer
	public void setTrack(ArrayList<BaseModel> trackList, int playTrackNum){
		mediaPlayerViewUtils.exPandPanel(slidingUpPanel);
		mediaPlayerViewUtils.setTrack(trackList, playTrackNum);
	}

	/* The click listner for ListView in the navigation drawer */
	private class navigationItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			navigationLayout.closeDrawer(navigationList);
			
			if(position == FragmentPagerAdapterTypeDefine.SETTINGS_FRAGMENT){
				NavigationUtils.goSettings(getApplicationContext());
			} else {
				registerFragmentPagerAdapter(position);
				getSupportActionBar().setTitle(navigationTitles[position]);
			}
		}
	}

	@Override
	protected int registerContentView() {
		return R.layout.activity_main_activity;
	}

	public void registerFragmentPagerAdapter(int fragmentPagerAdapterType){

		if(baseFragmentPagerAdapter != null){
			switch(fragmentPagerAdapterType){
			case FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER:
				if(baseFragmentPagerAdapter instanceof SearchFragmentPagerAdapter){
					return;
				}
				break;

			case FragmentPagerAdapterTypeDefine.PLAYLIST_FRAGMENT_PAGER:
				if(baseFragmentPagerAdapter instanceof PlayListFragmentPagerAdapter){
					return;
				}
				break;
			}

			baseFragmentPagerAdapter = null;
		}

		if(baseViewpager.getAdapter() != null){
			((BaseFragmentPagerAdapter)baseViewpager.getAdapter()).clearAll(getSupportFragmentManager());
		}

		switch(fragmentPagerAdapterType){
		case FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER:
			menuType = FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER;
			baseFragmentPagerAdapter = new SearchFragmentPagerAdapter(getApplicationContext(), getSupportFragmentManager());
			break;

		case FragmentPagerAdapterTypeDefine.PLAYLIST_FRAGMENT_PAGER:
			menuType = FragmentPagerAdapterTypeDefine.PLAYLIST_FRAGMENT_PAGER;
			baseFragmentPagerAdapter = new PlayListFragmentPagerAdapter(getApplicationContext(), getSupportFragmentManager());
			break;

		default :
			baseFragmentPagerAdapter = null;
			break;
		}

		baseFragmentPagerAdapter.setSearchListener(searchViewlistener);
		baseViewpager.setAdapter(baseFragmentPagerAdapter);
		baseSlidingTabLayout.setViewPager(baseViewpager);
		
		invalidateOptionsMenu();
	}

	//setting actionbar menu(action item)
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if(menuType > -1){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.main_activity_actions, menu);

			Spinner menuSpinner = (Spinner) MenuItemCompat.getActionView(menu.findItem(R.id.menu_spinner));
			
			switch(menuType){
			case FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER:
				menuSpinner.setAdapter(searchViewAdapter);
				break;

			case FragmentPagerAdapterTypeDefine.PLAYLIST_FRAGMENT_PAGER:
				menuSpinner.setAdapter(playlistViewAdapter);
				break;
			}
			
			menuSpinner.setOnItemSelectedListener(spinnerListener);

			//SearchView set listener
			SearchView menuSearch = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
			menuSearch.setOnQueryTextListener(searchViewlistener);
		}

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
}
