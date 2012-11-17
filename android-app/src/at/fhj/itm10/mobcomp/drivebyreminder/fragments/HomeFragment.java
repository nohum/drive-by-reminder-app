package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import roboguice.inject.ContentView;
import android.os.Bundle;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

/**
 * Fragment for the home view.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends RoboSherlockFragment {

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
	
}
