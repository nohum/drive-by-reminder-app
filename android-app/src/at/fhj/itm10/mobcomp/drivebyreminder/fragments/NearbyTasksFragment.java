package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import roboguice.inject.ContentView;
import android.os.Bundle;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

/**
 * Fragment for the nearby tasks view.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.fragment_home)
public class NearbyTasksFragment extends RoboSherlockFragment {

	public static NearbyTasksFragment newInstance() {
		NearbyTasksFragment fragment = new NearbyTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
	
}
