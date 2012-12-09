package at.fhj.itm10.mobcomp.drivebyreminder.database;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Task;
import at.fhj.itm10.mobcomp.drivebyreminder.models.TaskLocationResult;

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
		if (db == null || !db.isOpen()) {
			db = helper.getWritableDatabase();
		}
	}
	
	/**
	 * Closes database connection.
	 */
	public void close() {
		db.close();
	}
	
	public Cursor findAllTasksForFragmentCursor() {
		open();

		return db.rawQuery("SELECT `id` as _id, * FROM "
				+ TaskStorageHelper.TABLE_TASKS_NAME
				+ " ORDER BY `sorting` ASC, `noDate` DESC", null);
	}

	public List<Task> findAllTasks() {
		open();

		Cursor cursor = db.query(TaskStorageHelper.TABLE_TASKS_NAME, null,
				null, null, null, null, null);
		cursor.moveToFirst();

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
		open();

		Cursor cursor = db.query(TaskStorageHelper.TABLE_TASKS_NAME, null,
				"id = ?", new String[] { String.valueOf(id) }, null, null,
				null);
		cursor.moveToFirst();
		
		if (cursor.getCount() == 0) {
			return null;
		}
		
		Task result = fillTaskByCursor(cursor);

		cursor.close();
		return result;
	}
	
	public long findTaskHighestSortingNumber() {
		open();

		Cursor cursor = db.rawQuery("SELECT MAX(sorting) as sortNum FROM "
				+ TaskStorageHelper.TABLE_TASKS_NAME, null);
		cursor.moveToFirst();

		if (cursor.getCount() == 0) {
			return 1;
		}

		long result = cursor.getLong(cursor.getColumnIndex("sortNum"));
		return result > 1 ? result : 1;
	}
	
	public long insert(Task task) {
		open();

		return db.insert(TaskStorageHelper.TABLE_TASKS_NAME, null,
				fillContentValuesByTask(task));
	}

	public void delete(Task task) {
		open();

		db.delete(TaskStorageHelper.TABLE_TASKS_NAME, "id = ?",
				new String[] { String.valueOf(task.getId()) });
	}
	
	public void update(Task task) {
		open();

		db.update(TaskStorageHelper.TABLE_TASKS_NAME,
				fillContentValuesByTask(task), "id = ?",
				new String[] { String.valueOf(task.getId()) });
	}

	public List<Location> findAllLocationsByTask(Task task) {
		open();

		Cursor cursor = db.query(TaskStorageHelper.TABLE_LOCATIONS_NAME, null,
				"taskId = ?", new String[] { String.valueOf(task.getId()) },
				null, null, null);
		cursor.moveToFirst();
		
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

	public Cursor findLocationsByBoundariesForFragmentCursor(Calendar date,
			double minLatitude, double minLongitude, double maxLatitude,
			double maxLongitude) {
		open();

		return db.rawQuery("SELECT `id` as _id, * FROM "
				+ TaskStorageHelper.TABLE_TASKS_NAME
				+ " ORDER BY `sorting` ASC, `noDate` DESC", null);
	}
	
	/**
	 * Find locations by certain boundaries.
	 * 
	 * @param date
	 * @param minLatitude
	 * @param minLongitude
	 * @param maxLatitude
	 * @param maxLongitude
	 * @return List<BoundedLocation>
	 */
	public List<TaskLocationResult> findLocationsByBoundaries(Calendar date,
			double minLatitude, double minLongitude, double maxLatitude,
			double maxLongitude) {
		open();

		/* Original query:
		SELECT l.taskId, t.customProximitry, t.title, t.description, t.snoozeDate,
				l.latitude, l.longitude FROM locations l
		INNER JOIN tasks t ON l.taskId = t.id
		WHERE t.done = 0 AND (t.noDate = 1 OR (t.noDate = 0 AND 1222211
				BETWEEN t.startDate AND t.endDate)) AND l.latitude BETWEEN 1 AND 2
				AND l.longitude BETWEEN 1 AND 2
		*/

		Cursor cursor = db.rawQuery("SELECT l.taskId, t.customProximitry, t.title, "
				+ "t.description, t.snoozeDate, l.latitude, l.longitude FROM "
				+ TaskStorageHelper.TABLE_LOCATIONS_NAME + " l INNER JOIN "
				+ TaskStorageHelper.TABLE_TASKS_NAME + " t ON l.taskId = t.id "
				+ "WHERE t.done = 0 AND (t.noDate = 1 OR (t.noDate = 0 AND ? "
				+ "BETWEEN t.startDate AND t.endDate)) AND l.latitude BETWEEN ? "
				+ "AND ? AND l.longitude BETWEEN ? AND ?", new String[] {
						String.valueOf(date.getTimeInMillis()),
						String.valueOf(minLatitude), String.valueOf(maxLatitude),
						String.valueOf(minLongitude), String.valueOf(maxLongitude)});
		cursor.moveToFirst();
		
//		String debugSql = "SELECT l.taskId, t.customProximitry, t.title, t.description, t.snoozeDate,"
//		+ " l.latitude, l.longitude FROM locations l INNER JOIN tasks t ON l.taskId = t.id"
//		+ " WHERE t.done = 0 AND (t.noDate = 1 OR (t.noDate = 0 AND " + String.valueOf(
//				date.getTimeInMillis())
//		+ " BETWEEN t.startDate AND t.endDate)) AND l.latitude BETWEEN " + String.valueOf(minLatitude)
//		+ " AND " + String.valueOf(maxLatitude) + " AND l.longitude BETWEEN "
//		+ String.valueOf(minLongitude) + " AND " + String.valueOf(maxLongitude);
//		Log.v(getClass().getSimpleName(), "findLocationsByBoundaries sql = " + debugSql);

		List<TaskLocationResult> foundLocations =
				new ArrayList<TaskLocationResult>(cursor.getCount());

		while (!cursor.isAfterLast()) {
			TaskLocationResult b = new TaskLocationResult();
			b.setTaskId(cursor.getLong(0));
			b.setCustomProximitry(cursor.getInt(1));
			b.setTitle(cursor.getString(2));
			b.setDescription(cursor.getString(3));
			
			long snoozeDate = cursor.getLong(4);
			if (snoozeDate == 0) {
				b.setSnoozeDate(null);
			} else {
				Calendar snooze = Calendar.getInstance();
				snooze.setTimeInMillis(snoozeDate);
				b.setSnoozeDate(snooze);
			}

			b.setLatitude(cursor.getDouble(5));
			b.setLongitude(cursor.getDouble(6));

			foundLocations.add(b);
			cursor.moveToNext();
		}
		
		return foundLocations;
	}

	public long insert(Location location) {
		open();

		return db.insert(TaskStorageHelper.TABLE_LOCATIONS_NAME, null,
				fillContentValuesByLocation(location));
	}

	public void delete(Location location) {
		open();

		db.delete(TaskStorageHelper.TABLE_LOCATIONS_NAME, "id = ?",
				new String[] { String.valueOf(location.getId()) });
	}

	public void update(Location location) {
		open();

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

		task.setId(cursor.getLong(cursor.getColumnIndex("id")));
		task.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		task.setDescription(cursor.getString(
				cursor.getColumnIndex("description")));
		task.setCustomProximitry(cursor.getInt(
				cursor.getColumnIndex("customProximitry")));

		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(cursor.getLong(
				cursor.getColumnIndex("startDate")));
		task.setStartDate(start);

		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("endDate")));

		task.setEndDate(end);
		task.setNoDate(cursor.getInt(
				cursor.getColumnIndex("noDate")) == 0 ? false : true);
		task.setDone(cursor.getInt(
				cursor.getColumnIndex("done")) == 0 ? false : true);

		long snoozeData = cursor.getLong(cursor.getColumnIndex("snoozeDate"));
		if (snoozeData == 0) {
			task.setSnoozeDate(null);
		} else {
			Calendar snooze = Calendar.getInstance();
			snooze.setTimeInMillis(snoozeData);
			task.setSnoozeDate(snooze);
		}

		task.setSorting(cursor.getLong(cursor.getColumnIndex("sorting")));

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
		
		if (task.getId() > 0) {
			values.put("id", task.getId());
		}

		values.put("title", task.getTitle());
		values.put("description", task.getDescription());
		values.put("customProximitry", task.getCustomProximitry());
		values.put("startDate", task.getStartDate().getTimeInMillis());
		values.put("endDate", task.getEndDate().getTimeInMillis());
		values.put("noDate", task.isNoDate() ? 1 : 0);
		values.put("done", task.isDone() ? 1 : 0);
		if (task.getSnoozeDate() == null) {
			values.put("snoozeDate", 0);
		} else {
			values.put("snoozeDate", task.getSnoozeDate().getTimeInMillis());
		}
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
		
		location.setId(cursor.getLong(cursor.getColumnIndex("id")));
		location.setTaskId(cursor.getLong(cursor.getColumnIndex("taskId")));
		location.setName(cursor.getString(cursor.getColumnIndex("title")));
		location.setAddress(cursor.getString(cursor.getColumnIndex("address")));
		location.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
		location.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));

		return location;
	}

	/**
	 * Fills a content values object for database insert or updates.
	 * 
	 * @param location the location
	 * @return A filled ContentValues object
	 */
	private ContentValues fillContentValuesByLocation(Location location) {
		ContentValues values = new ContentValues();

		if (location.getId() > 0) {
			values.put("id", location.getId());
		}

		values.put("taskId", location.getTaskId());
		values.put("title", location.getName());
		values.put("address", location.getAddress());
		values.put("latitude", location.getLatitude());
		values.put("longitude", location.getLongitude());

		return values;
	}
}
