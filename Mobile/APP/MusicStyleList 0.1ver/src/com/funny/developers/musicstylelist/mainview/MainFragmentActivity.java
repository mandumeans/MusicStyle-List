package com.funny.developers.musicstylelist.mainview;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.funny.developers.musicstylelist.baseactivity.BaseFragmentActivity;
import com.funny.developers.musicstylelist.baseactivity.R;

public class MainFragmentActivity extends BaseFragmentActivity{

	@Override
	protected void settingView() {
		baseViewpager = (ViewPager)findViewById(R.id.mainfragment_viewpager);
		registerFragmentPagerAdapter(0);
		
		Log.d("Error", "MainFragmentActivity baseFragmentPagerAdapter new");
		
		baseViewpager.setAdapter(baseFragmentPagerAdapter);
		
		Log.d("Error", "MainFragmentActivity baseFragmentPagerAdapter return");
	}

	@Override
	protected int registerContentView() {
		return R.layout.activity_basefragment;
	}
	
	//프레그먼트 페이져 어뎁터 타입을 정해준다.
	public void registerFragmentPagerAdapter(int fragmentPagerAdapterType){
		
		switch(fragmentPagerAdapterType){
		case 0:
			baseFragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
			break;
			
		default :
			baseFragmentPagerAdapter = null;
			break;
		}
	}

}
