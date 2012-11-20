<?php

namespace FHJ\ITM10\MobComp\DriveByReminder\Google;

use Monolog\Logger;

/**
 * API interface for interacting with Google's places and geocoder API.
 *
 * @author Wolfgang Gaar
 */
class ApiCaller {

	private $apiKey;

	private $language;

	private $region;

	private $logger;

	private $geocoderUrl = 'http://maps.googleapis.com/maps/api/geocode/%s?%s';

	private $placesUrl = 'http://maps.googleapis.com/maps/api/place/textsearch/%s?%s';

	/**
	 * Create a new api caller.
	 *
	 * @param string $apiKey the Google API key
	 */
	public function __construct(Logger $logger, $apiKey) {
		$this->logger = $logger;
		$this->apiKey = $apiKey;
	}

	public function retrieveMorePlacesByName($placeName) {
		// region code not needed

		$result = $this->callWebservice($this->placesUrl, array(
			'query' => $placeName,
			'language' => $this->language,
			'sensor' => 'false',
			'key' => $this->apiKey
		));

		return $this->formatJson($result);
	}

	public function retrieveOnePlaceByName($placeName) {
		$result = $this->callWebservice($this->geocoderUrl, array(
			'address' => $placeName,
			'language' => $this->language,
			'region' => $this->region,
			'sensor' => 'false'
		));

		return $this->formatJson($result);
	}

	/**
	 * @param array $geolocation latitude and longitude (in this order)
	 */
	public function retrieveOneNameByPlace(array $geolocation) {
		$result = $this->callWebservice($this->geocoderUrl, array(
			'latlng' => $geolocation[0] . ',' . $geolocation[1],
			'language' => $this->language,
			'region' => $this->region,
			'sensor' => 'false'
		));

		return $this->formatJson($result);
	}

	private function callWebservice($url, array $parameters) {
		$callUrl = sprintf($url, 'json', http_build_query($parameters, '', '&'));
		$this->logger->info('Calling url: ' . $callUrl);
		$data = file_get_contents($callUrl);

		if ($data === false) {
			return false;
		}

		$json = json_decode($data);
		if (json_last_error() <> JSON_ERROR_NONE) {
			throw new \Exception("json error: " . json_last_error());
		}

		return $json;
	}

	private function formatJson(\stdClass $json) {
		if ($json->status == 'ZERO_RESULTS') {
			return array();
		} else if ($json->status != 'OK') {
			throw new \Exception('maps api returned status: ' . $json->status);
		}

		$resultList = array();
		foreach ($json->results as $result) {
			$resultList[] = array(
				'address' => $result->formatted_address,
				'latitude' => $result->geometry->location->lat,
				'longitude' => $result->geometry->location->lng,
			);
		}

		return $resultList;
	}

	public function getApiKey() {
		return $this->apiKey;
	}

	public function getLanguage() {
		return $this->language;
	}

	public function setLanguage($language) {
		$this->language = $language;
	}

	public function getRegion() {
		return $this->region;
	}

	public function setRegion($region) {
		$this->region = $region;
	}
}

?>