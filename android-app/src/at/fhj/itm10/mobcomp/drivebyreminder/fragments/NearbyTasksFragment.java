package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import roboguice.inject.InjectResource;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.LocationBoundariesCalculator;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.TasksListAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.models.TaskLocationResult;

/**
 * Fragment for the nearby tasks view.
 * 
 * @author Wolfgang Gaar
 */
public class NearbyTasksFragment extends AllTasksFragment {

	/**
	 * Describes the maximum distance of a point of interest from a user's view.
	 * Taken from res/values/arrays.xml - proximitryEntriesWithDefault - last entry.
	 */
	private static final int MAXIMUM_USER_DISTANCE = 15000;

	private int defaultMaximumMetersDistance = 0;
	
	@InjectResource(R.array.proximitryEntriesWithDefault)
	private String[] proximitryEntries;
	
	private Location currentUserLocation;
	
	private TasksListAdapter listAdapter;

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return NearbyTasksFragment
	 */
	public static NearbyTasksFragment newInstance(TaskDataDAO dao,
			MainFragmentPagerAdapter pagerAdapter) {
		NearbyTasksFragment fragment = new NearbyTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setDbDao(dao);
        fragment.setPagerAdapter(pagerAdapter);

        return fragment;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		defaultMaximumMetersDistance = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(getActivity()).getString(
				// 3000 = see res/values/arrays.xml - proximitryEntries
				"defaultProximitry", "3000"));
		Log.v(getClass().getSimpleName(), "onCreate: proximitryEntries = "
				+ proximitryEntries);
		proximitryEntries = getResources().getStringArray(
				R.array.proximitryEntriesWithDefault);
		Log.v(getClass().getSimpleName(), "onCreate after: proximitryEntries = "
				+ proximitryEntries);
		
		super.onCreate(savedInstanceState);
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_nearbytasks, container,
       		false);
    }

	@Override
	public void onStart() {
		Log.v(getClass().getSimpleName(), "------------ ONSTART BEFORE");
		
		super.onStart();
		reloadViewData();
	}
	
	@Override
	public void onResume() {
		Log.v(getClass().getSimpleName(), "------------ ONRESUME BEFORE");
		
		super.onStart();
		reloadViewData();
	}
    
    /**
     * Reload the view.
     */
    @Override
    public void reloadViewData() {
    	Log.v(getClass().getSimpleName(), "reloadViewData(): activity = "
    			+ getActivity());

    	if (getActivity() == null) {
    		Log.w(getClass().getSimpleName(),
    				"RACECONDITION with reloadViewData() and attached activity!");
    		return;
    	}
    	
    	if (currentUserLocation == null) {
    		Log.w(getClass().getSimpleName(),
    				"reloadViewData: currentUserLocation is null");
    		return;
    	}
    	
		LocationBoundariesCalculator calc = new LocationBoundariesCalculator(
				currentUserLocation, MAXIMUM_USER_DISTANCE);
		Location min = calc.getMinBoundary();
		Location max = calc.getMaxBoundary();	

		Log.v(getClass().getSimpleName(), "reloadViewData: min boundary = "
				+ min);
		Log.v(getClass().getSimpleName(), "reloadViewData: max boundary = "
				+ max);
		
    	List<TaskLocationResult> taskLocations = dbDao.findLocationsByBoundaries(
    			Calendar.getInstance(), min.getLatitude(), min.getLongitude(),
    			max.getLatitude(), max.getLongitude());

    	List<Long> taskIds = new ArrayList<Long>();
    	for (TaskLocationResult foundLocation : taskLocations) {
			Log.v(getClass().getSimpleName(), "reloadViewData: found: "
					+ foundLocation.toString());
			proximitryEntries = getResources().getStringArray(
					R.array.proximitryEntriesWithDefault);
			Log.v(getClass().getSimpleName(), "proximitryEntries = "
					+ proximitryEntries);

			// The custom proximitry is zero, check for the
			// defaultMaximumMetersDistance
			if (foundLocation.getCustomProximitry() == 0) {
				if (isFoundTaskNearEnough(currentUserLocation, foundLocation,
						defaultMaximumMetersDistance)) {
					taskIds.add(foundLocation.getTaskId());
				}
			}
			// If the custom proximitry equals the highest possible proximitry, this
			// is going to be true. We already checked for MAXIMUM_USER_DISTANCE using
			// the supplied dao query, so we just report this task and all work is done.
			// FIXME: NPE
			else if (foundLocation.getCustomProximitry() == proximitryEntries.length - 1) {
				taskIds.add(foundLocation.getTaskId());
			}
			// Any other setting
			else {
				if (isFoundTaskNearEnough(currentUserLocation, foundLocation,
						Integer.parseInt(proximitryEntries[foundLocation
						                                   .getCustomProximitry()]))) {
					taskIds.add(foundLocation.getTaskId());
				}
			}
		}

    	// Get a query using the already determined task ids
    	usedCursor = dbDao.constructFindTasksCursorByIdList(taskIds);
    	
    	// Sometimes the context is null...
    	Context context = getActivity();
    	if (context == null) {
    		context = getSherlockActivity();
    	}
    	
        listAdapter = TasksListAdapter.newInstance(context, 
        		usedCursor);

        setListAdapter(listAdapter);
    }

    /**
     * Update the location data and reload the view.
     * 
     * @param location
     * @param context
     */
	public void updateUserLocation(Location location) {
		Log.v(getClass().getSimpleName(), "------------ updateUserLocation");

		currentUserLocation = location;
		reloadViewData();
	}
    
	/**
	 * Test found location for a custom proximitry.
	 * 
	 * @param userLocation
	 * @param foundLocation
	 * @param proximitry task proximitry in meters
	 * @return boolean
	 */
	private boolean isFoundTaskNearEnough(Location userLocation,
			TaskLocationResult foundLocation, int proximitry) {
		return LocationBoundariesCalculator.testTaskProximitry(userLocation,
				foundLocation, proximitry);
	}
}
