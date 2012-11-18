package at.fhj.itm10.mobcomp.drivebyreminder.activities;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TabWidget;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

/**
 * Main view.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockFragmentActivity
		implements OnNavigationListener, OnPageChangeListener {
	
	@InjectView(R.id.lblNavigationList)
	private TextView lblNavigationSelected;
	
	@InjectView(R.id.pgrMainView)
	private ViewPager pagerMainView;

	private MainFragmentPagerAdapter pagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Context context = getSupportActionBar().getThemedContext();
        // Set content of nav dropdown
        ArrayAdapter<CharSequence> list = ArrayAdapter.createFromResource(context,
        		R.array.locations, R.layout.sherlock_spinner_item);
        list.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        
        // Pager class which enables us to have several fragments loaded and "pagable"
        pagerAdapter = new MainFragmentPagerAdapter(
        		getSupportFragmentManager());
        pagerMainView.setAdapter(pagerAdapter);
        pagerMainView.setOnPageChangeListener(this);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            NavUtils.navigateUpFromSameTask(this);
            return true;
        case R.id.menu_main_add:
        	Intent intent = new Intent(this, AddTaskActivity.class);
        	// There should only be one "Add new" activity open at any time
        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        	this.startActivity(intent);
        	return true;
        case R.id.menu_main_settings:
        	this.startActivity(new Intent(this, SettingsActivity.class));
        	return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Used for the top-left navigation menu.
     */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// Change the focused fragment of the pager if the navigation list is changed
		pagerMainView.setCurrentItem(itemPosition, true);

		return true;
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// Empty by intention
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// Empty by intention	
	}

	/**
	 * Called after a fragment has been selected ("scrolled to") by the user.
	 */
	@Override
	public void onPageSelected(int position) {
		// Change the focused navigation list item if the viewed fragment has been changed
		getSupportActionBar().setSelectedNavigationItem(position);
	}

}
