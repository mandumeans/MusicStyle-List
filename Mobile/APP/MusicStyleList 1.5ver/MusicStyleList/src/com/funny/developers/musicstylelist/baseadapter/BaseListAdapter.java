package com.funny.developers.musicstylelist.baseadapter;

import java.util.ArrayList;

import com.funny.developers.musicstylelist.model.BaseModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter extends BaseAdapter{
	protected Context mContext = null;
	protected ImageLoader imageLoader = null;
	protected ImageLoaderConfiguration imageConfig = null;
	protected ArrayList<BaseModel> mList = null;
	protected LayoutInflater mInflater = null;
	
	public abstract View getItemView(int position, View convertView, ViewGroup parent);
	
	protected BaseListAdapter(Context context) {
		
		mContext = context;
		
		imageLoader = ImageLoader.getInstance();
		imageConfig = new ImageLoaderConfiguration.Builder(context).build();
		imageLoader.init(imageConfig);
		
		mInflater = LayoutInflater.from(context);
	}
	
	public void reset(){
		if(mList != null){
			mList.clear();
		}
	}
	
	public void setList(ArrayList<BaseModel> list){
		mList = list;
	}
	
	public ArrayList<BaseModel> getList() {
		return mList;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItemView(position, convertView, parent);
	}
}
