package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

/**
 * Listview adapter for the edit location activity.
 * 
 * @author Wolfgang Gaar
 */
public class LocationSearchListAdapter extends ArrayAdapter<Location> {

	public LocationSearchListAdapter(Context context, int textViewResourceId,
			List<Location> objects) {
		super(context, textViewResourceId, objects);

	}
	
	

}
