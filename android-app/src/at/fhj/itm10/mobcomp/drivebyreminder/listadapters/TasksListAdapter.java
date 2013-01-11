package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import java.text.DateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

/**
 * List view adapter for displaying all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class TasksListAdapter extends SimpleCursorAdapter {

	private Context context;
	
	private String strTaskItemNoDate;
	
	private String strSnoozeMarker;
	
	/**
	 * Create an instance by context and cursor.
	 * 
	 * @param context  the context
	 * @param cursor cursor
	 * @return AllTasksListAdapter
	 */
	public static TasksListAdapter newInstance(Context context, Cursor cursor) {
		Log.v("TasksListAdapter", "newInstance: context = " + context);
		Log.v("TasksListAdapter", "newInstance: cursor = " + cursor);
		
		return new TasksListAdapter(context, R.layout.listitem_task, cursor,
				new String[] { "title", "startDate", "endDate", "noDate",
						"snoozeDate", "description", "done" },
				new int[] { R.id.lblTaskTitle, R.id.lblTaskDate,
						R.id.lblTaskDescription, R.id.lblTaskSnoozed });
	}

	public TasksListAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to, 0);
		setViewBinder(new TaskItemViewBinder());
		
		strTaskItemNoDate = context.getString(R.string.fragment_alltasks_task_nodate);
		strSnoozeMarker = context.getString(R.string.fragment_alltasks_task_snoozed);

		this.context = context;
	}

	private class TaskItemViewBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int index) {
			// Save the id to the list element using the tag property
			((View) view.getParent()).setTag(cursor.getLong(
					cursor.getColumnIndex("id")));
			
			// Putting the DateFormat's up as object variables somehow produces
			// NullPointerExceptions...
			DateFormat formatterDate
					= android.text.format.DateFormat.getLongDateFormat(context);
			DateFormat formatterTime
					= android.text.format.DateFormat.getTimeFormat(context);

			switch (view.getId()) {
			case R.id.lblTaskTitle:
				TextView taskTitle = (TextView) view;
				taskTitle.setText(cursor.getString(
						cursor.getColumnIndex("title")));

				int done = cursor.getInt(cursor.getColumnIndex("done"));
				Log.v(getClass().getSimpleName(), "setViewValue(): index = " + index
						+ ", done = " + done);
				
				if (done != 0) {
					// Strike trough for tasks with "done" flag
					taskTitle.setPaintFlags(taskTitle.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				}

				return true;
			case R.id.lblTaskSnoozed:
				long now = Calendar.getInstance().getTimeInMillis();
				long snooze = cursor.getLong(cursor.getColumnIndex("snoozeDate"));
				TextView taskSnoozed = (TextView) view;
				
				if (snooze != 0 && snooze > now) {
					taskSnoozed.setText(" " + strSnoozeMarker);
				} else {
					taskSnoozed.setText(""); // remove default layout text
				}

				return true;
			case R.id.lblTaskDate:
				TextView taskDate = (TextView) view;
				taskDate.setText(strTaskItemNoDate);
				
				// No date = 0? *Care* about saved dates...
				if (cursor.getInt(cursor.getColumnIndex("noDate")) == 0) {
					// Get timestamps and convert them
					long startTimestamp = cursor.getLong(
							cursor.getColumnIndex("startDate"));
					long endTimestamp = cursor.getLong(
							cursor.getColumnIndex("endDate"));
					
					Calendar startDate = Calendar.getInstance();
					Calendar endDate = Calendar.getInstance();
					startDate.setTimeInMillis(startTimestamp);
					endDate.setTimeInMillis(endTimestamp);

					if (isOneDay(startDate, endDate)) {
						// Same day, treat as all day - though it should not happen...
						taskDate.setText(formatterDate.format(startDate.getTime()));
					} else {
						StringBuilder sb = new StringBuilder();
						sb.append(formatterDate.format(startDate.getTime()))
							.append(", ")
							.append(formatterTime.format(startDate.getTime()))
							.append(" - ")
							.append(formatterDate.format(endDate.getTime()))
							.append(", ")
							.append(formatterTime.format(endDate.getTime()));

						taskDate.setText(sb.toString());
					}
				}

				return true;
			case R.id.lblTaskDescription:
				TextView taskDescription = (TextView) view;
				taskDescription.setText(cursor.getString(
						cursor.getColumnIndex("description")));

				return true;
			default:
				return false;
			}
		}

		private boolean isOneDay(Calendar startDate, Calendar endDate) {
			if (startDate.compareTo(endDate) == 0) {
				return true;
			}
			
			// See add task and modify task... this is the way an all-day task is saved
			if (startDate.get(Calendar.DAY_OF_MONTH) == endDate.get(Calendar.DAY_OF_MONTH)
					|| startDate.get(Calendar.MONTH) == endDate.get(Calendar.MONTH)
					|| startDate.get(Calendar.YEAR) == endDate.get(Calendar.YEAR)
					|| startDate.get(Calendar.HOUR_OF_DAY) == 0
					|| startDate.get(Calendar.MINUTE) == 0
					|| endDate.get(Calendar.HOUR_OF_DAY) == 23
					|| endDate.get(Calendar.MINUTE) == 59) {
				return true;
			}
			
			return false;
		}
	}

}
