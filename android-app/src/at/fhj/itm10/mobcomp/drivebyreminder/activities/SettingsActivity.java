package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import android.os.Bundle;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;

public class SettingsActivity extends RoboSherlockPreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
	}
	
}
