package com.funny.developers.musicstylelist.actionbar.listener;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class SpinnerListener implements OnItemSelectedListener{

	private SearchViewListener searchViewListener = null;
	
	public void setSearchViewListener(SearchViewListener searchViewListener){
		this.searchViewListener = searchViewListener;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		searchViewListener.setSearchType(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

}
