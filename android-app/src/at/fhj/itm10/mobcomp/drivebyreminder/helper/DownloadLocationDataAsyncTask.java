package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.view.Window;

import android.os.AsyncTask;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.EditLocationActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.LocationQuery;

/**
 * Downloads and returns found locations to use in the
 * {@link EditLocationActivity}.
 * 
 * @author Wolfgang Gaar
 */
public class DownloadLocationDataAsyncTask extends
		AsyncTask<LocationQuery, Void, List<Location>> {

	/**
	 * The error code for the {@link DownloadLocationDataAsyncTask}.
	 * 
	 * @author Wolfgang Gaar
	 */
	public enum ErrorCode {
		NO_ERROR,
		INVALID_NAME,
		DOWNLOAD_ERROR,
		STATUS_ERROR,
		OTHER_ERROR
	};
	
	private final String API_LOCATION_MORE
			= "http://drivebyreminder.truthfactory.tk/service/more/byname/%s"
					+ "/inlanguage/%s/inregion/%s";
	
	private EditLocationActivity activity;
	
	private volatile ErrorCode occuredError = ErrorCode.NO_ERROR;

	/**
	 * Create the async task.
	 * 
	 * @param activity the activity
	 */
	public DownloadLocationDataAsyncTask(EditLocationActivity activity) {
		super();
		this.activity = activity;
	}
	
	@Override
	protected List<Location> doInBackground(LocationQuery... params) {
		LocationQuery query = params[0];
		
		String url = null;
		try {
			url = String.format(API_LOCATION_MORE,
					URLEncoder.encode(query.getLocationName().trim(), "UTF-8"),
					query.getLanguageCode(), query.getRegionCode());
		} catch (UnsupportedEncodingException e1) {
			Log.d("DownloadLocationDataAsyncTask",
					"error on encoding url-data!");
			e1.printStackTrace();
			
			occuredError = ErrorCode.INVALID_NAME;
			return null;
		}

		JSONObject json;
		try {
			json = retrieveUrlData(url);
		} catch (Exception e) {
			Log.d("DownloadLocationDataAsyncTask",
					"error on downloading data!");
			e.printStackTrace();
			
			occuredError = ErrorCode.DOWNLOAD_ERROR;
			return null;
		}
		
		List<Location> results = null;
		try {
			if (!json.getString("status").equals("OK")) {
				throw new IllegalStateException(
						"Server error, status returned: "
								+ json.getString("status"));
			}

			JSONArray entries = json.getJSONArray("entries");
			results = new ArrayList<Location>();
			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);

				results.add(new Location(entry.getString("name"),
						entry.getString("address"), entry.getDouble("latitude"),
						entry.getDouble("longitude")));
			}
		} catch (JSONException e) {
			Log.d("DownloadLocationDataAsyncTask",
					"error when converting data!");
			e.printStackTrace();
			
			occuredError = ErrorCode.DOWNLOAD_ERROR;
		} catch (IllegalStateException e) {
			Log.d("DownloadLocationDataAsyncTask", "status error!");
			e.printStackTrace();
			
			occuredError = ErrorCode.STATUS_ERROR;
		} 

		return results;
	}

	/**
	 * Shows a infinite progress bar.
	 */
	protected void onPreExecute() {
		//Hack to hide the regular progress bar
		activity.setSupportProgress(Window.PROGRESS_END);
		// Show the infinite progress meter
		activity.setSupportProgressBarIndeterminateVisibility(true);
	}
	
	/**
	 * Processes the result.
	 * 
	 * @param result the location result
	 */
	protected void onPostExecute(List<Location> result) {
		Log.d("DownloadLocationDataAsyncTask", "result code = "
				+ this.occuredError);
	
		activity.setSupportProgressBarIndeterminateVisibility(false);
		Log.d("DownloadLocationDataAsyncTask", "calling activity.processFoundLocations()");
		activity.processFoundLocations(this.occuredError, result);
    }
	
	/**
	 * Retrieve json data from given url.
	 * 
	 * @param url the url to fetch
	 * @return JSONObject
	 * @throws IOException IOException
	 * @throws JSONException IOException
	 */
	private JSONObject retrieveUrlData(String url) throws IOException,
			JSONException {
		StringBuffer sb = new StringBuffer();
		BufferedReader inreader = null;
		URL theUrl = new URL(url);
		Log.d("DownloadLocationDataAsyncTask", "theUrl = " + theUrl);
		HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();

		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			inreader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = inreader.readLine()) != null) {
				Log.d("DownloadLocationDataAsyncTask", "CONN-LINE: " + line);
				sb.append(line);
			}
			inreader.close();
		}
		conn = null;

		Log.d("DownloadLocationDataAsyncTask", "retrieved data: "
				+ sb.toString());
		
		return new JSONObject(sb.toString());
	}

}
