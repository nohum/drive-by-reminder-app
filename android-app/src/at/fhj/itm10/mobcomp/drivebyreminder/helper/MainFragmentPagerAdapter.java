package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
	
	public MainFragmentPagerAdapter(FragmentManager fm, TaskDataDAO dao) {
		super(fm);
		this.dbDao = dao;
	}

	@Override
	public Fragment getItem(int number) {
		switch(number) {
		case 0:
			return HomeFragment.newInstance();
		case 1:
			allTasks = AllTasksFragment.newInstance(dbDao, this);
			return allTasks;
		case 2:
			nearbyTasks = NearbyTasksFragment.newInstance(dbDao, this);
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
			getItem(1);
		}
		
		if (nearbyTasks == null) {
			getItem(2);
		}
	}
	
}
