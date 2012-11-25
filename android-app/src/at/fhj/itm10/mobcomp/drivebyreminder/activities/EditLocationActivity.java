package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DownloadLocationDataTask;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.LocationSearchListAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

/**
 * Location choosing activity.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_editlocation)
public class EditLocationActivity extends RoboSherlockActivity
		implements OnClickListener {

	@InjectView(R.id.txtLocationName)
	private EditText txtLocationName;
	
	@InjectView(R.id.btnLocationSearch)
	private Button btnLocationSearch;
	
	@InjectView(R.id.lblResult)
	private TextView lblResult;
	
	@InjectResource(R.string.activity_editlocation_result_noresults)
	private String strResultEmptyResult;
	
	@InjectResource(R.string.activity_editlocation_result_nosearch)
	private String strResultNoSearch;
	
	@InjectResource(R.string.activity_editlocation_result_nonetwork)
	private String strResultNetworkError;

	@InjectView(R.id.lstFoundLocations)
	private ListView lstFoundLocations;

	private SharedPreferences preferences;

	private DownloadLocationDataTask downloadTask;
	
	/**
	 * Just used to save the instance state and to avoid calling the webservice.
	 */
	private List<Location> locations;
	
	/**
	 * Used to encode the user language for our webservice.
	 */
	private HashMap<String, String> languageCodeMap = new HashMap<String, String>() {

		/**
		 * serial.
		 */
		private static final long serialVersionUID = 1461249924358530289L;

		{
			put("Deutsch", "de");
			put("English", "en");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (savedInstanceState != null) {
        	restoreFromState(savedInstanceState);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(
        		getApplicationContext());

        String language = Locale.getDefault().getDisplayLanguage();
        Log.d("EditLocationActivity", "language = " + language);
        Log.d("EditLocationActivity", "language code = "
        		+ languageCodeMap.get(language));

        String regionBias = preferences.getString("locationBias", "at");
        Log.d("EditLocationActivity", "pref = " + regionBias);

        downloadTask = new DownloadLocationDataTask(getApplicationContext(), this,
        		languageCodeMap.get(language), regionBias);

        initViewFromValues();
        initViewEvents();
	}

	/**
	 * Add view events
	 */
	private void initViewEvents() {
		btnLocationSearch.setOnClickListener(this);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

//		outState.putSerializable("s", locations);
//		outState.
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState == null) {
			restoreFromState(savedInstanceState);
			initViewFromValues();
		}
	}

	private void restoreFromState(Bundle savedInstanceState) {

	}
	
	/**
	 * Init all views.
	 */
	private void initViewFromValues() {
		// TODO Auto-generated method stub
		
	}
	
	public void setFoundLocations(List<Location> locations) {
		if (locations.size() == 0) {
			// Show a text for the user
			lblResult.setText(strResultEmptyResult);
			lblResult.setVisibility(View.VISIBLE);

			return;
		}
		
		// Field is used for maintaining state
		this.locations = locations;
		lstFoundLocations.setAdapter(new LocationSearchListAdapter(locations));
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnLocationSearch)) {
			locations = null;

			if (TextUtils.isEmpty(txtLocationName.getText().toString())) {
				lblResult.setText(strResultNoSearch);
				lblResult.setVisibility(View.VISIBLE);
			} else {
				downloadTask.setLocationName(txtLocationName.getText().toString());
				lblResult.setVisibility(View.GONE);
				
				// Call the task
				downloadTask.execute();
			}
		}
	}

	/**
	 * Shows an error message about the network.
	 */
	public void showNetworkErrorMessage() {
		lblResult.setText(strResultNetworkError);
		lblResult.setVisibility(View.VISIBLE);
	}
}
