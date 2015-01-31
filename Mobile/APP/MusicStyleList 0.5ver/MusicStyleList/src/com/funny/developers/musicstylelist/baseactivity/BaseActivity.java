package com.funny.developers.musicstylelist.baseactivity;

import android.app.Activity;
import android.os.Bundle;

abstract public class BaseActivity extends Activity {
	
	abstract protected void settingView();
	abstract protected int registerContentView();
	
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
