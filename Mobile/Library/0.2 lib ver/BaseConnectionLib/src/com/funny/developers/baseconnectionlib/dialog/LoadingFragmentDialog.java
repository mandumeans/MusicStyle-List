package com.funny.developers.baseconnectionlib.dialog;

import com.funny.developers.baseconnectionlib.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;

public class LoadingFragmentDialog extends DialogFragment{
	
	public boolean isShow = false;
	private View layout = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
		LayoutInflater mLayoutInflater = getActivity().getLayoutInflater();

		layout = mLayoutInflater.inflate(R.layout.base_dialog_loading, null);
		
		mBuilder.setView(layout);
		
		return mBuilder.create();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void show(FragmentManager manager, String tag) {
		if(isShow) return;

		super.show(manager, tag);
		isShow = true;
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		isShow = false;
		super.onDismiss(dialog);
	}

	@Override
	public void onStart() {
		super.onStart();
		clearBackgrounds(layout);
	}
	
	private void clearBackgrounds(View view) {
		while (view != null) {
			view.setBackgroundResource(android.graphics.Color.TRANSPARENT);

			final ViewParent parent = view.getParent();
			if (parent instanceof View) {
				view = (View) parent;
			} else {
				view = null;
			}
		}
	}
}
