package com.funny.developers.musicstylelist.actionbar.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SpinnerListener implements OnItemSelectedListener{

	private SearchViewListener searchViewListener = null;
	
	public void setSearchViewListener(SearchViewListener searchViewListener){
		this.searchViewListener = searchViewListener;
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		
		searchViewListener.setSearchType(position + 1);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}

}
