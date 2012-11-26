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
	
	public void setData(String key, Object data) {
		this.data.put(key, data);
	}
	
	public Object getData(String key) {
		return this.data.get(key);
	}
	
}
