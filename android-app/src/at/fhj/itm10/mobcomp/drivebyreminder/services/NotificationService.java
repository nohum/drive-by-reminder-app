package at.fhj.itm10.mobcomp.drivebyreminder.services;

import java.util.Calendar;
import java.util.List;

import roboguice.inject.InjectResource;
import roboguice.service.RoboService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.MainActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskStorageHelper;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.LocationBoundariesCalculator;
import at.fhj.itm10.mobcomp.drivebyreminder.models.TaskLocationResult;

import com.google.inject.Inject;

/**
 * Notification service for the nearby tasks reminder.
 * 
 * @author Wolfgang Gaar
 */
public class NotificationService extends RoboService
		implements LocationListener {
	
	// "at.fhj.itm10.mobcomp.drivebyreminder.intents.REQUEST_LOCATION"

	@Inject
	private NotificationManager notificationManager;

	@Inject
	private LocationManager locationManager;

	private SharedPreferences preferences;
	
	private TaskDataDAO dbDao;
	
	private Location currentUserLocation;
	
	private static final int LOCATION_UPDATE_INTERVAL = 1000 * 60 * 5;
	
	private static final int METERS_UPDATE_THRESHOLD = 300;
	
	/**
	 * Describes the maximum distance of a point of interest from a user's view.
	 * Taken from res/values/arrays.xml - proximitryEntriesWithDefault - last entry.
	 */
	private static final int MAXIMUM_USER_DISTANCE = 15000;

	private int defaultMaximumMetersDistance = 0;
	
	private Uri notificationRingtoneResource = null;
	
	private boolean notificationVibrate = true;
	
	@InjectResource(R.array.proximitryEntriesWithDefault)
	private String[] proximitryEntries;
	
	@InjectResource(R.string.service_notification_title)
	private String strNotificationTitle;
	
	@Override
	public IBinder onBind(final Intent data) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		initServiceVars();
		initService();

		Log.i(getClass().getSimpleName(), "notification service is up and running");
		
		Location lastLocation = locationManager.getLastKnownLocation(
				LocationManager.NETWORK_PROVIDER);

		sendLocationToActivities(lastLocation);
		checkLocationMatchInDatabase(lastLocation);
	}

	/**
	 * Init service variables.
	 */
	private void initServiceVars() {
		preferences = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext());
		dbDao = new TaskDataDAO(new TaskStorageHelper(getApplicationContext()));
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
				// 3000 = see res/values/arrays.xml - proximitryEntries
				"defaultProximitry", "3000"));
		
		String settingTemp = preferences.getString("notificationRingtone", "");
		if (!settingTemp.isEmpty()) {
			notificationRingtoneResource = Uri.parse(settingTemp);
		}

		notificationVibrate = preferences.getBoolean("notificationVibrate", true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		locationManager.removeUpdates(this);
		dbDao.close();

		Log.i(getClass().getSimpleName(), "notification service is shutting down");
	}

	private void checkLocationMatchInDatabase(Location userLocation) {
		if (userLocation == null) {
			return;
		}

		LocationBoundariesCalculator calc = new LocationBoundariesCalculator(
				userLocation, MAXIMUM_USER_DISTANCE);
		Location min = calc.getMinBoundary();
		Location max = calc.getMaxBoundary();	

		// Output the meters distance for debugging
		float[] minResults = new float[3];
		Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
				min.getLatitude(), min.getLongitude(), minResults);
		Log.v(getClass().getSimpleName(), "checkLocationMatchInDatabase: min location "
				+ "distance from original point = " + minResults[0]);

		List<TaskLocationResult> locations = dbDao.findLocationsByBoundaries(
				Calendar.getInstance(), min.getLatitude(), min.getLongitude(),
    			max.getLatitude(), max.getLongitude());

		// Get out of here fast if there are no matches
		if (locations.size() == 0) {
			return;
		}

		for (TaskLocationResult foundLocation : locations) {
			Log.v(getClass().getSimpleName(), "checkLocationMatchInDatabase: found: "
					+ foundLocation.toString());

			// The custom proximitry is zero, check for the
			// defaultMaximumMetersDistance
			if (foundLocation.getCustomProximitry() == 0) {
				testFoundTaskProximitry(userLocation, foundLocation,
						defaultMaximumMetersDistance);
			}
			// If the custom proximitry equals the highest possible proximitry, this
			// is going to be true. We already checked for MAXIMUM_USER_DISTANCE using
			// the supplied dao query, so we just report this task and all work is done.
			else if (foundLocation.getCustomProximitry() == proximitryEntries.length - 1) {
				notifyUserAboutTask(foundLocation);
			}
			// Any other setting
			else {
				testFoundTaskProximitry(userLocation, foundLocation, Integer.parseInt(
						proximitryEntries[foundLocation.getCustomProximitry()]));
			}
		}
	}

	/**
	 * Test found location for a custom proximitry and notify the user if
	 * appropriate.
	 * 
	 * @param userLocation
	 * @param foundLocation
	 * @param proximitry task proximitry in meters
	 */
	private void testFoundTaskProximitry(Location userLocation,
			TaskLocationResult foundLocation, int proximitry) {
		float[] results = new float[3];
		Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
				foundLocation.getLatitude(), foundLocation.getLongitude(), results);

		Log.v(getClass().getSimpleName(), "testFoundTaskProximitry: proximitry result = "
				+ results[0]);
		if (results[0] <= proximitry) {
			notifyUserAboutTask(foundLocation);
		}
	}

	/**
	 * Notify the user about a nearby task.
	 * 
	 * @param foundLocation
	 */
	private void notifyUserAboutTask(TaskLocationResult foundLocation) {
		Log.v(getClass().getSimpleName(), "notifyUserAboutTask: foundLocation = "
				+ foundLocation);

		// If there is a snooze time set and it is in the future,
		// skip this notification...
		if (foundLocation.getSnoozeDate() != null
				&& foundLocation.getSnoozeDate().compareTo(Calendar.getInstance()) == 1) {
			Log.i(getClass().getSimpleName(), 
					"notifyUserAboutTask: skipping because of snooze");
			Log.v(getClass().getSimpleName(), 
					"snooze date = " + foundLocation.getSnoozeDate()
					.getTime());
			Log.v(getClass().getSimpleName(), 
					"now = " + Calendar.getInstance().getTime());

			return;
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentTitle(strNotificationTitle);
		builder.setContentText(foundLocation.getTitle());
		builder.setTicker(foundLocation.getTitle());
		builder.setOnlyAlertOnce(true);
		builder.setAutoCancel(true); // cancel notification on click automatically

		// On dismiss of notification: snooze that task
		Intent snoozeTaskIntent = new Intent();
		snoozeTaskIntent.putExtra("taskId", foundLocation.getTaskId());
		snoozeTaskIntent.setAction(
				"at.fhj.itm10.mobcomp.drivebyreminder.intents.TASK_SNOOZED");
		builder.setDeleteIntent(PendingIntent.getBroadcast(getApplicationContext(),
				0, snoozeTaskIntent, 0));

		// Show nearby tasks on click
		Intent showNearbyTasksIntent = new Intent(this, MainActivity.class);
		showNearbyTasksIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		showNearbyTasksIntent.putExtra("openedFragment", 2);
		builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(),
				0, showNearbyTasksIntent, 0));

		if (notificationRingtoneResource != null) {
			builder.setSound(notificationRingtoneResource);
		}

		if (notificationVibrate) {
			builder.setVibrate(new long[] {0, 300, 400, 300});
			builder.setLights(0xff00ff00, 300, 1000);
		}

		// Show a notification for this task only once... id here also unique
		notificationManager.notify((int) foundLocation.getTaskId(),
				builder.getNotification());
	}

	/**
	 * Send the current user location to the activities so that they are
	 * able to use that data.
	 * 
	 * @param location
	 */
	private void sendLocationToActivities(Location location) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(
				"at.fhj.itm10.mobcomp.drivebyreminder.intents.LOCATION_CHANGED");
		sendIntent.putExtra("location", location);

		sendBroadcast(sendIntent);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.d(getClass().getSimpleName(), "onLocationChanged: latitude = "
				+ location.getLatitude() + ", longitude = " + location.getLongitude());

		currentUserLocation = location;

		sendLocationToActivities(location);
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
