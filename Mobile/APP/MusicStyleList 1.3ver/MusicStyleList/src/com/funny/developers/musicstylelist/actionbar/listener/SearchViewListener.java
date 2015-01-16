package com.funny.developers.musicstylelist.actionbar.listener;

import java.util.Calendar;

import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import android.widget.SearchView.OnQueryTextListener;

public class SearchViewListener implements OnQueryTextListener{
	
	public interface SearchQuery{
		public void setSearchQueryType(String query, int type);
	}
	
	private BaseFragment tempFragment = null;
	private int searchType = 0;
	long lastSearchTime = 0;
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		
		long actualSearchTime = (Calendar.getInstance()).getTimeInMillis();

        if (actualSearchTime > lastSearchTime + 1000)   // Only one search every seond to avoid key-down & key-up
        {
            lastSearchTime = actualSearchTime;
            tempFragment.setSearchQueryType(query, searchType);
        }
        
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		return false;
	}
	
	//프레그먼트 종류 확인
	public void setNowFragment(BaseFragment nowFragment) {
		tempFragment = nowFragment;
	}
	
	//Spinner에서 선택된 searching 타입 확인
	public void setSearchType(int type) {
		searchType = type;
	}
}
