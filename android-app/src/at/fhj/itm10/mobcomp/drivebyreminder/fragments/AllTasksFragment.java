package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.MainActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.ModifyTaskActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.AllTasksListAdapter;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.ActionMode.Callback;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockListFragment
		implements OnItemLongClickListener, OnItemClickListener {

	private TaskDataDAO dbDao;
	
	private MainFragmentPagerAdapter pagerAdapter;
	
	private SimpleCursorAdapter listAdapter;

	private Cursor usedCursor;
	
	private ActionMode actionMode;

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return AllTasksFragment
	 */
	public static AllTasksFragment newInstance(TaskDataDAO dao,
			MainFragmentPagerAdapter pagerAdapter) {
		AllTasksFragment fragment = new AllTasksFragment();

        fragment.setDbDao(dao);
        fragment.setPagerAdapter(pagerAdapter);

        return fragment;
    }
	
	public void setDbDao(TaskDataDAO dao) {
		this.dbDao = dao;
	}

	public void setPagerAdapter(MainFragmentPagerAdapter pagerAdapter) {
		this.pagerAdapter = pagerAdapter;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (dbDao == null) {
        	dbDao = ((MainActivity) getActivity()).getDao();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_alltasks, container, false);
       
       return v;
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);

    	getListView().setOnItemLongClickListener(this);
    	getListView().setOnItemClickListener(this);
    	getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	getListView().setItemsCanFocus(false);
    }
    
    @Override
	public void onDestroy() {
    	if (usedCursor != null) {
    		usedCursor.close();
    		usedCursor = null;
    	}
    	
    	super.onDestroy();
    }

    public void reloadViewData() {
    	usedCursor = dbDao.findAllTasksForFragmentCursor();
    	Log.d(this.getClass().getSimpleName(), "reloadViewData: usedCursor = "
    			+ usedCursor);
    	
        listAdapter = AllTasksListAdapter.newInstance(getActivity(), 
        		usedCursor);
        setListAdapter(listAdapter);
    }
    
//    public void finishActionModes() {
//    	if (actionMode != null) {
//    		actionMode.finish();
//    	}
//    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        if (dbDao == null) {
        	dbDao = ((MainActivity) getActivity()).getDao();
        }
        
        reloadViewData();
    }
    
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) {
		case 200: // Process modify task activity
			if (resultCode == Activity.RESULT_OK) {
				Log.d("MainActivity", "ModifyTaskActivity result = RESULT_OK");
				pagerAdapter.refreshFragments();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.d("MainActivity", "ModifyTaskActivity result = RESULT_CANCELED");
			}
		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
			long id) {
		// If in action mode, skip this to prevent re-entering the action mode
		if (actionMode != null) {
			return false;
		}
		
		long taskId = (Long) v.getTag();
    	if (taskId > 0) {
    		// We have to use the sherlock action mode starter to maintain
    		// compatibility
    		actionMode = ((MainActivity) getActivity())
    				.startActionMode(new ModifyTaskListActionMode(getListView()));
    		((MainActivity) getActivity()).setActionMode(actionMode);
    		v.setSelected(true);

    		return true;
    	}
		
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		if (actionMode != null) {
			v.setSelected(true);
			return;
		}
		
		long taskId = (Long) v.getTag();
    	if (taskId > 0) {
    		Intent edit = new Intent(getActivity(), ModifyTaskActivity.class);
    		edit.putExtra("taskId", taskId);
    		this.startActivityForResult(edit, 200);
    	}
	}
	
	/**
	 * Action mode (action bar icons) for add/modify task activities.
	 *
	 * @author Wolfgang Gaar
	 */
	public class ModifyTaskListActionMode implements Callback {

		private ListView listView;
		
		public ModifyTaskListActionMode(ListView lv) {
			this.listView = lv;
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			getSherlockActivity().getSupportMenuInflater()
					.inflate(R.menu.menu_cab_alltasks, menu);

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
			Log.v("ModifyTaskListActionMode", "clicked item = " + item);

            long[] selected = listView.getCheckedItemIds();
            if (selected.length > 0) {
                for (long id : selected) {
                	Log.v("ModifyTaskListActionMode", "selected id = " + id);
                }
            }

            mode.finish();
            return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			Log.v("ModifyTaskListActionMode", "onDestroyActionMode");

			// Destroying action mode, let's unselect all items
            for (int i = 0; i < listView.getAdapter().getCount(); i++)
            	listView.setItemChecked(i, false);
 
            if (mode == actionMode) {
            	actionMode = null;
            }
		}

	}
}
