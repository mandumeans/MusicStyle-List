package com.funny.developers.musicstylelist.mainactivity;

import java.util.ArrayList;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
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

public class MainFragmentActivity extends BaseFragmentActivity implements OnDrawerScrollListener{

	//action bar
	protected ActionBar mainFragmentActivityActionBar = null;

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

	@Override
	protected void settingView() {

		//navigation setting
		mTitle = mDrawerTitle = getTitle();
		navigationTitles = getResources().getStringArray(R.array.navigation_titles_array);
		navigationLayout = (DrawerLayout) findViewById(R.id.navigation_layout);
		navigationList = (ListView) findViewById(R.id.navigation_drawer);
		
		navigationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		navigationAdapter.addAll(navigationTitles);

		navigationLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		navigationList.setAdapter(navigationAdapter);
		navigationList.setOnItemClickListener(new navigationItemClickListener());

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		navigationToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				navigationLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description for accessibility */
				R.string.drawer_close  /* "close drawer" description for accessibility */
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		navigationLayout.setDrawerListener(navigationToggle);

		//Setting action bar
		mainFragmentActivityActionBar = getActionBar();
		mainFragmentActivityActionBar.setDisplayHomeAsUpEnabled(true);
		mainFragmentActivityActionBar.setHomeButtonEnabled(true);

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
		mediaPlayerView.setPlayTrackList(trackList, playTrackNum);
		customSlidingDrawer.setVisibility(View.VISIBLE);
	}
	
	  /* The click listner for ListView in the navigation drawer */
    private class navigationItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	
        	registerFragmentPagerAdapter(position);
    		navigationLayout.closeDrawer(navigationList);
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
			getActionBar().show();
		} else {
			getActionBar().hide();
		}
	}
}
