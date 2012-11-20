<?php

namespace FHJ\ITM10\MobComp\DriveByReminder\Controller;

use FHJ\ITM10\MobComp\DriveByReminder\Google\ApiCaller;

use Silex\Application;

use Symfony\Component\HttpFoundation\Response;

/**
 * Provides position coding services.
 *
 * @author Wolfgang Gaar
 */
class PositionCodingController {

	private $app;

	public function __construct(Application $app) {
		$this->app = $app;
	}

	/**
	 * Retrieve location data for a location name, returns one set.
	 *
	 * @param string $name
	 * @param string $langcode two char language identifier
	 * @param string $regioncode ccTLD name
	 */
	public function oneLocationByNameAction($name, $langcode, $regioncode) {
		if (empty($name) || empty($langcode)) {
			$this->app->abort(400);
		}

		if (strlen($langcode) <> 2) {
			$this->app->abort(400);
		}

		if (strlen($regioncode) <> 2) {
			$this->app->abort(400);
		}

		try {
			$webservice = new ApiCaller($this->app['google.apikey']);
			$webservice->setLanguage($langcode);
			$webservice->setRegion($regioncode);
			$result = $webservice->retrieveOnePlaceByName($name);

			return $this->returnSuccessResponse(array($result));
		} catch (\Exception $e) {
			return $this->returnError($e->getMessage());
		}
	}

	/**
	 * Get one location and its data by latitude and longitude.
	 *
	 * @param double $latitude
	 * @param double $longitude
	 * @param string $langcode two char language identifier
	 * @param string $regioncode ccTLD name
	 */
	public function oneNameByLocationAction($latitude, $longitude, $langcode,
			$regioncode) {
		if (!floatval($latitude) || !floatval($longitude)) {
			$this->app->abort(400);
		}

		if (strlen($langcode) <> 2) {
			$this->app->abort(400);
		}

		if (strlen($regioncode) <> 2) {
			$this->app->abort(400);
		}

		try {
			$webservice = new ApiCaller($this->app['google.apikey']);
			$webservice->setLanguage($langcode);
			$webservice->setRegion($regioncode);
			$result = $webservice->retrieveOneNameByPlace(array($latitude,
					$longitude));

			return $this->returnSuccessResponse(array($result));
		} catch (\Exception $e) {
			return $this->returnError($e->getMessage());
		}
	}

	/**
	 * Retrieve location data for a location name, returns multiple matches.
	 *
	 * @param string $name
	 * @param string $langcode two char language identifier
	 * @param string $regioncode ccTLD name
	 */
	public function moreLocationsByNameAction($name, $langcode, $regioncode) {
		if (empty($name) || empty($langcode)) {
			$this->app->abort(400);
		}

		if (strlen($langcode) <> 2) {
			$this->app->abort(400);
		}

		if (strlen($regioncode) <> 2) {
			$this->app->abort(400);
		}

		try {
			$webservice = new ApiCaller($this->app['google.apikey']);
			$webservice->setLanguage($langcode);
			$webservice->setRegion($regioncode);
			$resultArray = $webservice->retrieveMorePlacesByName($name);

			return $this->returnSuccessResponse($resultArray);
		} catch (\Exception $e) {
			return $this->returnError($e->getMessage());
		}
	}

	private function returnError($message) {
		$error = new \stdClass();
		$error->code = 500;
		$error->status = 'ERROR';
// 		$error->message = $message;

		$this->app['monolog']->err($message);
		return new Response(json_encode($error), $error->code);
	}

	private function returnSuccessResponse(array $fetchedEntries) {
		$jsonResult = new \stdClass();
		$jsonResult->code = 200;
		$jsonResult->status = 'OK';
		$jsonResult->entries = $fetchedEntries;

		$response = new Response(json_encode($jsonResult), $jsonResult->code);
		$response->setTtl($this->app['cache.ttl']);

		return $response;
	}
}

?>