<?php

use Symfony\Component\HttpFoundation\Response;

// All supplied parameters are GET parameters - because of this we are
// able to use the http caching provided by the framework reliably.

$app->get('/', function() {
	return new Response('');
});

$app->get('/service/one/byname/{name}/inlanguage/{langcode}/inregion/{regioncode}',
		'positioncoding.controller:oneLocationByNameAction');

$app->get('/service/more/byname/{name}/inlanguage/{langcode}/inregion/{regioncode}',
		'positioncoding.controller:moreLocationsByNameAction');

$app->get('/service/one/bylocation/{latitude}/{longitude}/inlanguage/{langcode}/inregion/{regioncode}',
		'positioncoding.controller:oneNameByLocationAction');

$app->error(function (\Exception $e, $code) use ($app) {
	// If this is debug mode, don't catch the error.
	// Because of this we'll get an enhanced stracktrace output
	// by using the Silex built-in error handler.
    if ($app['debug']) {
        return;
    }

    $result = new \stdClass();
	$result->code = $code;
	$result->status = 'ERROR';

	// $app->json() would also be a possibilty, but it would not be
	// possible to set a response code so easy as with this statement.
    return new Response(json_encode($result), $code);
});

return $app;
