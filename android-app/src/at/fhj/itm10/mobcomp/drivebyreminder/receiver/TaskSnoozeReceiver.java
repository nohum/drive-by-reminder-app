package at.fhj.itm10.mobcomp.drivebyreminder.receiver;

import java.util.Calendar;

import roboguice.inject.InjectPreference;
import roboguice.receiver.RoboBroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
	
	@InjectPreference("snoozeTime")
	private String snoozeTime;

	@Override
	protected void handleReceive(Context context, Intent intent) {
		Log.v(getClass().getSimpleName(), "got snooze event");
		
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
		
		int snoozeTimeConverted = Integer.parseInt(snoozeTime);
		Log.d(getClass().getSimpleName(), "given converted snooze time: "
				+ snoozeTimeConverted);

		Calendar snoozeDate = Calendar.getInstance();
		Log.d(getClass().getSimpleName(), "snooze now = " + snoozeDate.getTime());
		// The easiest way to add regarding not taking care of hour boundaries
		snoozeDate.setTimeInMillis(snoozeDate.getTimeInMillis()
				+ snoozeTimeConverted * 60 * 10);
		Log.d(getClass().getSimpleName(), "snooze future = " + snoozeDate.getTime());

		task.setSnoozeDate(snoozeDate);

		dao.update(task);
		dao.close();
	}
	
}
