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
import android.widget.Toast;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskStorageHelper;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DataSingletonStorage;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Task;

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
	protected TextView txtTitle;

	@InjectResource(R.string.activity_addtask_save_validation_notitle)
	protected String strSaveValidationNoTitle;

	@InjectView(R.id.btnLocation)
	private Button btnLocation;

	@InjectResource(R.string.activity_addtask_form_location_button)
	private String strButtonSetLocation;
	
	@InjectResource(R.string.activity_addtask_save_validation_nolocations)
	protected String strSaveValidationNoLocations;

	@InjectResource(
			R.string.activity_addtask_form_location_button_multiple_prefix)
	private String strButtonLocationMultipe;

	@InjectView(R.id.chbDateBoundaries)
	protected CheckBox chbDateBoundaries;

	@InjectView(R.id.btnStartDate)
	private Button btnStartDate;

	@InjectView(R.id.btnStartTime)
	private Button btnStartTime;

	protected Calendar startDateTime;

	@InjectView(R.id.btnEndDate)
	private Button btnEndDate;

	@InjectView(R.id.btnEndTime)
	private Button btnEndTime;

	protected Calendar endDateTime;

	@InjectView(R.id.txtDescription)
	protected TextView txtDescription;

	@InjectView(R.id.selCustomProximitry)
	protected Spinner selCustomProximitry;

	protected java.text.DateFormat systemDateFormat;

	protected java.text.DateFormat systemTimeFormat;

	private boolean systemTime24Hours;

	/**
	 * Indicates the currently user-opened picker dialog.
	 */
	private OpenedPickerType openedPicker;
	
	/**
	 * {@link Location Locations} associated with this task.
	 */
	protected List<Location> associatedLocations = null;
	
	@Inject
	private DataSingletonStorage dataStorage;

	/**
	 * Database DAO.
	 */
	protected TaskDataDAO taskDataDAO;

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

        taskDataDAO = new TaskDataDAO(new TaskStorageHelper(
        		getApplicationContext()));

        initSystemDateTimeFormats();
        refreshViewsWithValues();
        initViewEvents();
	}
	
	/**
	 * Add all necessary events to the views.
	 */
	protected void initViewEvents() {
		btnLocation.setOnClickListener(this);
		
		chbDateBoundaries.setOnCheckedChangeListener(this);

		btnStartDate.setOnClickListener(this);
		btnStartTime.setOnClickListener(this);
		btnEndDate.setOnClickListener(this);
		btnEndTime.setOnClickListener(this);
	}

	/**
	 * Retrieves the date and time format from the system settings.
	 * @see http://stackoverflow.com/questions/6981505/
	 */
	private void initSystemDateTimeFormats() {
		// Get the formats
		systemDateFormat = DateFormat.getMediumDateFormat(getApplicationContext());
		systemTimeFormat = DateFormat.getTimeFormat(getApplicationContext());
		systemTime24Hours = DateFormat.is24HourFormat(getApplicationContext());
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
        	this.setResult(Activity.RESULT_CANCELED);
            NavUtils.navigateUpFromSameTask(this);

            return true;
        case R.id.menu_addtask_save:
        	if (this.saveData()) {
        		this.setResult(Activity.RESULT_OK);
        		this.finish();
        	}
        	
        	return true;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putBoolean("useDateBoundaries", chbDateBoundaries.isChecked());
		outState.putLong("startDateTime", startDateTime.getTimeInMillis());
		outState.putLong("endDateTime", endDateTime.getTimeInMillis());

		outState.putInt("customProximitry", selCustomProximitry
				.getSelectedItemPosition());
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
	 * Used to save the received locations over activity restarts.
	 * 
	 * @return Object
	 */
	@Override
	public Object onRetainNonConfigurationInstance() {
	    return this.associatedLocations;
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

    /**
     * Restores state from a bundle.
     * 
     * @param savedInstanceState
     */
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void restoreFromState(Bundle savedInstanceState) {
		// Title and description field are restoring themselves!

		// Reload locations after activity restart...
        // Should use setRetainInstance
        // see: http://developer.android.com/reference/android/app/Fragment.html#setRetainInstance%28boolean%29
		List<Location> lastLocations = (List<Location>)
        		getLastNonConfigurationInstance();
        if (lastLocations != null) {
        	this.associatedLocations = lastLocations;
        }

		chbDateBoundaries.setChecked(savedInstanceState
				.getBoolean("useDateBoundaries"));
        changeDateButtonsEnabledState(savedInstanceState
        		.getBoolean("useDateBoundaries"));

		startDateTime = Calendar.getInstance();
		startDateTime.setTimeInMillis(savedInstanceState
				.getLong("startDateTime"));
		endDateTime = Calendar.getInstance();
		endDateTime.setTimeInMillis(savedInstanceState.getLong("endDateTime"));
		selCustomProximitry.setSelection(savedInstanceState
				.getInt("customProximitry"), false);
	}

	/**
	 * Init all views - show values.
	 */
	protected void refreshViewsWithValues() {
		// Disable the date buttons if "no date" has been selected
		changeDateButtonsEnabledState(chbDateBoundaries.isChecked());

		// ... but also set the date buttons to the value of their
		// date variables
		btnStartDate.setText(systemDateFormat.format(startDateTime.getTime()));
		btnStartTime.setText(systemTimeFormat.format(startDateTime.getTime()));
		btnEndDate.setText(systemDateFormat.format(endDateTime.getTime()));
		btnEndTime.setText(systemTimeFormat.format(endDateTime.getTime()));

		// Handle different kinds of locations
		if (associatedLocations == null || associatedLocations.size() == 0) {
			btnLocation.setText(strButtonSetLocation);
		} else if (associatedLocations.size() == 1) {
			btnLocation.setText(associatedLocations.get(0).getName() + ", "
					+ associatedLocations.get(0).getAddress());
		} else {
			// Multiple locations
			Set<String> locList = new LinkedHashSet<String>();
			for (Location location : associatedLocations) {
				locList.add(location.getName());
			}

			btnLocation.setText(strButtonLocationMultipe + " "
					+ TextUtils.join(", ", locList));
		}
	}
	
	/**
	 * Change the state of the date buttons. 
	 * 
	 * @param newState True to enable, false to disable
	 */
	protected void changeDateButtonsEnabledState(boolean newState) {
		btnStartDate.setEnabled(newState);
		btnStartTime.setEnabled(newState);
		btnEndDate.setEnabled(newState);
		btnEndTime.setEnabled(newState);
	}

	/**
	 * Used for the alert date checkbox.
	 * 
	 * @param buttonView checkbox
	 * @param isChecked state
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
	 * @param view the view
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
				dataStorage.setData("locationsToShow",
						this.associatedLocations);
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
				associatedLocations = (List<Location>) dataStorage
						.getData("locationsToSave");

				Log.d("AddTaskActivity", "onActivityResult: locationsToSave = "
						+ associatedLocations);

				refreshViewsWithValues();
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * Retrieves a date picker dialog instance from a calendar.
	 * 
	 * @param cal source calendar
	 * @return DatePickerDialog
	 */
	private DatePickerDialog retrieveDatePicker(Calendar cal) {
		return new DatePickerDialog(this, this, cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Retrieves a time picker dialog instance from a calendar.
	 * 
	 * @param cal data source calendar
	 * @return TimePickerDialog
	 */
	private TimePickerDialog retrieveTimePicker(Calendar cal) {
		return new TimePickerDialog(this, this, cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE), systemTime24Hours);
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
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
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
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
	
	protected boolean validateAndCorrectData() {
		if (TextUtils.isEmpty(txtTitle.getText())) {
			Toast.makeText(this, strSaveValidationNoTitle, Toast.LENGTH_LONG)
				.show();
			return false;
		}
		
		if (associatedLocations == null || associatedLocations.size() == 0) {
			Toast.makeText(this, strSaveValidationNoLocations, Toast.LENGTH_LONG)
				.show();
			return false;
		}
		
		// Be nice about swapped start and end time
		if (startDateTime.after(endDateTime)) {
			Calendar temp = startDateTime;
			startDateTime = endDateTime;
			endDateTime = temp;
		}
		
		return true;
	}
	
	/**
	 * Save all data. Must return true if successful.
	 * 
	 * @return true on success
	 */
	private boolean saveData() {
		if (!validateAndCorrectData()) {
			return false;
		}

		Task task = new Task();
		task.setTitle(txtTitle.getText().toString());
		task.setNoDate(!chbDateBoundaries.isChecked());
		task.setStartDate(startDateTime);
		task.setEndDate(endDateTime);
		task.setDone(false);
		task.setDescription(txtDescription.getText().toString());
		task.setCustomProximitry(selCustomProximitry.getSelectedItemPosition());

		long sorting = taskDataDAO.findTaskHighestSortingNumber() + 1;
		Log.d("AddTaskActivity", "new task: sorting number = " + sorting);
		task.setSorting(sorting);

		long insertId = taskDataDAO.insert(task);
		for (Location location : associatedLocations) {
			location.setTaskId(insertId);
			taskDataDAO.insert(location);
		}

		return true;
	}
}
