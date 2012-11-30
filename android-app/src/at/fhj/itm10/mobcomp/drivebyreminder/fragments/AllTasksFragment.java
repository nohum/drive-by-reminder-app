package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.MainActivity;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.listadapters.AllTasksListAdapter;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockListFragment {

	private TaskDataDAO dbDao;
	
	private SimpleCursorAdapter listAdapter;

	private Cursor usedCursor;

//	private Cursor usedCursor;

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return AllTasksFragment
	 */
	public static AllTasksFragment newInstance(TaskDataDAO dao) {
		AllTasksFragment fragment = new AllTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setDbDao(dao);

        return fragment;
    }
	
	public void setDbDao(TaskDataDAO dao) {
		this.dbDao = dao;
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
    	Log.d(this.getClass().getSimpleName(), "reloadViewData: dbDao = "
    			+ dbDao);
    	usedCursor = dbDao.findAllTasksCursor();
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

}
