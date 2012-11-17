package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import roboguice.inject.ContentView;
import android.os.Bundle;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

/**
 * Fragment to view all tasks.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.fragment_home)
public class AllTasksFragment extends RoboSherlockFragment {

	public static AllTasksFragment newInstance() {
		AllTasksFragment fragment = new AllTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
	
}
