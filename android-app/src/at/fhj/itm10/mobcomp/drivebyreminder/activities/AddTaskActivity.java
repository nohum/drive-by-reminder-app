package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DataSingletonStorage;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;

/**
 * Add task activity.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_addtask)
public class AddTaskActivity extends RoboSherlockActivity
		implements OnCheckedChangeListener, OnClickListener, OnTimeSetListener,
				OnDateSetListener {

	@InjectView(R.id.txtTitle)
	private TextView txtTitle;
	
	@InjectView(R.id.btnLocation)
	private Button btnLocation;
	
	@InjectResource(R.string.activity_addtask_form_location_button)
	private String strButtonSetLocation;
	
	@InjectResource(R.string.activity_addtask_form_location_button_multiple_prefix)
	private String strButtonLocationMultipe;

	@InjectView(R.id.chbDateBoundaries)
	private CheckBox chbDateBoundaries;
	
	@InjectView(R.id.btnStartDate)
	private Button btnStartDate;
	
	@InjectView(R.id.btnStartTime)
	private Button btnStartTime;
	
	private Calendar startDateTime;
	
	@InjectView(R.id.btnEndDate)
	private Button btnEndDate;
	
	@InjectView(R.id.btnEndTime)
	private Button btnEndTime;
	
	private Calendar endDateTime;
	
	@InjectView(R.id.txtDescription)
	private TextView txtDescription;
	
	@InjectView(R.id.selCustomProximitry)
	private Spinner selCustomProximitry;
	
	private java.text.DateFormat systemDateFormat;
	
	private java.text.DateFormat systemTimeFormat;
	
	private boolean systemTime24Hours;
	
	private OpenedPickerType openedPicker;
	
	private List<Location> associatedLocations = null;
	
	@Inject
	private DataSingletonStorage dataStorage;
	
	/**
	 * Indicator for opened picker.
	 * 
	 * @author Wolfgang Gaar
	 */
	private enum OpenedPickerType {
		STARTDATE,
		STARTTIME,
		ENDDATE,
		ENDTIME
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (savedInstanceState == null) {
        	setDefaultValues();
        } else {
        	restoreFromState(savedInstanceState);
        }

        initSystemDateTimeFormats();
        refreshViewsWithValues();
        initViewEvents();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_addtask, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.menu_addtask_save:
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.v("AddTaskActivity", "onSaveInstanceState");
	
		// Title and description field are persisting themselves!

		// TODO: Locationdata
		outState.putBoolean("useDateBoundaries", chbDateBoundaries.isChecked());
		outState.putLong("startDateTime", startDateTime.getTimeInMillis());
		outState.putLong("endDateTime", endDateTime.getTimeInMillis());

		outState.putInt("customProximitry", selCustomProximitry.getSelectedItemPosition());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState == null) {
			restoreFromState(savedInstanceState);
			refreshViewsWithValues();
		}
	}

	/**
	 * Set activity default values for views.
	 */
    private void setDefaultValues() {
    	startDateTime = Calendar.getInstance();
		startDateTime.setTime(Calendar.getInstance().getTime());
		endDateTime = (Calendar) startDateTime.clone();
		
		selCustomProximitry.setSelection(0); // 0 = default setting
		chbDateBoundaries.setChecked(false);
	}

	private void restoreFromState(Bundle savedInstanceState) {
		// Title and description field are restoring themselves!

		// TODO: Locationdata
		chbDateBoundaries.setChecked(savedInstanceState.getBoolean("useDateBoundaries"));

		startDateTime = Calendar.getInstance();
		startDateTime.setTimeInMillis(savedInstanceState.getLong("startDateTime"));
		endDateTime = Calendar.getInstance();
		endDateTime.setTimeInMillis(savedInstanceState.getLong("endDateTime"));
//		txtDescription.setText(savedInstanceState.getString("description"));
		selCustomProximitry.setSelection(savedInstanceState.getInt("customProximitry"), false);
	}
	
	/**
	 * Init all views - show values.
	 */
	private void refreshViewsWithValues() {
		// Disable the date buttons if "no date" has been selected
		changeDateButtonsEnabledState(chbDateBoundaries.isChecked());

		// ... but also set the date buttons to the value of their date variables
		btnStartDate.setText(systemDateFormat.format(startDateTime.getTime()));
		btnStartTime.setText(systemTimeFormat.format(startDateTime.getTime()));
		btnEndDate.setText(systemDateFormat.format(endDateTime.getTime()));
		btnEndTime.setText(systemTimeFormat.format(endDateTime.getTime()));
		
	}
	
	private void changeDateButtonsEnabledState(boolean newState) {
		btnStartDate.setEnabled(newState);
		btnStartTime.setEnabled(newState);
		btnEndDate.setEnabled(newState);
		btnEndTime.setEnabled(newState);
	}
	
	/**
	 * Add all necessary events to the views.
	 */
	private void initViewEvents() {
		chbDateBoundaries.setOnCheckedChangeListener(this);
		
		btnStartDate.setOnClickListener(this);
		btnStartTime.setOnClickListener(this);
		btnEndDate.setOnClickListener(this);
		btnEndTime.setOnClickListener(this);
		btnLocation.setOnClickListener(this);
		
	}

	/**
	 * Retrieves the date and time format from the system settings.
	 * @see http://stackoverflow.com/questions/6981505/android-get-user-selected-date-format
	 */
	private void initSystemDateTimeFormats() {
		// Get the formats
		systemDateFormat = DateFormat.getDateFormat(getApplicationContext());
		systemTimeFormat = DateFormat.getTimeFormat(getApplicationContext());
		systemTime24Hours = DateFormat.is24HourFormat(getApplicationContext());
	}

	/**
	 * Used for the alert date checkbox.
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.equals(chbDateBoundaries)) {
			changeDateButtonsEnabledState(isChecked);
		}
	}

	/**
	 * Used for the date selection, location buttons.
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		if (view.equals(btnStartDate)) {
			openedPicker = OpenedPickerType.STARTDATE;
			retrieveDatePicker(startDateTime).show();
		} else if (view.equals(btnStartTime)) {
			openedPicker = OpenedPickerType.STARTTIME;
			retrieveTimePicker(startDateTime).show();
		} else if (view.equals(btnEndDate)) {
			openedPicker = OpenedPickerType.ENDDATE;
			retrieveDatePicker(endDateTime).show();
		} else if (view.equals(btnEndTime)) {
			openedPicker = OpenedPickerType.ENDTIME;
			retrieveTimePicker(endDateTime).show();
		} else if (view.equals(btnLocation)) {
			Intent intent = new Intent(this, EditLocationActivity.class);
			intent.putExtra("loadFromStorage", false);

			if (this.associatedLocations != null) {
				dataStorage.setData("locationsToShow", this.associatedLocations);
				intent.putExtra("loadFromStorage", true);
			}
			
			this.startActivityForResult(intent, 1);
		}
	}

	@SuppressWarnings("unchecked")
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode) {
		case 1:
			// Process location chooser activity
			if (resultCode == Activity.RESULT_OK) {
				Log.d("AddTaskActivity", "onActivityResult: return code = RESULT_OK");
				Log.d("AddTaskActivity", "dataStorage = " + dataStorage);

				associatedLocations = (List<Location>) dataStorage
						.getData("locationsToSave");

				Log.d("AddTaskActivity", "onActivityResult: locationsToSave = "
						+ associatedLocations);

				// Handle different kinds of locations
				if (associatedLocations == null || associatedLocations.size() == 0) {
					btnLocation.setText(strButtonSetLocation);
				} else if (associatedLocations.size() == 1) {
					btnLocation.setText(associatedLocations.get(0).getName() + ", "
							+ associatedLocations.get(0).getAddress());
				} else {
					Set<String> locList = new LinkedHashSet<String>();
					for (Location location : associatedLocations) {
						locList.add(location.getName());
					}

					btnLocation.setText(strButtonLocationMultipe + " "
							+ TextUtils.join(", ", locList));
				}
			}
			break;
		}
	}
	
	/**
	 * Retrieves a date picker dialog instance from a calendar.
	 * 
	 * @param cal
	 * @return DatePickerDialog
	 */
	private DatePickerDialog retrieveDatePicker(Calendar cal) {
		return new DatePickerDialog(this, this, cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Retrieves a time picker dialog instance from a calendar.
	 * 
	 * @param cal
	 * @return TimePickerDialog
	 */
	private TimePickerDialog retrieveTimePicker(Calendar cal) {
		return new TimePickerDialog(this, this, cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE), systemTime24Hours);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		Log.d("AddTaskActivity", "onTimeSet: view = " + view);
		
		if (openedPicker.equals(OpenedPickerType.STARTTIME)) {
			startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			startDateTime.set(Calendar.MINUTE, minute);
		} else if (openedPicker.equals(OpenedPickerType.ENDTIME)) {
			endDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			endDateTime.set(Calendar.MINUTE, minute);
		}

		// Refresh views
		refreshViewsWithValues();
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		Log.d("AddTaskActivity", "onDateSet: view = " + view);

		if (openedPicker.equals(OpenedPickerType.STARTDATE)) {
			startDateTime.set(Calendar.YEAR, year);
			startDateTime.set(Calendar.MONTH, monthOfYear);
			startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		} else if (openedPicker.equals(OpenedPickerType.ENDDATE)) {
			endDateTime.set(Calendar.YEAR, year);
			endDateTime.set(Calendar.MONTH, monthOfYear);
			endDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		}

		// Refresh views
		refreshViewsWithValues();
	}
}
