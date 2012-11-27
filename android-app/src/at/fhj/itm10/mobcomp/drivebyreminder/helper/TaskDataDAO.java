package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Task;

/**
 * Task data keeper.
 * Stores all tasks, reads and writes to the database.
 * 
 * @author Wolfgang Gaar
 */
public class TaskDataDAO {

	private TaskStorageHelper db;

	private SQLiteDatabase writableDb;

	/**
	 * Create a task data keeper DAO.
	 * 
	 * @param storageHelper
	 */
	public TaskDataDAO(TaskStorageHelper storageHelper) {
		this.db = storageHelper;
		writableDb = this.db.getWritableDatabase();
	}

	public List<Task> findAllTasks() {
		return null;
	}
	
	public Task findTaskById(long id) {
		return null;
	}
	
	public long insert(Task task) {
		return 0;
	}
	
	public boolean delete(Task task) {
		return false;
	}
	
	public boolean update(Task task) {
		return false;
	}

	public List<Location> findAllByTask(Task task) {
		return null;
	}

	public List<Location> findNearestByLocation(double latitude,
			double longitude) {
		return null;
	}

	public long insert(Location location) {
		return 0;
	}
	
	public boolean delete(Location location) {
		return false;
	}

	public boolean update(Location location) {
		return false;
	}
}
