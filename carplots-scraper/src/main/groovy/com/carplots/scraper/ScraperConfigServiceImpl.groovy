package com.carplots.scraper

import com.carplots.common.exception.ApplicationConfigurationException;
import com.carplots.scraper.ScraperConfigService.ScraperConfigServicePropertyMissing
import com.google.inject.Singleton;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties


@Singleton
class ScraperConfigServiceImpl implements ScraperConfigService {
	
	private static final String CONFIGURATION_FILE = 'scraper.properties'
	private static final String missingPropertyValue = UUID.randomUUID().toString()
	private static Properties properties
		
	private final Object configLock = new Object()	
	private final def runtimePropertyMap = [:]
	
	
	
	private def get(propertyName) {				
		
		def propertyValue = missingPropertyValue
		synchronized (configLock) {						
			if (properties == null) {				
				loadProperties()	
			}			
			if (runtimePropertyMap.containsKey(propertyName)) {
				propertyValue = runtimePropertyMap[propertyName]
			}
			else {
				propertyValue = properties.getProperty(propertyName, missingPropertyValue)
			}
		}
		
		return propertyValue
	}
	
	@Override
	def getApplicationParameter(propertyName) throws ScraperConfigServicePropertyMissing {
		def propVal = null
		if ((propVal = get(propertyName)).equals(missingPropertyValue)) {
			throw new ScraperConfigServicePropertyMissing("Config property missing '${propertyName}'" as String)
		}
		return propVal
	}

	@Override
	public void setApplicationParameter(propertyName, propertyValue) {
		synchronized (configLock) {
			runtimePropertyMap[propertyName] = propertyValue
		}				
	}
	
	//copied from simple logger
	private static void loadProperties() {	
		InputStream input = (InputStream) AccessController.doPrivileged(
			new PrivilegedAction() {
			  public Object run() {
				ClassLoader threadCL = Thread.currentThread().getContextClassLoader()
				if (threadCL != null) {
				  return threadCL.getResourceAsStream(CONFIGURATION_FILE)
				} else {
				  return ClassLoader.getSystemResourceAsStream(CONFIGURATION_FILE)
				}
			  }
			})	
		if (input != null) {
			properties = new Properties()
			properties.load(input)
		}
		else {
			throw new ApplicationConfigurationException('Could not load scraper.properties from config folder.')
		}		
	}
}
