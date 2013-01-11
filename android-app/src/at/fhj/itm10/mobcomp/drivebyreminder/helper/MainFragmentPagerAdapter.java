package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import at.fhj.itm10.mobcomp.drivebyreminder.database.TaskDataDAO;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.AllTasksFragment;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.HomeFragment;
import at.fhj.itm10.mobcomp.drivebyreminder.fragments.NearbyTasksFragment;

/**
 * Fragment helper adapter for main activity.
 * 
 * @author Wolfgang Gaar
 */
public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private TaskDataDAO dbDao;
	
	private AllTasksFragment allTasks;
	
	private NearbyTasksFragment nearbyTasks;
	
	public static final int ALL_TASKS_FRAGMENT = 1;
	
	public static final int NEARBY_TASKS_FRAGMENT = 2;
	
	public MainFragmentPagerAdapter(FragmentManager fm, TaskDataDAO dao) {
		super(fm);
		this.dbDao = dao;
		
		Log.v(getClass().getSimpleName(),
				"------------ CONSTRUCTOR MainFragmentPagerAdapter");
	}

	@Override
	public Fragment getItem(int number) {
		switch(number) {
		case 0:
			return HomeFragment.newInstance();
		case ALL_TASKS_FRAGMENT:
//			if (allTasks == null) {
				allTasks = AllTasksFragment.newInstance(dbDao, this);
//			}
			Log.v(getClass().getSimpleName(), "------------ GETITEM AllTasksFragment = "
					+ allTasks);
			return allTasks;
		case 2:
			if (nearbyTasks == null) {
				nearbyTasks = NearbyTasksFragment.newInstance(dbDao, this);
			}

			Log.v(getClass().getSimpleName(), "------------ GETITEM NearbyTasksFragment = "
					+ allTasks);
			return nearbyTasks;
		default:
			throw new IllegalStateException("number higher than 2");
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

	public void refreshFragments() {
		nullChecks();
		
		allTasks.reloadViewData();
		nearbyTasks.reloadViewData();
	}

	private void nullChecks() {
		if (allTasks == null) {
			getItem(ALL_TASKS_FRAGMENT);
		}
		
		if (nearbyTasks == null) {
			getItem(NEARBY_TASKS_FRAGMENT);
		}
	}
	
}
