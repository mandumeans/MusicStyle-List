package com.funny.developers.musicstylelist.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.util.ImageUtils;

public class DetailHeaderView extends RelativeLayout{

	private Context mContext;

	private TextView titleView;
	private ImageView thumnailView;
	private TextView trackNumView;
	
	private ImageView playButton;

	public DetailHeaderView(Context context) {
		super(context);
		mContext = context;
		inflate(mContext, R.layout.layout_for_listview_header, this);

		titleView = (TextView) findViewById(R.id.header_title_view);
		thumnailView = (ImageView) findViewById(R.id.header_thumnail_view);
		trackNumView = (TextView) findViewById(R.id.header_tracknum_view);
		
		playButton = (ImageView) findViewById(R.id.header_play_button);
	}

	public void settingDetailHeaderView(String title, String thumnailUrl, int trackNum){

		titleView.setText(title);
		trackNumView.setText("" + trackNum);

		ImageUtils.displayUrlImage(thumnailView, thumnailUrl, R.drawable.no_image);
	}
	
	public void settingPlayButtonClickListener(OnClickListener listener){
		playButton.setOnClickListener(listener);
	}

}
