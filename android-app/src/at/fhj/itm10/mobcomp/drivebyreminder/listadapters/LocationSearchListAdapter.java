package at.fhj.itm10.mobcomp.drivebyreminder.listadapters;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;
import at.fhj.itm10.mobcomp.drivebyreminder.models.Location;

/**
 * Listview adapter for the edit location activity.
 * 
 * @author Wolfgang Gaar
 */
public class LocationSearchListAdapter extends SimpleAdapter {

	public LocationSearchListAdapter(List<Location> locations) {
		super(null, null, (Integer) 0, null, null);
	}
	
	public LocationSearchListAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
	}

}
