package at.fhj.itm10.mobcomp.drivebyreminder.helper;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContextSingleton;

/**
 * Used to store data.
 * 
 * @author Wolfgang Gaar
 */
@ContextSingleton
public class DataSingletonStorage {

	private Map<String, Object> data = new HashMap<String, Object>();
	
	public void setData(String key, Object data) {
		this.data.put(key, data);
	}
	
	public Object getData(String key) {
		return this.data.get(key);
	}
	
}
