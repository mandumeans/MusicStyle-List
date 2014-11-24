package com.funny.developers.musicstylelist.actionbar.listener;

import com.funny.developers.musicstylelist.basefragment.BaseFragment;
import android.widget.SearchView.OnQueryTextListener;

public class SearchViewListener implements OnQueryTextListener{
	
	public interface SearchQuery{
		public void setSearchQueryType(String query, int type);
	}
	
	private BaseFragment tempFragment = null;
	private int searchType = 0;
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		
		tempFragment.setSearchQueryType(query, searchType);
		
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		
		return false;
	}
	
	//�����׸�Ʈ ���� Ȯ��
	public void setNowFragment(BaseFragment nowFragment) {
		tempFragment = nowFragment;
	}
	
	//Spinner���� ���õ� searching Ÿ�� Ȯ��
	public void setSearchType(int type) {
		searchType = type;
	}
}
