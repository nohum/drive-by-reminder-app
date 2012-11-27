package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import android.app.Activity;
import android.os.Bundle;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Modify task activity.
 * 
 * @author Wolfgang Gaar
 */
public class ModifyTaskActivity extends AddTaskActivity {

	private int dataId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dataId = getIntent().getIntExtra("taskId", 0);
		if(dataId == 0) {
			throw new IllegalArgumentException("taskId extra must be given");
		}

		this.loadData(dataId);
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
        	
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }
	
	/**
	 * Load data from database.
	 */
	private void loadData(int id) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Save all data. Must return true if successful.
	 */
	private boolean saveData(int id) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
