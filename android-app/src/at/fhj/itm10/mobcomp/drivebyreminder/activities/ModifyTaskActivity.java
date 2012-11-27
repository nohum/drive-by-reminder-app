package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import java.util.List;

import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DeleteTaskDialogHelper;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.DeleteTaskDialogHelper.DialogListener;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Task;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Modify task activity.
 * 
 * @author Wolfgang Gaar
 */
public class ModifyTaskActivity extends AddTaskActivity {

	private int dataId;
	
	@InjectView(R.id.done_container)
	private LinearLayout doneContainer;
	
	@InjectView(R.id.chbDone)
	private CheckBox chbDone;
	
    @InjectResource(R.string.dialog_delete_task_question)
	private String strDeleteDialogQuestion;
	
    @InjectResource(R.string.dialog_delete_task_positive)
	private String strDeleteDialogPositive;
	
    @InjectResource(R.string.dialog_delete_task_negative)
	private String strDeleteDialogNegative;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doneContainer.setVisibility(LinearLayout.VISIBLE);
		
		dataId = getIntent().getIntExtra("taskId", 0);
		if (dataId == 0) {
			throw new IllegalArgumentException("taskId extra must be given");
		}

		this.loadData(dataId);
	}
	
	@Override
	public void onResume() {
		doneContainer.setVisibility(LinearLayout.VISIBLE);
		
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_modifytask, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Back button is handled in parent class
        case R.id.menu_modifytask_save:
        	if (this.saveData(dataId)) {
        		this.setResult(Activity.RESULT_OK);
        		this.finish();
        	}

        	return true;
        case R.id.menu_modifytask_delete:
        	handleDelete();
        	return true;
        default:
        	break;
        }

        return super.onOptionsItemSelected(item);
    }

	/**
	 * Load data from database.
	 * 
	 * @param id the id of the task to load
	 */
	private void loadData(int id) {
		Task task = taskDataDAO.findTaskById(id);
		if (task == null) {
			throw new IllegalStateException("no task found for id: " + id);
		}

		txtTitle.setText(task.getTitle());
		chbDateBoundaries.setChecked(!task.isNoDate());
		
		changeDateButtonsEnabledState(chbDateBoundaries.isChecked());
		
		startDateTime = task.getStartDate();
		endDateTime = task.getEndDate();
		
		txtDescription.setText(task.getDescription());

		selCustomProximitry.setSelection(task.getCustomProximitry());
		
		associatedLocations = taskDataDAO.findAllLocationsByTask(task);
		for (Location location : associatedLocations) {
			location.setLocationChooserSelected(true);
		}

		refreshViewsWithValues();
	}

	/**
	 * Save all data. Must return true if successful.
	 * 
	 * @param id the id of the task to save
	 * @return true if save operation was successful
	 */
	private boolean saveData(int id) {
		if (TextUtils.isEmpty(txtTitle.getText())) {
			Toast.makeText(this, strSaveValidationNoTitle, Toast.LENGTH_LONG)
					.show();
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

		taskDataDAO.update(task);

		// Delete old locations first
		List<Location> oldLocations = taskDataDAO.findAllLocationsByTask(task);
		for (Location oldLocation : oldLocations) {
			taskDataDAO.delete(oldLocation);
		}
		
		// Insert new locations
		for (Location location : associatedLocations) {
			location.setTaskId(task.getId());
			taskDataDAO.insert(location);
		}

		return true;
	}

	/**
	 * Shows a delete dialog.
	 */
	private void handleDelete() {
		DeleteTaskDialogHelper dialog = new DeleteTaskDialogHelper(
				strDeleteDialogQuestion, strDeleteDialogPositive,
				strDeleteDialogNegative);

		dialog.setOnPositiveClickListener(new DialogListener() {
			
			@Override
			public void execute() {
				Task task = taskDataDAO.findTaskById(dataId);
				if (task != null) {
					taskDataDAO.delete(task);

					ModifyTaskActivity.this.setResult(Activity.RESULT_OK);
					ModifyTaskActivity.this.finish();
				}
			}
		});
		
		dialog.show(this);
	}
}
