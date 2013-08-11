package com.carplots.common.interfaces;

/**
 * 
 * @author Google Guice
 * https://code.google.com/p/google-guice/wiki/ModulesShouldBeFastAndSideEffectFree
 */
public interface InitializationService {
	  /**
	   * Starts the service. This method blocks until the service has completely started.
	   */
	  void start() throws Exception;

	  /**
	   * Stops the service. This method blocks until the service has completely shut down.
	   */
	  void stop();
}
