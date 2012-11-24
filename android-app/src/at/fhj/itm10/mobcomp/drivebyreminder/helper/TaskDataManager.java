package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.database.sqlite.SQLiteDatabase;

import com.google.inject.Inject;

import roboguice.inject.ContextSingleton;

/**
 * Task data keeper. Stores all tasks, reads and writes to
 * the database.
 * 
 * @author Wolfgang Gaar
 */
@ContextSingleton
public class TaskDataManager {

	@Inject
	private TaskStorageHelper db;

	private SQLiteDatabase writableDb;
	
	public TaskDataManager() {
		writableDb = db.getWritableDatabase();
	}
	
}
