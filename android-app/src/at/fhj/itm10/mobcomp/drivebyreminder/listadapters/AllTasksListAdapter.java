package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

/**
 * List view adapter for displaying all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksListAdapter extends SimpleCursorAdapter {

	/**
	 * Create an instance by context and cursor.
	 * 
	 * @param context the context
	 * @param cursor cursor
	 * @return AllTasksListAdapter
	 */
	public static AllTasksListAdapter newInstance(Context context,
			Cursor cursor) {
		return new AllTasksListAdapter(context, R.layout.listitem_task,
				cursor, new String[] { "title", "description" },
        		new int[] { R.id.lblTaskTitle, R.id.lblTaskDescription });
	}
	
	public AllTasksListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to, 0);
	}

	/*
	 private class TutorialViewBinder implements SimpleCursorAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Cursor cursor, int index) {
        if (index == cursor.getColumnIndex(TutListDatabase.COL_DATE)) {
            // get a locale based string for the date
            DateFormat formatter = android.text.format.DateFormat
                    .getDateFormat(getActivity().getApplicationContext());
            long date = cursor.getLong(index);
            Date dateObj = new Date(date * 1000);
            ((TextView) view).setText(formatter.format(dateObj));
            return true;
        } else {
            return false;
        }
    }
}
	 */
}
