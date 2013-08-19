package com.jens.collicounter;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

import android.os.Bundle;

public class Preferences extends SherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
