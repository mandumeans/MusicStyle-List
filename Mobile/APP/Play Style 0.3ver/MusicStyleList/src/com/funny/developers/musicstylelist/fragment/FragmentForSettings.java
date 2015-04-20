package com.funny.developers.musicstylelist.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.funny.developers.musicstylelist.R;

public class FragmentForSettings extends PreferenceFragment{
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_xml);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View rootView = super.onCreateView(inflater, container, savedInstanceState);
	    rootView.setBackgroundColor(getResources().getColor(android.R.color.white));
	    
	    getActivity().setTheme(R.style.Setting_Style);
	    
	    ListView list = (ListView) rootView.findViewById(android.R.id.list);
	    list.setDivider(new ColorDrawable(Color.GRAY)); // or some other color int
	    list.setDividerHeight(1);
	    
	    return rootView;
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}

}
