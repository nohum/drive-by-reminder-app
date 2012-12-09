package at.fhj.itm10.mobcomp.drivebyreminder.receiver;

import java.util.Calendar;

import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskStorageHelper;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Task;

/**
 * Used to snooze tasks whose notifications have been cleared (="dismissed").
 * 
 * @author Wolfgang Gaar
 */
public class TaskSnoozeReceiver extends RoboBroadcastReceiver {

	@Override
	protected void handleReceive(Context context, Intent intent) {
		if (intent == null || intent.getLongExtra("taskId", 0) == 0) {
			return;
		}
		
		TaskDataDAO dao = new TaskDataDAO(new TaskStorageHelper(
				context));

		// Get element
		long taskId = intent.getLongExtra("taskId", 0);
		Task task = dao.findTaskById(taskId);
		if (task == null) {
			Log.w(getClass().getSimpleName(), "no valid task id given: "
					+ taskId);
			return;
		}

		int snoozeTime = Integer.parseInt(PreferenceManager
				// 20 = see res/arrays.xml
				.getDefaultSharedPreferences(context).getString("snoozeTime", "20"));
		Log.v(getClass().getSimpleName(), "given converted snooze time: "
				+ snoozeTime);

		Calendar snoozeDate = Calendar.getInstance();
		Log.v(getClass().getSimpleName(), "snooze now = " + snoozeDate.getTime());
		// The easiest way to add minutes regarding not taking care of hour boundaries
		snoozeDate.roll(Calendar.MINUTE, snoozeTime);
		Log.v(getClass().getSimpleName(), "snooze future = " + snoozeDate.getTime());

		task.setSnoozeDate(snoozeDate);

		dao.update(task);
		dao.close();
		
		super.handleReceive(context, intent);
	}
	
}
