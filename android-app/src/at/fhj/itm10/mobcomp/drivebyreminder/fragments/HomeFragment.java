package at.fhj.itm10.mobcomp.drivebyreminder.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import at.fhj.itm10.mobcomp.drivebyreminder.R;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

/**
 * Fragment for the home view.
 * 
 * @author Wolfgang Gaar
 */
public class HomeFragment extends RoboSherlockFragment {

	/**
	 * Get an instance of this fragment.
	 * 
	 * @return HomeFragment
	 */
	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
	
}
