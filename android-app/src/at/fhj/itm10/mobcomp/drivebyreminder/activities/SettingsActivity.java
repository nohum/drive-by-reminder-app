package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

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
        }
        return super.onOptionsItemSelected(item);
    }	
}
