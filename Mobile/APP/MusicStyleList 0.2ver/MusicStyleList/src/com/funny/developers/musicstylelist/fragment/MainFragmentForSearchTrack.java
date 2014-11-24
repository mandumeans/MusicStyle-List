package com.funny.developers.musicstylelist.fragment;

import android.util.Log;

import com.funny.developers.baseconnectionlib.consts.BaseConsts;
import com.funny.developers.musicstylelist.baseactivity.R;
import com.funny.developers.musicstylelist.basefragment.BaseFragment;

public class MainFragmentForSearchTrack extends BaseFragment{

	@Override
	public void settingView() {
	}

	@Override
	public int registerContentView() {
		return R.layout.fragment_for_search_track;
	}

	@Override
	public void setSearchQueryType(String query, int type) {
		Log.d("Error", query + " " + type);
		
		onRequestStart(this);
	}

	@Override
	public int getRequestType() {
		return BaseConsts.REQUEST_TYPE_GET;
	}

	@Override
	public int getResultType() {
		return BaseConsts.RESULT_TYPE_JSON;
	}

	@Override
	public String onRequestGetParameter() {
		return null;
	}

	@Override
	public String onRequestGetUrl() {
		return null;
	}

	@Override
	public void onRequestResult(Object result, int resultType) {
		
		if(resultType == BaseConsts.RESULT_TYPE_JSON){
			
		} else {
			
		}
	}

}
