package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.MainActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.ModifyTaskActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.TasksListAdapter;

import com.actionbarsherlock.view.ActionMode;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockListFragment
		implements OnItemClickListener {

	protected TaskDataDAO dbDao;
	
	private MainFragmentPagerAdapter pagerAdapter;
	
	private SimpleCursorAdapter listAdapter;

	protected Cursor usedCursor;
	
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
       return inflater.inflate(R.layout.fragment_alltasks, container, false);
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);

    	getListView().setOnItemClickListener(this);
    	getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    	getListView().setItemsCanFocus(false);
    }
    
    @Override
	public void onDestroy() {
    	Log.v(getClass().getSimpleName(), "------------ ONDESTROY BEFORE");
    	
    	if (usedCursor != null) {
    		usedCursor.close();
    	}
    	
    	super.onDestroy();
    }

    public void reloadViewData() {
    	usedCursor = dbDao.findAllTasksForFragmentCursor();
    	
    	// Sometimes the context is null...
    	Context context = getActivity();
    	if (context == null) {
    		context = getSherlockActivity();
    	}
    	
        listAdapter = TasksListAdapter.newInstance(context, 
        		usedCursor);

        setListAdapter(listAdapter);
    }
    
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
				reloadViewData();
//				pagerAdapter.refreshFragments();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				Log.d("MainActivity", "ModifyTaskActivity result = RESULT_CANCELED");
			}
		default:
			break;
		}
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

}
