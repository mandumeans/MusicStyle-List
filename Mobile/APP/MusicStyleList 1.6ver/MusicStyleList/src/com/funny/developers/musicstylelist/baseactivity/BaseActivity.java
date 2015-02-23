package com.funny.developers.musicstylelist.baseactivity;

import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask;
import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask.OnRequestInterFace;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

abstract public class BaseActivity extends Activity implements OnRequestInterFace{
	
	abstract protected void settingView();
	abstract protected int registerContentView();
	
	protected BaseAsyncTask connectVender;
	
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
	
	public void onRequestStart(Context context, OnRequestInterFace callback){
		connectVender = new BaseAsyncTask(context, callback);
		connectVender.execute();
	}
	
	@Override
	public void onRequestError(int errorType) {
		//process error 
		onRequestCancle();
	}
	
	@Override
	public void onRequestCancle() {
		connectVender.cancel(true);
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
