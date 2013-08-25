package com.carplots.scraper

import com.carplots.scraper.ScraperConfigService.ScraperConfigServicePropertyMissing
import com.google.inject.Singleton;

import java.util.Properties


@Singleton
class ScraperConfigServiceImpl implements ScraperConfigService {
	
	private static final String scraperConfigPath = System.getProperty("com.carplots.scaper.configFilePath")
	private static final String missingPropertyValue = UUID.randomUUID().toString()
		
	private final Object configLock = new Object()	
	private final def runtimePropertyMap = [:]
	
	private Properties properties
	
	private def get(propertyName) {				
		
		def propertyValue = missingPropertyValue
		synchronized (configLock) {			
			
			if (scraperConfigPath == null) {
				throw new Exception("com.carplots.scaper.configFilePath system variable must point to scraper properties file.")	
			}			
			if (properties == null) {
				properties = new Properties()
				properties.load((new File(scraperConfigPath)).newInputStream())				
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
			throw new ScraperConfigServicePropertyMissing("Config property missing '${propertyName}'")
		}
		return propVal
	}

	@Override
	public void setApplicationParameter(propertyName, propertyValue) {
		synchronized (configLock) {
			runtimePropertyMap[propertyName] = propertyValue
		}				
	}
	
	
}
