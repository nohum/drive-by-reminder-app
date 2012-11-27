package at.fhj.itm10.mobcomp.drivebyreminder.models;

/**
 * Represents a geo location which also has a name and an address.
 * 
 * @author Wolfgang Gaar
 */
public class Location {

	private int id;

	private int taskId;
	
	private String name;

	private String address;
	
	private double latitude;
	
	private double longitude;
	
	private boolean locationChooserSelected = false;

	public Location(String name, String address, double latitude,
			double longitude) {
		setName(name);
		setAddress(address);
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	public Location() {
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	/**
	 * This property is not going to be stored in database!
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public boolean isLocationChooserSelected() {
		return locationChooserSelected;
	}

	public void setLocationChooserSelected(boolean locationChooserSelected) {
		this.locationChooserSelected = locationChooserSelected;
	}

}
