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

	/**
	 * Create a location query.
	 */
	public LocationQuery() {

	}

	/**
	 * Create a location query.
	 *
	 * @param locationName location search query
	 * @param regionCode two-letter region code
	 * @param languageCode two-letter lang name
	 */
	public LocationQuery(final String locationName, final String regionCode,
			final String languageCode) {
		setLocationName(locationName);
		setRegionCode(regionCode);
		setLanguageCode(languageCode);
	}

	public String getRegionCode() {
		return regionCode;
	}

	public void setRegionCode(final String regionCode) {
		this.regionCode = regionCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(final String locationName) {
		this.locationName = locationName;
	}
}