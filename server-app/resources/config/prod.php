<?php


// Cache
$app['cache.path'] = __DIR__ . '/../cache';

// Http cache
$app['http_cache.cache_dir'] = $app['cache.path'] . '/http';

// Google server API key
// THIS KEY MAY NOT WORK FOR YOU BECAUSE OF AN IP RESTRICTION!
$app['google.apikey'] = 'AIzaSyC-O5iqlX-7nieoi1ZeRMT8Z-XNEPl0SXc';

// Cache time to life in seconds
$app['cache.ttl'] = 129600; // one and a half days
