package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import roboguice.receiver.RoboBroadcastReceiver;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskStorageHelper;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.NearbyTasksFragment;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.services.NotificationService;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;

/**
 * Main activity.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockFragmentActivity
		implements OnNavigationListener, OnPageChangeListener {
	
	@InjectView(R.id.lblNavigationList)
	private TextView lblNavigationSelected;
	
	@InjectView(R.id.pgrMainView)
	private ViewPager pagerMainView;
	
	@Inject
	private LocationManager locationManager;

    @InjectResource(R.string.network_alert_title)
    private String strNetworkAlertTitle;
	
    @InjectResource(R.string.network_alert_text)
    private String strNetworkAlertText;
	
    @InjectResource(R.string.network_alert_ok)
	private String strNetworkAlertButton;

	/**
	 * Database DAO.
	 */
	protected TaskDataDAO taskDataDAO;

	private MainFragmentPagerAdapter pagerAdapter;

	private InternalLocationUpdateReceiver locationUpdateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Context context = getSupportActionBar().getThemedContext();
        // Set content of nav dropdown
        ArrayAdapter<CharSequence> list =
        		ArrayAdapter.createFromResource(context,
        		R.array.locations, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        taskDataDAO = new TaskDataDAO(new TaskStorageHelper(
        		getApplicationContext()));

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        // Pager class which enables us to have several fragments loaded
        // and "pagable"
        pagerAdapter = new MainFragmentPagerAdapter(
        		getSupportFragmentManager(), taskDataDAO);
        pagerMainView.setAdapter(pagerAdapter);
        pagerMainView.setOnPageChangeListener(this);

        if (savedInstanceState != null) {
			restoreState(savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		SharedPreferences preferences = PreferenceManager
        		.getDefaultSharedPreferences(getApplicationContext());

		boolean networkLocEnabled = locationManager.isProviderEnabled(
				LocationManager.NETWORK_PROVIDER);

		if (!networkLocEnabled) {
			new AlertDialog.Builder(this)
		    .setTitle(strNetworkAlertTitle)
		    .setMessage(strNetworkAlertText)
		    .setPositiveButton(strNetworkAlertButton,
		    		new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            dialog.cancel();
		        }
		     })
		     .show();
		}
		
		// Start the service if requested by the user
		if (preferences.getBoolean("appEnabled", true) && networkLocEnabled) {
			Log.v(getClass().getSimpleName(),
					"onStart: appEnabled set to true, starting service...");

			Intent startServiceIntent = new Intent(this,
					NotificationService.class);
	        startService(startServiceIntent);
		}

		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.v(getClass().getSimpleName(), "onResume called");
		
		locationUpdateReceiver = new InternalLocationUpdateReceiver();
		registerReceiver(locationUpdateReceiver, new IntentFilter(
				"at.fhj.itm10.mobcomp.drivebyreminder.intents.LOCATION_CHANGED"));
		taskDataDAO.open();
		
		requestLocationUpdate();
		
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(locationUpdateReceiver);
		taskDataDAO.close();

		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		taskDataDAO.close();
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("currentFragment", pagerMainView.getCurrentItem());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState != null) {
			restoreState(savedInstanceState);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Log.v(getClass().getSimpleName(), "onNewIntent called");

        // Used by notifications
        if (intent != null) {
        	int openedFragment = intent.getIntExtra("openedFragment", -1);
        	if (openedFragment > -1) {
        		showFragmentWithNumber(openedFragment);
        	}
        }

		super.onNewIntent(intent);
	}
	
	private void restoreState(Bundle savedInstanceState) {
		// Pager and nav menu item number
		showFragmentWithNumber(savedInstanceState.getInt("currentFragment"));
	}
	
	/**
	 * Request a location update from the service.
	 */
	private void requestLocationUpdate() {
		Log.v(getClass().getSimpleName(), "requestLocationUpdate called");
		
		Intent requestLocationUpdateIntent = new Intent();
        requestLocationUpdateIntent.setAction(
        		"at.fhj.itm10.mobcomp.drivebyreminder.intents.REQUEST_LOCATION");
        sendBroadcast(requestLocationUpdateIntent);
	}
	
	/**
	 * Show specific fragment.
	 * 
	 * @param number
	 */
	private void showFragmentWithNumber(int number) {
		getSupportActionBar().setSelectedNavigationItem(number);
		pagerMainView.setCurrentItem(number, false);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.menu_main_add:
        	Intent intent = new Intent(this, AddTaskActivity.class);
        	// There should only be one "Add new" activity open at any time
        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        	this.startActivityForResult(intent, 100);
        	return true;
        case R.id.menu_main_settings:
        	this.startActivity(new Intent(this, SettingsActivity.class));
        	return true;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Used for the top-left navigation menu.
     * 
     * @param itemPosition the pager item selection
     * @param itemId item id
     */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// Change the focused fragment of the pager if the navigation list is changed.
		// itemPosition = number of fragment, see MainFragmentPagerAdapter for numbering
		pagerMainView.setCurrentItem(itemPosition, true);

		return true;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// Empty by intention
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// Empty by intention
	}

	/**
	 * Called after a fragment has been selected ("scrolled to") by the user.
	 * 
	 * @param position the page position
	 */
	@Override
	public void onPageSelected(int position) {
		// Change the focused navigation list item if the viewed fragment has been changed.
		// position = number of fragment, see MainFragmentPagerAdapter for numbering
		getSupportActionBar().setSelectedNavigationItem(position);
		
		// Cancel action modes
//		if (getActionMode() != null) {
//			getActionMode().finish();
//		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case 100: // Process add task activity
			if (resultCode == Activity.RESULT_OK) {
				Log.d("MainActivity", "AddTaskActivity result = RESULT_OK");
				pagerAdapter.refreshFragments();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.d("MainActivity", "AddTaskActivity result = RESULT_CANCELED");
			}
		default:
			break;
		}
	}
	
	public TaskDataDAO getDao() {
		return taskDataDAO;
	}
	
	/**
	 * Location update broadcast receiver.
	 * 
	 * @author Wolfgang Gaar
	 */
	private class InternalLocationUpdateReceiver extends RoboBroadcastReceiver {

		@Override
		protected void handleReceive(Context context, Intent intent) {
			// Get fragment and update it's knowledge of the last user location

			NearbyTasksFragment fragment = (NearbyTasksFragment) pagerAdapter
					.getItem(MainFragmentPagerAdapter.NEARBY_TASKS_FRAGMENT);
			Location location = (Location) intent.getParcelableExtra("location");

			Log.d(getClass().getSimpleName(), "handleReceive: location = " + location);
			fragment.updateUserLocation(location, context);

			super.handleReceive(context, intent);
		}

	}
	
}
