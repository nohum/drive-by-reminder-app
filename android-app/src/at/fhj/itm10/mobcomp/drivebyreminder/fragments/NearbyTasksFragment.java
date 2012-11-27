package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;

/**
 * Fragment for the nearby tasks view.
 * 
 * @author Wolfgang Gaar
 */
public class NearbyTasksFragment extends AllTasksFragment {

	private TaskDataDAO dbDao;
	
	/**
	 * Get an instance of this fragment.
	 * 
	 * @return NearbyTasksFragment
	 */
	public static NearbyTasksFragment newInstance(TaskDataDAO dao) {
		NearbyTasksFragment fragment = new NearbyTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.setDbDao(dao);

        return fragment;
    }

	public void setDbDao(TaskDataDAO dao) {
		this.dbDao = dao;
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nearbytasks, container,
        		false);
//        View tv = v.findViewById(R.id.text);
//       ((TextView)tv).setText("Fragment #");
       return v;
    }
	
}
