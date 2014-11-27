package com.funny.developers.musicstylelist.baseactivity;

import com.funny.developers.musicstylelist.basepageradapter.BaseFragmentPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

abstract public class BaseFragmentActivity extends FragmentActivity {
	
	//�並 �����ϴ� �߻�޼ҵ�
	abstract protected void settingView();
	
	//���� Content �並 �����ϴ� �߻�޼ҵ�
	abstract protected int registerContentView();
	
	protected ViewPager baseViewpager;
	protected BaseFragmentPagerAdapter baseFragmentPagerAdapter;
	
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
}
