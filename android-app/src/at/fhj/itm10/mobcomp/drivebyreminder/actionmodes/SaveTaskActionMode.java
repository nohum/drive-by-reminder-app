package at.fhj.itm10.mobcomp.drivebyreminder.actionmodes;

import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Action mode (action bar icons) for add/modify task activities.
 * 
 * @author Wolfgang Gaar
 */
public class SaveTaskActionMode implements Callback {
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        menu.add("Save")
            .setIcon(R.drawable.ic_action_accept)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		mode.setTitle("wtf?");
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		Log.v("SaveTaskActionMode", "clicked item = " + item);
		Log.v("SaveTaskActionMode", "clicked item.itemid = " + item.getItemId());
		
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		Log.v("SaveTaskActionMode", "onDestroyActionMode");
		
		// TODO Auto-generated method stub
		
	}

}
