package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import java.text.DateFormat;
import java.util.Calendar;

import roboguice.inject.InjectResource;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

/**
 * List view adapter for displaying all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksListAdapter extends SimpleCursorAdapter {

	private Context context;
	
	@InjectResource(R.string.fragment_alltasks_task_nodate)
	private String strTaskItemNoDate;
	
	/**
	 * Create an instance by context and cursor.
	 * 
	 * @param context  the context
	 * @param cursor cursor
	 * @return AllTasksListAdapter
	 */
	public static AllTasksListAdapter newInstance(Context context, Cursor cursor) {
		return new AllTasksListAdapter(context, R.layout.listitem_task, cursor,
				new String[] { "title", "startDate", "endDate", "noDate", "done" },
				new int[] { R.id.lblTaskTitle, R.id.lblTaskDate });
		
		/*`id`, `title`, `startDate`, `endDate`, `noDate`, `done` */
	}

	public AllTasksListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to, 0);
		setViewBinder(new TaskItemViewBinder());
		
		this.context = context;
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		return super.getView(position, convertView, parent);
//	}
	
	private class TaskItemViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Cursor cursor, int index) {
			// Save the id to the list element using the tag property
			((View) view.getParent()).setTag(cursor.getLong(
					cursor.getColumnIndex("id")));

			switch (view.getId()) {
			case R.id.lblTaskTitle:
				TextView taskTitle = (TextView) view;
				taskTitle.setText(cursor.getString(
						cursor.getColumnIndex("title")));
				if (cursor.getInt(cursor.getColumnIndex("done")) != 0) {
					// Strike trough for tasks with "done" flag
					taskTitle.setPaintFlags(taskTitle.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				}
				return true;
			case R.id.lblTaskDate:
				TextView taskDate = (TextView) view;
				taskDate.setText(strTaskItemNoDate);
				if (cursor.getInt(cursor.getColumnIndex("noDate")) == 0) {
					long startTimestamp = cursor.getLong(
							cursor.getColumnIndex("startDate"));
					long endTimestamp = cursor.getLong(
							cursor.getColumnIndex("endDate"));
					
					Calendar startDate = Calendar.getInstance();
					Calendar endDate = Calendar.getInstance();
					
					startDate.setTimeInMillis(startTimestamp);
					endDate.setTimeInMillis(endTimestamp);
					
					DateFormat formatter = null;
					if (startTimestamp == endTimestamp) {
						formatter = android.text.format.DateFormat
								.getLongDateFormat(context);

						taskDate.setText(formatter.format(startDate.getTime()));
					} else {
						formatter = android.text.format.DateFormat
								.getMediumDateFormat(context);
						StringBuilder sb = new StringBuilder();
						sb.append(formatter.format(startDate.getTime()))
							.append(" - ")
							.append(formatter.format(endDate.getTime()));
						
						taskDate.setText(sb.toString());
					}
				}
				return true;
			default:
				return false;
			}
		}
	}

}
