package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database storage class.
 * 
 * @author Wolfgang Gaar
 */
public class TaskStorageHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tasks";

	private static final int DATABASE_VERSION = 1;
	
	private static final String TABLE_TASKS_SQL = "CREATE TABLE `tasks` (" +
			"`id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ," +
			"`title` TEXT NOT NULL ," +
			"`description` TEXT NOT NULL ," +
			"`customProximitry` INTEGER," +
			"`startDate` DATETIME," +
			"`endDate` DATETIME," +
			"`noDate` BOOL NOT NULL  DEFAULT false," +
			"`done` BOOL NOT NULL  DEFAULT false," +
			"`sorting` INTEGER NOT NULL )";
	
	private static final String TABLE_LOCATIONS_SQL = "CREATE TABLE `locations` (" +
			"`id` INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL ," +
			"`taskId` INTEGER NOT NULL ," +
			"`title` TEXT NOT NULL ," +
			"`address` TEXT NOT NULL ," +
			"`latitude` DOUBLE NOT NULL ," +
			"`longitude` DOUBLE NOT NULL )";

	public TaskStorageHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_TASKS_SQL);
		db.execSQL(TABLE_LOCATIONS_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Empty by now, not needed
	}

}
