package com.funny.developers.musicstylelist.view;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomPreferenceCategory extends PreferenceCategory{
	public CustomPreferenceCategory(Context context) {
		super(context);
	}

	public CustomPreferenceCategory(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomPreferenceCategory(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected View onCreateView(ViewGroup parent){
		TextView titleView = (TextView)super.onCreateView(parent);
		titleView.setTextColor(Color.parseColor("#EF5350"));
		
		return titleView;
	}
}
