package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;

/**
 * Used to store data.
 * 
 * @author Wolfgang Gaar
 */
@Singleton
public class DataSingletonStorage {

	private Map<String, Object> data = new HashMap<String, Object>();
	
	/**
	 * Save data to the storage using key.
	 * 
	 * @param key unique key
	 * @param value object data
	 */
	public void setData(String key, Object value) {
		data.put(key, value);
	}
	
	/**
	 * Get data from the storage.
	 * 
	 * @param key unique key
	 * @return Object
	 */
	public Object getData(String key) {
		return data.get(key);
	}
	
}
