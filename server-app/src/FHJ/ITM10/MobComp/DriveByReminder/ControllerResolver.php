<?php

namespace FHJ\ITM10\MobComp\DriveByReminder;

use Silex\ControllerResolver as BaseControllerResolver;

/**
 * Controller resolver to find controllers.
 *
 * @author Wolfgang Gaar
 * @see http://davedevelopment.co.uk/2012/10/03/Silex-Controllers-As-Services.html
 */
class ControllerResolver extends BaseControllerResolver {

	protected function createController($controller) {
		if (false !== strpos($controller, '::')) {
			return parent::createController($controller);
		}

		if (false === strpos($controller, ':')) {
			throw new \LogicException(sprintf(
					'Unable to parse the controller name "%s".', $controller));
		}

		list($service, $method) = explode(':', $controller, 2);

		if (!isset($this->app[$service])) {
			throw new \InvalidArgumentException(sprintf(
					'Service "%s" does not exist.', $controller));
		}

		return array($this->app[$service], $method);
	}
}

?>