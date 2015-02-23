package com.funny.developers.musicstylelist.basefragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask;
import com.funny.developers.baseconnectionlib.asnycktask.BaseAsyncTask.OnRequestInterFace;
import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.actionbar.listener.SearchViewListener.SearchQuery;
import com.funny.developers.musicstylelist.definition.Define;

abstract public class BaseFragment extends Fragment implements SearchQuery, OnRequestInterFace{
	
	public Context mContext = null;
	
	public int search_index = Define.SEARCH_INDEX;
	
	public ProgressBar loadingBar = null;
	
	abstract public void settingView();
	
	abstract public int registerContentView();
	
	protected BaseAsyncTask connectVender;
	
	public void indexOrganizer(int command) {
		if(command == Define.INDEX_INITIALIZE_COMMEND){
			search_index = Define.SEARCH_INDEX;
		} else {
			search_index++;
		}
	}
	
	public BaseFragment(){}
	
	public BaseFragment(Context context){
		mContext = context;
	}
	
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
		loadingBar = (ProgressBar) getView().findViewById(R.id.loading_progressbar);
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
	
	public void onRequestStart(Context context, OnRequestInterFace callback, boolean checkLoading){
		connectVender = new BaseAsyncTask(context, callback);
		connectVender.execute();
		
		if(loadingBar != null && checkLoading){
			loadingBar.setVisibility(View.VISIBLE);
		}
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
