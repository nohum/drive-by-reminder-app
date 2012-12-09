package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.services.NotificationService;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;

/**
 * Settings activity.
 * 
 * @author Wolfgang Gaar
 */
public class SettingsActivity extends RoboSherlockPreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Deprecated, but THE way to go to support pre android-3.0 devices
        addPreferencesFromResource(R.xml.preferences);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
		SharedPreferences preferences = PreferenceManager
        		.getDefaultSharedPreferences(getApplicationContext());

		Intent serviceIntent = new Intent(this,
				NotificationService.class);
		stopService(serviceIntent);

		if (preferences.getBoolean("appEnabled", true)) {
			Log.v(getClass().getSimpleName(),
					"onStart: appEnabled set to true, re-starting service...");

	        startService(serviceIntent);
		}

    	super.onBackPressed();
    }
}
