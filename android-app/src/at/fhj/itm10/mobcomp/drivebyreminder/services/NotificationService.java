package at.fhj.itm10.mobcomp.drivebyreminder.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskStorageHelper;

/**
 * Notification service for the nearby tasks reminder.
 * 
 * @author Wolfgang Gaar
 */
public class NotificationService extends Service implements LocationListener {

	private NotificationService notificationService;

	private LocationManager locationManager;
	
	private SharedPreferences preferences;
	
	private TaskDataDAO dbDao;
	
	private static final int UPDATE_INTERVAL = 1000 * 60 * 5;
	
	private static final int METERS_UPDATE_THRESHOLD = 300;

	@Override
	public IBinder onBind(final Intent data) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();

		notificationService = (NotificationService) getApplicationContext()
				.getSystemService(NOTIFICATION_SERVICE);
		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(LOCATION_SERVICE);
		preferences = PreferenceManager.getDefaultSharedPreferences(
				getApplicationContext());
		dbDao = new TaskDataDAO(new TaskStorageHelper(getApplicationContext()));

		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
				UPDATE_INTERVAL, METERS_UPDATE_THRESHOLD, this);
		locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
				UPDATE_INTERVAL, METERS_UPDATE_THRESHOLD, this);

		Log.d(getClass().getSimpleName(), "notification service is up and running");

		checkLocationMatchInDatabase(locationManager.getLastKnownLocation(
				LocationManager.NETWORK_PROVIDER));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		locationManager.removeUpdates(this);
		
		Log.d(getClass().getSimpleName(), "notification service is shutting down");
	}
	
	private void checkLocationMatchInDatabase(Location location) {
		//dbDao.

	}
	
	private void notifyUser() {
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
