package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.TaskDataDAO;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockListFragment {

	private TaskDataDAO dbDao;

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
//        View tv = v.findViewById(R.id.text);
//       ((TextView)tv).setText("Fragment #");
       return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        setListAdapter(new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, R.array.locations));
    }
	
}
