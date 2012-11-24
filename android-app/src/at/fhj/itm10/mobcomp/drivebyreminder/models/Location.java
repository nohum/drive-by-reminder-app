package at.fhj.itm10.mobcomp.drivebyreminder.models;

/**
 * Represents a geo location which also has a name.
 * 
 * @author Wolfgang Gaar
 */
public class Location {

	private int id;

	private int taskId;
	
	private String name;
	
	private double latitude;
	
	private double longitude;

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

}
