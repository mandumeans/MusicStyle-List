package com.funny.developers.musicstylelist.baseactivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;
import com.funny.developers.musicstylelist.view.SlidingTabLayout;

abstract public class BaseFragmentActivity extends ActionBarActivity {
	
	abstract protected void settingView();
	
	abstract protected int registerContentView();
	
	protected ViewPager baseViewpager;
	protected BaseFragmentPagerAdapter baseFragmentPagerAdapter;
	protected SlidingTabLayout baseSlidingTabLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(registerContentView());
		settingView();
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onRestart() {
		super.onRestart();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState){
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
	}
}
