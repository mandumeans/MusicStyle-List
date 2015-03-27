package com.funny.developers.musicstylelist.actionbar.listener;

import java.util.ArrayList;
import java.util.Calendar;

import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.MenuItem;

import com.funny.developers.musicstylelist.basefragment.BaseFragment;

public class SearchViewListener implements OnQueryTextListener{
	
	public interface SearchQuery{
		public void setSearchQueryType(String query, int type);
	}
	
	private MenuItem mSearchItem;
	private ArrayList<BaseFragment> fragmentList;
	private int searchType = 0;
	private long lastSearchTime = 0;
	
	public void setSearchItem(MenuItem searchItem){
		mSearchItem = searchItem;
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		
		long actualSearchTime = (Calendar.getInstance()).getTimeInMillis();

        if (actualSearchTime > lastSearchTime + 1000)   // Only one search every seond to avoid key-down & key-up
        {
            lastSearchTime = actualSearchTime;
            for(int i = 0; i < fragmentList.size(); i++){
            	fragmentList.get(i).setSearchQueryType(query, searchType);
            }
        }
        
        mSearchItem.collapseActionView();
        
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		return false;
	}
	
	public void setFragmentList(ArrayList<BaseFragment> fragmentList) {
		this.fragmentList = fragmentList;
	}
	
	public void setSearchType(int type) {
		searchType = type;
	}
}
