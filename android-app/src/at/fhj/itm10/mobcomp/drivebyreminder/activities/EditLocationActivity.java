package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.widget.ListView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

/**
 * Location choosing activity.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_editlocation)
public class EditLocationActivity extends RoboSherlockActivity {
	
	@InjectView(R.id.lstFoundLocations)
	private ListView foundLocations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (savedInstanceState == null) {
        	setDefaultValues();
        } else {
        	restoreFromState(savedInstanceState);
        }
        
        initViewFromValues();
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState == null) {
			restoreFromState(savedInstanceState);
			initViewFromValues();
		}
	}

	/**
	 * Set activity default values for views.
	 */
    private void setDefaultValues() {

	}

	private void restoreFromState(Bundle savedInstanceState) {
		
	}
	
	/**
	 * Init all views.
	 */
	private void initViewFromValues() {
		// TODO Auto-generated method stub
		
	}
}
