package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import roboguice.inject.ContentView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.TaskStorageHelper;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
public class AllTasksFragment extends RoboSherlockFragment {

//	@Inject
//	private TaskStorageHelper taskDb;

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return AllTasksFragment
	 */
	public static AllTasksFragment newInstance() {
		AllTasksFragment fragment = new AllTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
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
