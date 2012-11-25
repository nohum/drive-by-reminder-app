package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import roboguice.activity.event.OnDestroyEvent;
import roboguice.event.Observes;
import roboguice.inject.InjectResource;
import roboguice.util.RoboAsyncTask;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.EditLocationActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

/**
 * Downloads and displays found locations in the {@link EditLocationActivity}.
 * 
 * @author Wolfgang Gaar
 * @see http://code.google.com/p/roboguice/wiki/RoboAsyncTask
 */
public class DownloadLocationDataTask extends RoboAsyncTask<List<Location>> {

	private String locationName;
	
	private String languageCode;
	
	private String regionBiasCode;
	
	private EditLocationActivity activity;

	private final String API_LOCATION_MORE
			= "http://drivebyreminder.truthfactory.tk/service/more/byname/%s/inlanguage/%s/inregion/%s";
	
	@InjectResource(R.string.activity_editlocation_location_downloaderror)
	private String downloadException;

	public DownloadLocationDataTask(Context context, EditLocationActivity activity,
			String languageCode, String regionBiasCode) {
		super(context);

		this.activity = activity;
		this.languageCode = languageCode;
		this.regionBiasCode = regionBiasCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	/**
	 * This method is the same as AsyncTask.doInBackground(Object[]).
	 */
	@Override
	public List<Location> call() throws Exception {

		String url = String.format(API_LOCATION_MORE, this.locationName, this.languageCode,
				this.regionBiasCode);
		JSONObject json = retrieveUrlData(url);
		
		if (!json.getString("status").equals("OK")) {
			throw new Exception("Server error, status returned: " + json.getString("status"));
		}

		JSONArray entries = json.getJSONArray("entries");
		List<Location> results = new ArrayList<Location>();
		for(int i = 0; i < entries.length(); i++) {
			JSONObject entry = entries.getJSONObject(i);

			results.add(new Location(entry.getString("name"), entry.getString("address"),
					entry.getDouble("latitude"), entry.getDouble("longitude")));
		}

		return results;
	}
	
	private JSONObject retrieveUrlData(String url) throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader inreader = null;
		URL theUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();

		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			inreader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = inreader.readLine()) != null) {
				sb.append(line);
			}
			inreader.close();
		}
		conn = null;

		Log.d("DownloadLocationDataTask", "retrieved data: " + sb.toString());
		
		return new JSONObject(sb.toString());
	}

	@Override 
    protected void onPreExecute() {
		// Show the infinite progress meter
		activity.setSupportProgressBarIndeterminateVisibility(true);
    } 
    
	/**
	 * Do this in the UI thread if call() succeeds.
	 * 
	 * @param result
	 */
    @Override 
    protected void onSuccess(List<Location> result) {
//    	activity.setFoundLocations(result);
    }
    
    /**
     * Do this in the UI thread if call() threw an exception.
     */
    @Override 
    protected void onException(Exception e) {
        Toast.makeText(activity, downloadException, Toast.LENGTH_LONG).show();
        activity.showNetworkErrorMessage();

        Log.e("DownloadLocationDataTask", e.getMessage());
    } 
    
    /**
     * Always do this in the UI thread after calling call().
     */
    @Override 
    protected void onFinally() {
    	activity.setSupportProgressBarIndeterminateVisibility(false);
    } 
	
	/**
	 * If the activity is destroyed, this handler will make sure
	 * that this background task gets canceled.
	 */
    protected void onActivityDestroy(@Observes OnDestroyEvent destroyEvent) {
        this.cancel(true);
    }
}
