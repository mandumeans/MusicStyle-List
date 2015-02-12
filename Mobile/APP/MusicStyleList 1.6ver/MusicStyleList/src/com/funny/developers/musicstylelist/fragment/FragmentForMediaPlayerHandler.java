package com.funny.developers.musicstylelist.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.model.BaseModel;
import com.funny.developers.musicstylelist.model.SearchTrackListModel;

public class FragmentForMediaPlayerHandler extends Fragment implements OnClickListener{
	
	TextView textAuthor = null;
	TextView textTitle = null;
	ImageButton buttonPlayPause = null;
	ImageButton buttonMenu = null;
	
	public void setPlayTrack(BaseModel playTrack){
		
		SearchTrackListModel data = (SearchTrackListModel) playTrack;
		
		textAuthor.setText("Author");
		textAuthor.setText(data.title);
	}
	
	public void setPlayPause(boolean playing){
		if (playing) {
			buttonPlayPause.setImageResource(R.drawable.ic_action_compose);
		} else {
			buttonPlayPause.setImageResource(R.drawable.ic_action_search);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = inflater.inflate(R.layout.fragment_for_mediaplayer_handler, container, false);
	
		textAuthor = (TextView) contentView.findViewById(R.id.text_for_author);
		textTitle = (TextView) contentView.findViewById(R.id.text_for_title);
		buttonPlayPause = (ImageButton) contentView.findViewById(R.id.button_for_play_pause);
		buttonPlayPause.setImageResource(R.drawable.ic_action_compose);
		buttonMenu = (ImageButton) contentView.findViewById(R.id.button_for_menu);
		buttonMenu.setOnClickListener(this);
		
		return contentView;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_for_menu:
			PopupMenu popup = new PopupMenu(getActivity(), v);
	        popup.getMenuInflater().inflate(R.menu.user_play_list_track_popup, popup.getMenu());

	        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
	            public boolean onMenuItemClick(MenuItem item) {
	                Log.d("Error", item.getTitle().toString());
	                return true;
	            }
	        });

	        popup.show();
			break;	
		}
	}
}
