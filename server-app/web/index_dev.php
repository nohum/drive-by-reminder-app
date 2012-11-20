<?php

// Only allow local access to dev front controller
if (isset($_SERVER['HTTP_CLIENT_IP'])
	|| isset($_SERVER['HTTP_X_FORWARDED_FOR'])
	|| !in_array(@$_SERVER['REMOTE_ADDR'], array(
	        '127.0.0.1',
	        '::1',
))) {
	header('HTTP/1.0 403 Forbidden');
}

ini_set('display_errors', 1);
error_reporting(-1);

require_once __DIR__.'/../vendor/autoload.php';

$app = new Silex\Application();

require __DIR__.'/../resources/config/dev.php';
require __DIR__.'/../src/app.php';

require __DIR__.'/../src/controllers.php';

$app->run();
