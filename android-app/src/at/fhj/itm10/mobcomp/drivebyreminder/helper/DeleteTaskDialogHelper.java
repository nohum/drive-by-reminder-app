package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Helper for UI-blocking delete dialogs.
 * 
 * @author Wolfgang Gaar
 * @see http://developmentality.wordpress.com/2009/10/31/android-dialog-box-tutorial/
 */
public class DeleteTaskDialogHelper {

	/**
	 * Dialog listener.
	 * 
	 * @author Wolfgang Gaar
	 */
	public interface DialogListener {
		public void execute();
	}

	/**
	 * Internal dialog listener wrapper for interface.
	 * 
	 * @author Wolfgang Gaar
	 */
	public class DialogListenerWrapper implements OnClickListener {
		private DialogListener listener;
		
		public DialogListenerWrapper(DialogListener listener) {
			this.listener = listener;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();

			if (listener != null) {
				listener.execute();
			}
		}
	}
	
	private String question;
	
	private String positiveAnswer;
	
	private String negativeAnswer;
	
	private DialogListener positiveListener = null;
	
	private DialogListener negativeListener = null;

	public DeleteTaskDialogHelper(String question, String positiveAnswer,
			String negativeAnswer) {
		this.question = question;
		this.positiveAnswer = positiveAnswer;
		this.negativeAnswer = negativeAnswer;
	}
	
	public void setOnPositiveClickListener(DialogListener listener) {
		positiveListener = listener;
	}
	
	public void setOnNegativeClickListener(DialogListener listener) {
		negativeListener = listener;
	}
	
	public void show(Context context) {
		Builder builder = new Builder(context);
		builder.setCancelable(true);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setTitle(question);
		builder.setInverseBackgroundForced(true);
		builder.setPositiveButton(positiveAnswer,
				new DialogListenerWrapper(positiveListener));
		builder.setNegativeButton(negativeAnswer,
				new DialogListenerWrapper(negativeListener));

		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
}
