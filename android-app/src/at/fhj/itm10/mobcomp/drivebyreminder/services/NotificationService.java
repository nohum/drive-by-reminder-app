package at.fhj.itm10.mobcomp.drivebyreminder.services;

import com.google.inject.Inject;

import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import roboguice.service.RoboService;

/**
 * Notification service for the nearby tasks reminder.
 * 
 * @author Wolfgang Gaar
 */
public class NotificationService extends RoboService {

	@Inject
	private NotificationService notificationService;
	
	@Inject
	private LocationManager locationManager;
	
	@Override
	public IBinder onBind(final Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}
}
