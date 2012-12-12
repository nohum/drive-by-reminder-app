package at.fhj.itm10.mobcomp.drivebyreminder.receiver;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.services.NotificationService;

import com.google.inject.Inject;

/**
 * Broadcast receiver to start service at change of connectivity mode.
 * 
 * @author Wolfgang Gaar
 */
public class ServiceConnectivityStateLoader extends RoboBroadcastReceiver {

	@Inject
	private LocationManager location;
	
	@Override
	protected void handleReceive(Context context, Intent intent) {
		SharedPreferences preferences = PreferenceManager
        		.getDefaultSharedPreferences(context);
		boolean networkState = location.isProviderEnabled(
				LocationManager.NETWORK_PROVIDER);
		
		Log.v(getClass().getSimpleName(), "network state = " + networkState);
		if (preferences.getBoolean("appEnabled", true) && networkState) {
			Log.v(getClass().getSimpleName(), "state is okay, starting service");
			
			Intent startServiceIntent = new Intent(context,
					NotificationService.class);
	        context.startService(startServiceIntent);
		}

		super.handleReceive(context, intent);
    }

}
