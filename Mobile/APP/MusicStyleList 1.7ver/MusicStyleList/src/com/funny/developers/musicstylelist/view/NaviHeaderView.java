package com.funny.developers.musicstylelist.view;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.util.SettingUtils;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NaviHeaderView extends RelativeLayout{
	
	private Context mContext;
	
	private TextView favoriteType;
	
	public NaviHeaderView (Context context){
		super(context);
		mContext = context;
		inflate(mContext, R.layout.layout_for_navi_listview_header, this);
		
		String favorite = SettingUtils.getFavoriteType(context);
		
		favoriteType = (TextView)findViewById(R.id.favorite_type);
		favoriteType.setText(context.getString(R.string.navi_headerview_title) + favorite);
	}
}
