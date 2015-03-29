package com.funny.developers.musicstylelist.baseactivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask;
import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask.OnRequestInterFace;

abstract public class BaseActivity extends ActionBarActivity implements OnRequestInterFace{
	
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
		if(connectVender != null){
			connectVender.cancel(true);
		}
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
