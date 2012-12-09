package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DataSingletonStorage;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DownloadLocationDataAsyncTask;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DownloadLocationDataAsyncTask.ErrorCode;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.LocationSearchListAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.LocationQuery;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;

/**
 * Location choosing activity.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_editlocation)
public class EditLocationActivity extends RoboSherlockActivity
		implements OnClickListener, OnKeyListener {

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
	
	@InjectResource(R.string.activity_editlocation_result_unknownerror)
	private String strResultUnknownError;
	
	@InjectResource(R.string.activity_editlocation_save_nolocations)
	private String strNoLocationsSelected;

	@InjectView(R.id.lstFoundLocations)
	private ListView lstFoundLocations;

	@Inject
	private DataSingletonStorage dataStorage;

	private String currentLanguageCode;
	
	private String currentRegionBiasCode;
	
	/**
	 * Just used to save the instance state and to avoid calling the webservice.
	 */
	private List<Location> locations;
	
	/**
	 * Used to encode the user language for our webservice.
	 */
	private HashMap<String, String> languageCodeMap =
			new HashMap<String, String>() {

		/**
		 * serial.
		 */
		private static final long serialVersionUID = 1461249924358530289L;

		{
			put("Deutsch", "de");
			put("English", "en");
		}
	};
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		
		setSupportProgressBarIndeterminateVisibility(false);
        setSupportProgressBarVisibility(false);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        initFromSettings();
        initViewEvents();
        
        // Reload locations after activity restart...
        // Should use setRetainInstance
        // see: http://developer.android.com/reference/android/app/
        // Fragment.html#setRetainInstance%28boolean%29
		List<Location> lastLocations = (List<Location>)
        		getLastNonConfigurationInstance();
        if (lastLocations != null) {
            this.processFoundLocations(ErrorCode.NO_ERROR, lastLocations);
        } else if (getIntent().getBooleanExtra("loadFromStorage", false)) {
        	Log.d("EditLocationActivity", "lastLocations was null, loading"
        			+ " location data from data storage");
        	
        	this.locations = (List<Location>) dataStorage
        			.getData("locationsToShow");
        	this.processFoundLocations(ErrorCode.NO_ERROR, this.locations);
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_editlocation, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
        //case R.id.menu_editlocation_cancel:
        	this.setResult(Activity.RESULT_CANCELED, new Intent());
            this.finish();

            return true;
        case R.id.menu_editlocation_save:
        	List<Location> selectedLocations = new ArrayList<Location>();
        	for (Location location : this.locations) {
        		if (location.isLocationChooserSelected()) {
        			selectedLocations.add(location);
        		}
        	}
        	
        	if (selectedLocations.size() == 0) {
        		Toast.makeText(this, strNoLocationsSelected, Toast.LENGTH_LONG)
        				.show();
        		return false;
        	}

        	Log.d("EditLocationActivity", "dataStorage = " + dataStorage);
        	Log.d("EditLocationActivity", "locationsToSave = "
        			+ selectedLocations);
        	dataStorage.setData("locationsToSave", selectedLocations);

        	this.setResult(Activity.RESULT_OK, new Intent());
        	this.finish();
        	
        	return true;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }
	
	/**
	 * Init data from settings.
	 */
	private void initFromSettings() {
		SharedPreferences preferences = PreferenceManager
        		.getDefaultSharedPreferences(getApplicationContext());

        String language = Locale.getDefault().getDisplayLanguage();
        this.currentLanguageCode = languageCodeMap.get(language);
        this.currentRegionBiasCode =
        		preferences.getString("locationBias", "at");

        Log.d("EditLocationActivity", "language = " + language);
        Log.d("EditLocationActivity", "language code = "
        		+ this.currentLanguageCode);
        Log.d("EditLocationActivity", "location bias pref = "
        		+ this.currentRegionBiasCode);
        
	}

	/**
	 * Add view events.
	 */
	private void initViewEvents() {
		btnLocationSearch.setOnClickListener(this);
		txtLocationName.setOnKeyListener(this);
	}

	/**
	 * Used to save the received locations over activity restarts.
	 * 
	 * @return Object
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
	    return this.locations;
	}

	@Override
	public void onClick(View v) {
		if (v.equals(btnLocationSearch)) {
			this.locations = null;
			// Remove focus from location text field
			this.btnLocationSearch.requestFocus();

			if (TextUtils.isEmpty(txtLocationName.getText().toString())) {
				lblResult.setText(strResultNoSearch);
				lblResult.setVisibility(View.VISIBLE);
			} else {
				lblResult.setVisibility(View.GONE);
				
				LocationQuery query = new LocationQuery(this.txtLocationName
						.getText().toString(), this.currentRegionBiasCode,
						this.currentLanguageCode);

				// Call the task
				new DownloadLocationDataAsyncTask(this).execute(query);
			}
		}
	}

	/**
	 * Post-processing after locations have been fetched by the async task.
	 * 
	 * @param occuredError error code
	 * @param result fetched locations
	 */
	public void processFoundLocations(ErrorCode occuredError,
			List<Location> result) {
		this.locations = null;

		switch (occuredError) {
		case NO_ERROR:
			if (result.size() == 0) {
				// Show a text for the user
				lblResult.setText(strResultEmptyResult);
				lblResult.setVisibility(View.VISIBLE);
				lstFoundLocations.setVisibility(View.GONE);

				return;
			}

			// This field is used for maintaining state
			this.locations = result;

			lblResult.setVisibility(View.GONE);
			lstFoundLocations.setVisibility(View.VISIBLE);
			lstFoundLocations.setAdapter(new LocationSearchListAdapter(this,
					// android.R.layout.simple_list_item_multiple_choice
					R.layout.listitem_location, locations));
			break;
		case DOWNLOAD_ERROR:
			lblResult.setText(strResultNetworkError);
			lblResult.setVisibility(View.VISIBLE);
			lstFoundLocations.setVisibility(View.GONE);
			break;
		case INVALID_NAME:
		case OTHER_ERROR:
		case STATUS_ERROR:
			lblResult.setText(strResultUnknownError);
			lblResult.setVisibility(View.VISIBLE);
			lstFoundLocations.setVisibility(View.GONE);
			break;
		default:
			Log.w("EditLocationActivity", "processFoundLocations: switch"
					+ " entered default case, this should not happen!");
			break;
		}
	}

	/**
	 * Listen for the done button in the text field.
	 * 
	 * @see http://stackoverflow.com/questions/5677563/listener-for-done-button-on-edittext
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
			// fake button press
			this.onClick(btnLocationSearch);
			return true;
		}

		return false;
	}

//	@Override
//	public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
//			long id) {
//
//		Location location = (Location) parent.getItemAtPosition(position);
//		Log.d("EditLocationActivity", "onItemLongClick: location = "
//				+ location);
//		
//		if (location != null) {
//			Intent intent = new Intent(Intent.ACTION_VIEW);
//	        intent.setData(Uri.parse("geo:0,0?q=" + location.getLatitude() + ","
//	        		+ location.getLongitude()));
//	        startActivity(intent);
//	        
//	        return true;
//		}
//
//		return false;
//	}
}
