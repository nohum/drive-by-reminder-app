package at.fhj.itm10.mobcomp.drivebyreminder.database;

import android.content.Context;
import android.database.Cursor;

/**
 * Custom task cursor loader.
 * 
 * @author Wolfgang Gaar
 * @see https://bitbucket.org/ssutee/418496_mobileapp/src/fc5ee705a2fd/demo/DotDotListDB/src/th/ac/ku/android/sutee/dotdotlist/DotDotListDBActivity.java?at=master
 */
public class AllTasksCursorLoader extends SimpleCursorLoader {
	
	private TaskDataDAO taskDataDao;
	
	public AllTasksCursorLoader(Context context, TaskDataDAO dbDao) {
		super(context);
		this.taskDataDao = dbDao;
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = taskDataDao.findAllTasksCursor();
		
		if (cursor != null) {
			// Mimic access to cursor
			cursor.getCount();
		}
		
		return cursor;
	}

}
