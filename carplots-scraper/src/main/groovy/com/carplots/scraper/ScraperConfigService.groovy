package com.carplots.scraper

interface ScraperConfigService {
	
	def getApplicationParameter(propertyName) throws ScraperConfigServicePropertyMissing;
	void setApplicationParameter(propertyName, propertyValue)
	
	class ScraperConfigServicePropertyMissing extends Exception {}
}
