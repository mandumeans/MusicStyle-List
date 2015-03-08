package com.funny.developers.musicstylelist.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.funny.developers.musicstylelist.R;
import com.funny.developers.musicstylelist.fragment.FragmentForSettings;

public class SettingsActivity extends ActionBarActivity{

	private Toolbar settingsActivityToolBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings_activity);

		settingsActivityToolBar = (Toolbar)findViewById(R.id.settings_activity_toolbar);
		setSupportActionBar(settingsActivityToolBar);

		getSupportActionBar().setTitle("Settings");
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getFragmentManager().beginTransaction()
			.replace(R.id.settings_content_layout, new FragmentForSettings())
			.commit();
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
	public void onRestart() {
		super.onRestart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
