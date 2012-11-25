package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.EditLocationActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.LocationQuery;

/**
 * Downloads and returns found locations to use in the {@link EditLocationActivity}.
 * 
 * @author Wolfgang Gaar
 */
public class DownloadLocationDataAsyncTask extends
		AsyncTask<LocationQuery, Void, List<Location>> {

	private final String API_LOCATION_MORE
			= "http://drivebyreminder.truthfactory.tk/service/more/byname/%s/inlanguage/%s/inregion/%s";
	
	private EditLocationActivity activity;

	public DownloadLocationDataAsyncTask(EditLocationActivity activity) {
		super();
		this.activity = activity;
	}
	
	@Override
	protected List<Location> doInBackground(LocationQuery... params) {
		LocationQuery query = params[0];
		
		String url = String.format(API_LOCATION_MORE, query.getLocationName(),
				query.getLanguageCode(), query.getRegionCode());
		JSONObject json;
		try {
			json = retrieveUrlData(url);
		} catch (Exception e) {
			Log.d("DownloadLocationDataAsyncTask", "error on downloading data!");
			e.printStackTrace();
			
			return null;
		}
		
		List<Location> results = null;
		try {
			if (!json.getString("status").equals("OK")) {
				throw new Exception("Server error, status returned: " + json.getString("status"));
			}

			JSONArray entries = json.getJSONArray("entries");
			results = new ArrayList<Location>();
			for(int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);

				results.add(new Location(entry.getString("name"), entry.getString("address"),
						entry.getDouble("latitude"), entry.getDouble("longitude")));
			}
		} catch (JSONException e) {
			Log.d("DownloadLocationDataAsyncTask", "error when converting data!");
			e.printStackTrace();
		} catch (Exception e) {
			Log.d("DownloadLocationDataAsyncTask", "status error!");
			e.printStackTrace();
		} 

		return results;
	}

	protected void onPreExecute() {
		// Show the infinite progress meter
		activity.setSupportProgressBarIndeterminateVisibility(true);
	}
	
	protected void onPostExecute(List<Location> result) {
		activity.setSupportProgressBarIndeterminateVisibility(false);
		Log.d("DownloadLocationDataAsyncTask", "result = " + result);
		
		if (result != null) {
			activity.setFoundLocations(result);
		}

        //showDialog("Downloaded " + result + " bytes");
    }
	
	/**
	 * Retrieve json data from given url.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	private JSONObject retrieveUrlData(String url) throws IOException, JSONException {
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

		Log.d("DownloadLocationDataAsyncTask", "retrieved data: " + sb.toString());
		
		return new JSONObject(sb.toString());
	}

}
