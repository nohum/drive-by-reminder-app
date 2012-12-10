package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

/**
 * Listview adapter for the edit location activity.
 * 
 * @author Wolfgang Gaar
 */
public class LocationSearchListAdapter extends ArrayAdapter<Location>
		implements OnClickListener {

	private List<Location> locations;
	
	private LayoutInflater inflater;
	
	/**
	 * Create a list view adapter.
	 * 
	 * @param context the context
	 * @param textViewResourceId the view resource
	 * @param objects locations to display
	 */
	public LocationSearchListAdapter(Context context, int textViewResourceId,
			List<Location> objects) {
		super(context, textViewResourceId, objects);
		this.locations = objects;
		
		inflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * Retrieves the custom item view.
	 * 
	 * @param position list position
	 * @param convertView the view
	 * @param parent parent
	 * @see http://codehenge.net/blog/2011/05/customizing-android-listview-item-layout/
	 */
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
        	v = inflater.inflate(R.layout.listitem_location, null);
        }

        // Retrieve a specific location
        Location location = locations.get(position);
        Log.v(getClass().getSimpleName(), "getView: position = " + position
        		+ ", location = " + location);

        if (location != null) {
        	CheckedTextView locationName = (CheckedTextView) v.findViewById(
        			android.R.id.text1);
        	TextView locationAddress = (TextView) v.findViewById(
        			R.id.lblItemLocationAddress);

        	locationName.setText(location.getName());
        	locationName.setTag(location);
        	locationName.setChecked(location.isLocationChooserSelected());

        	locationAddress.setText(location.getAddress());

        	v.setOnClickListener(this);
        }

        return v;
    }

	@Override
	public void onClick(View v) {
		CheckedTextView ctv = (CheckedTextView) v.findViewById(
				android.R.id.text1);
		ctv.toggle();

		Location location = (Location) ctv.getTag();
		location.setLocationChooserSelected(ctv.isChecked());

		Log.v("LocationSearchListAdapter", "location state in tag = "
				+ ((Location) ctv.getTag()));
	}

}
