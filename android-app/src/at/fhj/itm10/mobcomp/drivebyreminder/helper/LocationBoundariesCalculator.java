package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.location.Location;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.models.TaskLocationResult;

/**
 * Geo location boundaries calculator.
 * 
 * @author Wolfgang Gaar
 * @see http://gis.stackexchange.com/questions/2951/algorithm-for-offsetting-a-latitude-longitude-by-some-amount-of-meters
 */
public class LocationBoundariesCalculator {

	private int distanceInMeters;
	
	private Location location;
	
	private Location minBoundary;
	
	private Location maxBoundary;
	
	/**
	 * Earth radius.
	 */
	private static final int R = 6378137;

	public LocationBoundariesCalculator(Location baseLocation,
			int distanceInMeters) {
		location = baseLocation;
		this.distanceInMeters = distanceInMeters;
		
		calculate();
	}

	public Location getMinBoundary() {
		return minBoundary;
	}
	
	public Location getMaxBoundary() {
		return maxBoundary;
	}
	
	/**
	 * Calculate geo boundaries.
	 */
	private void calculate() {
		minBoundary = new Location(location);
		maxBoundary = new Location(location);
		
		Log.v(getClass().getSimpleName(), "calculate: distance meters = "
				+ distanceInMeters);
		
		calcDistanceFor(distanceInMeters * -1, minBoundary);
		calcDistanceFor(distanceInMeters, maxBoundary);
		
		// correction to +100 meters
		maxBoundary.setLatitude(maxBoundary.getLatitude() + 0.015);
		
		float[] minResults = new float[3];
		float[] maxResults = new float[3];

		Location.distanceBetween(location.getLatitude(), location.getLongitude(),
				minBoundary.getLatitude(), minBoundary.getLongitude(), minResults);
		Location.distanceBetween(location.getLatitude(), location.getLongitude(),
				maxBoundary.getLatitude(), maxBoundary.getLongitude(), maxResults);

		Log.v(getClass().getSimpleName(), "calculate: min location = " + minBoundary);
		Log.v(getClass().getSimpleName(), "calculate: min meters = " + minResults[0]);
		Log.v(getClass().getSimpleName(), "calculate: max location = " + maxBoundary);
		Log.v(getClass().getSimpleName(), "calculate: max meters = " + maxResults[0]);
	}
	
	private void calcDistanceFor(int distance, Location field) {
		double currentLat = location.getLatitude();
		double currentLon = location.getLongitude();
		
		double dLat = distance / R;
		double dLon = distance / (R * Math.cos(Math.PI * currentLat / 180));
		
		field.setLatitude(currentLat + dLat * 180 / Math.PI);
		field.setLongitude(currentLon + dLon * 180 / Math.PI);
	}

	/**
	 * Test found location for a custom proximitry.
	 * 
	 * @param userLocation
	 * @param foundLocation
	 * @param proximitry task proximitry in meters
	 * @return boolean
	 */
	public static boolean testTaskProximitry(Location userLocation,
			TaskLocationResult foundLocation, int proximitry) {
		float[] results = new float[3];
		Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(),
				foundLocation.getLatitude(), foundLocation.getLongitude(), results);

		float accuracyDiff = 0;
		if (userLocation.hasAccuracy()) {
			// http://stackoverflow.com/questions/3052517/how-is-location-accuracy-measured-in-android
			accuracyDiff = userLocation.getAccuracy();
			
			Log.v(LocationBoundariesCalculator.class.getSimpleName(),
					"testTaskProximitry: accuracy difference = " + accuracyDiff);
		}
		
		Log.v(LocationBoundariesCalculator.class.getSimpleName(),
				"testTaskProximitry: proximitry result = " + results[0]);
		return results[0] <= (proximitry + accuracyDiff);
	}
}
