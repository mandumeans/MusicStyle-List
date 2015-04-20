package com.funny.developers.musicstylelist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.definition.FragmentPagerAdapterTypeDefine;

public class NavigationMenuAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private String[] menuList;
	
	public NavigationMenuAdapter (){
		super();
	}
	
	public NavigationMenuAdapter (Context context){
		super();
		mInflater = LayoutInflater.from(context);
	}
	
	public void setList(String[] menuList){
		this.menuList = menuList;
	}
	
	@Override
	public int getCount() {
		return menuList.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.layout_for_navigation_view, null);
			holder = new ViewHolder();
			
			holder.mLogo = (ImageView)convertView.findViewById(R.id.logo_image_view);
			holder.mTitle = (TextView)convertView.findViewById(R.id.title_text_view);
			
			convertView.setTag(holder);
		} else {
			
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(position == FragmentPagerAdapterTypeDefine.HOT_TRACK_FRAGMENT_PAGER){
			holder.mLogo.setImageResource(R.drawable.selector_navi_menu_hot_track);
		} else if(position == FragmentPagerAdapterTypeDefine.SEARCH_FRAGMENT_PAGER){
			holder.mLogo.setImageResource(R.drawable.selector_navi_menu_search);
		} else if(position == FragmentPagerAdapterTypeDefine.PLAYLIST_FRAGMENT_PAGER){
			holder.mLogo.setImageResource(R.drawable.selector_navi_menu_playlist);
		} else if(position == FragmentPagerAdapterTypeDefine.SETTINGS_FRAGMENT){
			holder.mLogo.setImageResource(R.drawable.navi_menu);
		}
		
		holder.mTitle.setText(menuList[position]);
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView mLogo = null;
		TextView mTitle  = null;
	}

}
