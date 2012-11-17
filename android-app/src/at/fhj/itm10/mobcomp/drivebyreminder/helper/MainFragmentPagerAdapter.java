package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.AllTasksFragment;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.HomeFragment;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.NearbyTasksFragment;

/**
 * Fragment helper adapter for main activity.
 * 
 * @author Wolfgang Gaar
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int number) {
		Fragment fragment = null;
		
		switch(number) {
		case 0:
			fragment = HomeFragment.newInstance();
			break;
		case 1:
			fragment = AllTasksFragment.newInstance();
			break;
		case 2:
			fragment = NearbyTasksFragment.newInstance();
			break;
		default:
			throw new IllegalStateException("number higher than 2");
		} 
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

}
