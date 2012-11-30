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
import android.widget.ListView;
import android.widget.Toast;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.MainActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.ModifyTaskActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.AllTasksListAdapter;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockListFragment {

	private TaskDataDAO dbDao;
	
	private MainFragmentPagerAdapter pagerAdapter;
	
	private SimpleCursorAdapter listAdapter;

	private Cursor usedCursor;

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return AllTasksFragment
	 */
	public static AllTasksFragment newInstance(TaskDataDAO dao,
			MainFragmentPagerAdapter pagerAdapter) {
		AllTasksFragment fragment = new AllTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
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
	public void onDestroy() {
    	if (usedCursor != null) {
    		usedCursor.close();
    		usedCursor = null;
    	}
    	
    	super.onDestroy();
    }

    public void reloadViewData() {
    	Log.d(this.getClass().getSimpleName(), "reloadViewData");
    	usedCursor = dbDao.findAllTasksForFragmentCursor();
    	Log.d(this.getClass().getSimpleName(), "reloadViewData: usedCursor = "
    			+ usedCursor);
    	
        listAdapter = AllTasksListAdapter.newInstance(getActivity(), 
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
    public void onListItemClick(ListView l, View v, int position, long id) {
    	long taskId = (Long) v.getTag();

    	if (taskId > 0) {
    		Intent edit = new Intent(getActivity(), ModifyTaskActivity.class);
    		edit.putExtra("taskId", taskId);
    		this.startActivityForResult(edit, 200);
    	}

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
}
