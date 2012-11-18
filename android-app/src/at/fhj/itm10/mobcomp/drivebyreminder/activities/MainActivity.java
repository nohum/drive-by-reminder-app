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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.helper.MainFragmentPagerAdapter;

import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockListFragment;

/**
 * Main view.
 * 
 * @author Wolfgang Gaar
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends RoboSherlockFragmentActivity
		implements OnNavigationListener {
	
	@InjectView(R.id.lblNavigationList)
	private TextView lblNavigationSelected;
	
	@InjectView(R.id.pgrMainView)
	private ViewPager pagerMainView;
    
	@InjectResource(R.string.menu_add)
    private String stringAdd;
    
	@InjectResource(R.string.menu_settings)
    private String stringSettings;

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
        
        pagerAdapter = new MainFragmentPagerAdapter(
        		getSupportFragmentManager());
        pagerMainView.setAdapter(pagerAdapter);
	}

    public static class ArrayListFragment extends RoboSherlockListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        public static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
            View tv = v.findViewById(R.id.text);
           ((TextView)tv).setText("Fragment #" + mNum);
           return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, R.array.locations));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
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
            	// TODO: switch off activity stacking for this activity
            	this.startActivity(new Intent(this, AddTaskActivity.class));
            	return true;
            case R.id.menu_main_settings:
            	this.startActivity(new Intent(this, SettingsActivity.class));
            	return true;
        }

        return super.onOptionsItemSelected(item);
    }

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}
	

}
