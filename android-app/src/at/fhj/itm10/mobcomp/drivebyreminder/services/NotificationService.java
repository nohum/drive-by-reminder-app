package at.fhj.itm10.mobcomp.drivebyreminder.services;

import java.util.Calendar;
import java.util.List;

import roboguice.inject.InjectResource;
import roboguice.service.RoboService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskStorageHelper;
import at.fhj.itm10.mobcomp.drivebyreminder.models.BoundedLocation;

/**
 * Notification service for the nearby tasks reminder.
 * 
 * @author Wolfgang Gaar
 */
public class NotificationService extends RoboService
		implements LocationListener {

//	private NotificationService notificationService;

	private LocationManager locationManager;
	
	private SharedPreferences preferences;
	
	private TaskDataDAO dbDao;
	
	private static final int LOCATION_UPDATE_INTERVAL = 1000 * 60 * 5;
	
	private static final int METERS_UPDATE_THRESHOLD = 300;
	
	/**
	 * Latitude degrees for one meter - 40076 / 360 / 60 / 1000000.
	 */
	private static final double DEGREES_PER_METER_LAT = 0.00000185537037037037;
	
	/**
	 * Longitude degrees for one meter - 40076 / 360 / 60 / 100000.
	 */
	private static final double DEGREES_PER_METER_LON = 0.0000185537037037037;
	
	/**
	 * Describes the maximum distance of a point of interest from a user's view.
	 * Taken from res/values/arrays.xml - proximitryEntriesWithDefault - last entry.
	 */
	private static final int MAXIMUM_USER_DISTANCE = 15000;
	
	private static final double MAXIMUM_TASK_DISTANCE_LAT = DEGREES_PER_METER_LAT
			* MAXIMUM_USER_DISTANCE;
	
	private static final double MAXIMUM_TASK_DISTANCE_LON = DEGREES_PER_METER_LON
			* MAXIMUM_USER_DISTANCE;

	private int defaultMaximumMetersDistance = 0;
	
	@InjectResource(R.array.proximitryEntriesWithDefault)
	private String[] proximitryEntries;
	
	@Override
	public IBinder onBind(final Intent data) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		initServiceVars();
		initService();

		Log.d(getClass().getSimpleName(), "notification service is up and running");

		checkLocationMatchInDatabase(locationManager.getLastKnownLocation(
				LocationManager.NETWORK_PROVIDER));
	}

	/**
	 * Register for updates and fill other internal variables.
	 */
	private void initService() {
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				LOCATION_UPDATE_INTERVAL, METERS_UPDATE_THRESHOLD, this);
		// Register also an passive provider to get locations if other apps need them
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
				LOCATION_UPDATE_INTERVAL, METERS_UPDATE_THRESHOLD, this);
		
		defaultMaximumMetersDistance = Integer.parseInt(preferences.getString(
				"defaultProximitry", "3000"));
		
		Log.d(getClass().getSimpleName(), "initService: defaultMaximumMetersDistance = "
				+ defaultMaximumMetersDistance);
	}

	/**
	 * Init service variables.
	 */
	private void initServiceVars() {
//		notificationService = (NotificationService) getApplicationContext()
//				.getSystemService(NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(LOCATION_SERVICE);
		preferences = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext());
		dbDao = new TaskDataDAO(new TaskStorageHelper(getApplicationContext()));

		Log.d(getClass().getSimpleName(), "DEGREES_PER_METER = " + DEGREES_PER_METER_LAT);
		Log.d(getClass().getSimpleName(), "MAXIMUM_USER_DISTANCE = " + MAXIMUM_USER_DISTANCE);
		Log.d(getClass().getSimpleName(), "MAXIMUM_TASK_DISTANCE = " + MAXIMUM_TASK_DISTANCE_LAT);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		locationManager.removeUpdates(this);
		dbDao.close();
		
		Log.d(getClass().getSimpleName(), "notification service is shutting down");
	}
	
	private void checkLocationMatchInDatabase(Location userLocation) {
		if (userLocation == null) {
			return;
		}

		double minLatitude = userLocation.getLatitude() - MAXIMUM_TASK_DISTANCE_LAT;
		double minLongitude = userLocation.getLongitude() - MAXIMUM_TASK_DISTANCE_LON;
		double maxLatitude = userLocation.getLatitude() + MAXIMUM_TASK_DISTANCE_LAT;
		double maxLongitude = userLocation.getLongitude() + MAXIMUM_TASK_DISTANCE_LON;

//		Log.d(getClass().getSimpleName(), "checkLocationMatchInDatabase: min latitude = "
//				+ minLatitude + ", min longitude = " + minLongitude);
//		Log.d(getClass().getSimpleName(), "checkLocationMatchInDatabase: max latitude = "
//				+ maxLatitude + ", max longitude = " + maxLongitude);

		float[] minResults = new float[3];
		Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
				minLatitude, minLongitude, minResults);
		Log.d(getClass().getSimpleName(), "checkLocationMatchInDatabase: min location "
				+ "distance from original point = " + minResults[0]);

		List<BoundedLocation> locations = dbDao.findLocationsByBoundaries(
				Calendar.getInstance(), minLatitude, minLongitude, maxLatitude,
				maxLongitude);

		// Get out of here fast if there are no matches
		if (locations.size() == 0) {
			Log.d(getClass().getSimpleName(), "checkLocationMatchInDatabase: no locations");
			return;
		}

		for (BoundedLocation foundLocation : locations) {
			Log.d(getClass().getSimpleName(), "checkLocationMatchInDatabase: found: "
					+ foundLocation.toString());

			// The custom proximitry is zero, check for the
			// defaultMaximumMetersDistance
			if (foundLocation.getCustomProximitry() == 0) {
				testFoundTaskProximitry(foundLocation, defaultMaximumMetersDistance);
			}
			// If the custom proximitry equals the highest possible proximitry, this
			// is going to be true. We already checked for MAXIMUM_USER_DISTANCE using
			// the supplied dao query, so we just report this task and all work is done.
			else if (foundLocation.getCustomProximitry() == proximitryEntries.length - 1) {
				notifyUserAboutTask(foundLocation);
			}
			// Any other setting
			else {
				testFoundTaskProximitry(foundLocation, Integer.parseInt(
						proximitryEntries[foundLocation.getCustomProximitry()]));
			}
		}
	}

	/**
	 * Test found location for a custom proximitry and notify the user if
	 * appropriate.
	 * 
	 * @param foundLocation
	 * @param proximitry task proximitry in meters
	 */
	private void testFoundTaskProximitry(BoundedLocation foundLocation,
			int proximitry) {
		
		
		
	}

	/**
	 * Notify the user about a nearby task.
	 * 
	 * @param foundLocation
	 */
	private void notifyUserAboutTask(BoundedLocation foundLocation) {
//		Notification updateComplete = new Notification();
//		updateComplete.icon = android.R.drawable.stat_notify_sync;
//		updateComplete.tickerText = context
//		    .getText(R.string.notification_title);
//		updateComplete.when = System.currentTimeMillis();
		
//		Intent notificationIntent = new Intent(context,
//			    TutListActivity.class);
//			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//			    notificationIntent, 0);
		
//		notificationManager.notify(LIST_UPDATE_NOTIFICATION, updateComplete);
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(getClass().getSimpleName(), "onLocationChanged: latitude = "
				+ location.getLatitude() + ", longitude = " + location.getLongitude());

		checkLocationMatchInDatabase(location);
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
}
