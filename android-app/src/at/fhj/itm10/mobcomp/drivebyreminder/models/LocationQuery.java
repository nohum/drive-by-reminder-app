package at.fhj.itm10.mobcomp.drivebyreminder.models;

/**
 * Location download query data.
 * 
 * @author Wolfgang Gaar
 */
public class LocationQuery {
	private String regionCode;
	
	private String languageCode;
	
	private String locationName;

	public LocationQuery() {
		
	}
	
	public LocationQuery(String locationName, String regionCode,
			String languageCode) {
		setLocationName(locationName);
		setRegionCode(regionCode);
		setLanguageCode(languageCode);
	}
	
	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
}