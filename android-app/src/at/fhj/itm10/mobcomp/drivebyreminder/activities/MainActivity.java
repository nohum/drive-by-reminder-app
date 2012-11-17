package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

/**
 * Main view.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockActivity implements OnNavigationListener {
	
	@InjectView(R.id.lblNavigationList)
	private TextView mSelected;

	@InjectResource(R.array.locations)
    private String[] mLocations;
    
	@InjectResource(R.string.menu_add)
    private String stringAdd;
    
	@InjectResource(R.string.menu_settings)
    private String stringSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Context context = getSupportActionBar().getThemedContext();
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context,
        		R.array.locations, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//    	
//    	Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_LONG).show();
//    	
//    	return false;
//    }

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO Auto-generated method stub
		return false;
	}

}
