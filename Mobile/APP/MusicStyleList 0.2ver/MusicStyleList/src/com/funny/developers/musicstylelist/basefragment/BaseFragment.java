package com.funny.developers.musicstylelist.basefragment;

import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask;
import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask.OnRequestInterFace;
import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener.SearchQuery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

abstract public class BaseFragment extends Fragment implements SearchQuery, OnRequestInterFace{
	
	//뷰를 세팅하는 추상메소드
	abstract public void settingView();
	
	//메인 Content 뷰를 적용하는 추상메소드
	abstract public int registerContentView();
	
	protected BaseAsyncTask connectVender;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
        return inflater.inflate(registerContentView(), container, false);
    }
	
	@Override
	public void onStart() {
		super.onStart();
		settingView();
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
	public void onDestroy() {
		super.onDestroy();
	}
	
	public void onRequestStart(OnRequestInterFace callback){
		connectVender = new BaseAsyncTask(getActivity(), callback);
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
}
