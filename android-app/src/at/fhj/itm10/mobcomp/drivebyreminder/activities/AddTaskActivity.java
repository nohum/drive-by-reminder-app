package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;

/**
 * Add task activity.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_addtask)
public class AddTaskActivity extends RoboSherlockActivity {

	@InjectView(R.id.txtTitle)
	private TextView txtTitle;
	
	// Location textbox here...
	
	@InjectView(R.id.chbNoDate)
	private CheckBox chbNoDate;
	
	@InjectView(R.id.btnStartDate)
	private Button btnStartDate;
	
	@InjectView(R.id.btnStartTime)
	private Button btnStartTime;
	
	private Date startDateTime;
	
	@InjectView(R.id.btnEndDate)
	private Button btnEndDate;
	
	@InjectView(R.id.btnEndTime)
	private Button btnEndTime;
	
	private Date endDateTime;
	
	@InjectView(R.id.txtDescription)
	private TextView txtDescription;
	
	@InjectView(R.id.selCustomProximitry)
	private Spinner selCustomProximitry;
	
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
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	
		outState.putString("title", txtTitle.getText().toString());
		outState.putBoolean("nodate", chbNoDate.isChecked());
		outState.putLong("startDateTime", startDateTime.getTime());
		outState.putLong("endDateTime", endDateTime.getTime());
		outState.putString("description", txtDescription.getText().toString());
//		selCustomProximitry.get
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if (savedInstanceState == null) {
			restoreFromState(savedInstanceState);
		}
	}

	/**
	 * Set activity default values for views.
	 */
    private void setDefaultValues() {
		
	}

	private void restoreFromState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}
}
