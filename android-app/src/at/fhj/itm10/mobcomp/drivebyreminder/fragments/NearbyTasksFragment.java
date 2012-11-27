package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

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
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nearbytasks, container, false);
//        View tv = v.findViewById(R.id.text);
//       ((TextView)tv).setText("Fragment #");
       return v;
    }
	
}
