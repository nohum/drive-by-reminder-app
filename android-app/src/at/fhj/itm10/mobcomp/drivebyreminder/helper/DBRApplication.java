package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.app.Application;

/**
 * Android application object.
 * 
 * @author Wolfgang Gaar
 */
public class DBRApplication extends Application {

	private TaskDataDAO taskDataManager;
	
	public TaskDataDAO getTaskDataManager() {
		if (taskDataManager == null) {
			taskDataManager = new TaskDataDAO(new TaskStorageHelper(
					getApplicationContext()));
		}

		return taskDataManager;
	}
}
