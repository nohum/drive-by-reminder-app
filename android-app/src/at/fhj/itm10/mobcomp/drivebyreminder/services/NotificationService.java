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
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
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
}
