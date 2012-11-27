package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockListFragment
		implements LoaderCallbacks<Cursor> {

	private TaskDataDAO dbDao;
	
	private SimpleCursorAdapter listAdapter;

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
    	getLoaderManager().restartLoader(1, null, this);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listAdapter = new SimpleCursorAdapter(getActivity(),
        		R.layout.listitem_task, null,
        		new String[] { "title", "description" },
        		new int[] { R.id.lblTaskTitle, R.id.lblTaskDescription }, 0);
        setListAdapter(listAdapter);
        
        getLoaderManager().initLoader(1, null, this);
    }

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//		return new CursorL
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		listAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		listAdapter.swapCursor(null);
	}
	
}
