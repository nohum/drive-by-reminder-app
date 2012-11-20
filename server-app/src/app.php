<?php

use Silex\Provider\HttpCacheServiceProvider;
use Silex\Provider\MonologServiceProvider;

use FHJ\ITM10\MobComp\DriveByReminder\ControllerResolver;

$app->register(new HttpCacheServiceProvider());

$app->register(new MonologServiceProvider(), array(
    'monolog.logfile' => __DIR__.'/../resources/log/app.log',
    'monolog.name'    => 'app',
    'monolog.level'   => 300 // = Logger::WARNING
));

$app['positioncoding.controller'] = $app->share(function() {
	return new PositionCodingController($app);
});

// Override the default resolver
$app['resolver'] = $app->share(function () use ($app) {
	return new ControllerResolver($app, $app['logger']);
});

return $app;
