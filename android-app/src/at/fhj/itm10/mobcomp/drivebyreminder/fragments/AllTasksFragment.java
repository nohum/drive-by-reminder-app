package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       View v = inflater.inflate(R.layout.fragment_alltasks, container, false);
       
       return v;
    }

    public void reloadViewData() {
    	Cursor usedCursor = dbDao.findAllTasksCursor();
        listAdapter = AllTasksListAdapter.newInstance(getActivity(), 
        		usedCursor);
        setListAdapter(listAdapter);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        reloadViewData();
    }

}
