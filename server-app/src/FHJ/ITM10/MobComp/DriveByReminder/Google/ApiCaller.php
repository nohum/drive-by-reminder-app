<?php

namespace FHJ\ITM10\MobComp\DriveByReminder\Google;

/**
 * API interface for interacting with Google's places and geocoder API.
 *
 * @author Wolfgang Gaar
 */
class ApiCaller {

	private $apiKey;

	/**
	 * Create a new api caller.
	 *
	 * @param string $apiKey the Google API key
	 */
	public function __construct($apiKey) {
		$this->apiKey = $apiKey;
	}

	public function retrieveMorePlacesByName($placeName, $location = array(),
			$radius = null) {

	}

	public function retrieveOnePlaceByName($placeName) {

	}

}

?>