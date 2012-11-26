package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import at.fhj.itm10.mobcomp.drivebyreminder.R;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

/**
 * Listview adapter for the edit location activity.
 * 
 * @author Wolfgang Gaar
 */
public class LocationSearchListAdapter extends ArrayAdapter<Location> {

	private List<Location> locations;
	
	public LocationSearchListAdapter(Context context, int textViewResourceId,
			List<Location> objects) {
		super(context, textViewResourceId, objects);
		this.locations = objects;
	}
	

	/**
	 * Retrieves the custom item view.
	 * 
	 * @see http://codehenge.net/blog/2011/05/customizing-android-listview-item-layout/
	 */
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
      
        if (v == null) {
        	LayoutInflater inflater = (LayoutInflater) this.getContext()
        			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	v = inflater.inflate(R.layout.listitem_location, null);
        }

        // Retrieve a specific location
        Location location = locations.get(position);
        if (location != null) {
        	CheckBox chbItemSelected = (CheckBox) v.findViewById(R.id.chbItemSelected);
        	TextView locationName = (TextView) v.findViewById(R.id.lblItemLocationName);
        	TextView locationAddress = (TextView) v.findViewById(R.id.lblItemLocationAddress);

        	chbItemSelected.setChecked(location.isLocationChooserSelected());
        	locationName.setText(location.getName());
        	locationAddress.setText(location.getAddress());
        }

        return v;
    }

}
