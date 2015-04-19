package com.funny.developers.musicstylelist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public abstract class PullToRefreshView extends ListView implements OnScrollListener{

	public interface OnPullToRefresh{
		public void onRefresh();
	}
	
	protected LayoutInflater inflater = null;
	protected Context mContext = null;
	
	protected View headerView = null;
	protected View footerView = null;
	protected int mLimitDip = 0;
	
	private boolean lastItemVisibleFlag = false;
	
	protected OnPullToRefresh mCallback = null;
	
	public PullToRefreshView(Context context) {
		super(context);
	}
	
	public PullToRefreshView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public PullToRefreshView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	public void setRefreshCallback(OnPullToRefresh callback){
		mCallback = callback;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
            if(!footerView.isShown()){
            	mCallback.onRefresh();
            }
        }
	}
	
	public void alertRefresh(boolean alert){
		if(alert){
			footerView.setVisibility(View.VISIBLE);
		} else {
			footerView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1); 
	}
}
