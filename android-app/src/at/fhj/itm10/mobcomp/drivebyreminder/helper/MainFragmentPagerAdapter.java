package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.activities.MainActivity.ArrayListFragment;

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
		Log.v("MainFragmentPagerAdapter", "getItem number = " + number);
		Fragment fragment = null;
		
		switch(number) {
		case 0:
			fragment = ArrayListFragment.newInstance(number);
			break;
		case 1:
			fragment = ArrayListFragment.newInstance(number);
			break;
		case 2:
			fragment = ArrayListFragment.newInstance(number);
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
