package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.os.Bundle;

/**
 * Fragment for the nearby tasks view.
 * 
 * @author Wolfgang Gaar
 */
public class NearbyTasksFragment extends AllTasksFragment {

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return NearbyTasksFragment
	 */
	public static NearbyTasksFragment newInstance() {
		NearbyTasksFragment fragment = new NearbyTasksFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
	
}
