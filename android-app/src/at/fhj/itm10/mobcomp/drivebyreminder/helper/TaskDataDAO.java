package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Task;

/**
 * Task data keeper.
 * Stores all tasks, reads and writes to the database.
 * Combined DAO for {@link Task Tasks} and {@link Location Locations}.
 * 
 * @author Wolfgang Gaar
 */
public class TaskDataDAO {

	private TaskStorageHelper helper;

	private SQLiteDatabase db;

	/**
	 * Create a task data keeper DAO.
	 * 
	 * @param storageHelper the storage helper
	 */
	public TaskDataDAO(TaskStorageHelper storageHelper) {
		helper = storageHelper;
		open();
	}
	
	/**
	 * Opens database connection.
	 */
	public void open() {
		db = helper.getWritableDatabase();	
	}
	
	/**
	 * Closes database connection.
	 */
	public void close() {
		db.close();
	}

	public List<Task> findAllTasks() {
		Cursor cursor = db.query(TaskStorageHelper.TABLE_TASKS_NAME, null,
				null, null, null, null, null);
		
		// Set the result list length to the cursor result count
		List<Task> foundTasks = new ArrayList<Task>(cursor.getCount());
		
		while (!cursor.isAfterLast()) {
			foundTasks.add(fillTaskByCursor(cursor));
			cursor.moveToNext();
		}
		
		cursor.close();
		return foundTasks;
	}
	
	public Task findTaskById(long id) {
		Cursor cursor = db.query(TaskStorageHelper.TABLE_TASKS_NAME, null,
				"id = ?", new String[] { String.valueOf(id) }, null, null,
				null);
		
		if (cursor.getCount() == 0) {
			return null;
		}
		
		Task result = fillTaskByCursor(cursor);

		cursor.close();
		return result;
	}
	
	public long findTaskHighestSortingNumber() {
		Cursor cursor = db.rawQuery("SELECT MAX(sorting) FROM "
				+ TaskStorageHelper.TABLE_TASKS_NAME, null);
		
		if (cursor.getCount() == 0) {
			return 1;
		}
		
		long result = cursor.getLong(0);
		return result > 1 ? result : 1;
	}
	
	public long insert(Task task) {
		return db.insert(TaskStorageHelper.TABLE_TASKS_NAME, null,
				fillContentValuesByTask(task));
	}

	public void delete(Task task) {
		db.delete(TaskStorageHelper.TABLE_TASKS_NAME, "id = ?",
				new String[] { String.valueOf(task.getId()) });
	}
	
	public void update(Task task) {
		db.update(TaskStorageHelper.TABLE_TASKS_NAME,
				fillContentValuesByTask(task), "id = ?",
				new String[] { String.valueOf(task.getId()) });
	}

	public List<Location> findAllLocationsByTask(Task task) {
		Cursor cursor = db.query(TaskStorageHelper.TABLE_LOCATIONS_NAME, null,
				"taskId = ?", new String[] { String.valueOf(task.getId()) },
				null, null, null);
		
		// Set the result list length to the cursor result count
		List<Location> foundLocations =
				new ArrayList<Location>(cursor.getCount());

		while (!cursor.isAfterLast()) {
			foundLocations.add(fillLocationByCursor(cursor));
			cursor.moveToNext();
		}
		
		cursor.close();
		return foundLocations;
	}

	public List<Location> findLocationsByBoundaries(double minLatitude,
			double minLongitude, double maxLatitude, double maxLongitude) {
		return null;
	}

	public long insert(Location location) {
		return db.insert(TaskStorageHelper.TABLE_LOCATIONS_NAME, null,
				fillContentValuesByLocation(location));
	}

	public void delete(Location location) {
		db.delete(TaskStorageHelper.TABLE_LOCATIONS_NAME, "id = ?",
				new String[] { String.valueOf(location.getId()) });
	}

	public void update(Location location) {
		db.update(TaskStorageHelper.TABLE_TASKS_NAME,
				fillContentValuesByLocation(location), "id = ?",
				new String[] { String.valueOf(location.getId()) });
	}
	
	/**
	 * Fill a task model object by a database cursor. Does not modify
	 * the cursor.
	 * 
	 * @param cursor the cursor
	 * @return A filled Task object
	 */
	private Task fillTaskByCursor(Cursor cursor) {
		Task task = new Task();
		
		task.setId(cursor.getLong(0));
		task.setTitle(cursor.getString(1));
		task.setDescription(cursor.getString(2));
		task.setCustomProximitry(cursor.getInt(3));
		
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(cursor.getLong(4));
		task.setStartDate(start);
		
		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(cursor.getLong(5));
		
		task.setEndDate(end);
		task.setNoDate(cursor.getInt(6) == 0 ? false : true);
		task.setDone(cursor.getInt(7) == 0 ? false : true);
		task.setSorting(cursor.getLong(8));

		return task;
	}
	
	/**
	 * Fills a content values object for database insert or updates.
	 * 
	 * @param task the task
	 * @return A filled ContentValues object
	 */
	private ContentValues fillContentValuesByTask(Task task) {
		ContentValues values = new ContentValues();
		
		values.put("id", task.getId());
		values.put("title", task.getTitle());
		values.put("description", task.getTitle());
		values.put("customProximitry", task.getCustomProximitry());
		values.put("startDate", task.getStartDate().getTimeInMillis());
		values.put("endDate", task.getEndDate().getTimeInMillis());
		values.put("noDate", task.isNoDate() ? 1 : 0);
		values.put("done", task.isDone() ? 1 : 0);
		values.put("sorting", task.getSorting());

		return values;
	}
	
	/**
	 * Fills a location model object by a database cursor. Does not
	 * modify the cursor.
	 * 
	 * @param cursor the cursor
	 * @return A filled Task object
	 */
	private Location fillLocationByCursor(Cursor cursor) {
		Location location = new Location();
		
		location.setId(cursor.getLong(0));
		location.setTaskId(cursor.getLong(1));
		location.setName(cursor.getString(2));
		location.setAddress(cursor.getString(3));
		location.setLatitude(cursor.getDouble(4));
		location.setLongitude(cursor.getDouble(5));

		return null;
	}

	/**
	 * Fills a content values object for database insert or updates.
	 * 
	 * @param location the location
	 * @return A filled ContentValues object
	 */
	private ContentValues fillContentValuesByLocation(Location location) {
		ContentValues values = new ContentValues();

		values.put("id", location.getId());
		values.put("taskId", location.getTaskId());
		values.put("title", location.getName());
		values.put("address", location.getAddress());
		values.put("latitude", location.getLatitude());
		values.put("longitude", location.getLongitude());

		return values;
	}
}
