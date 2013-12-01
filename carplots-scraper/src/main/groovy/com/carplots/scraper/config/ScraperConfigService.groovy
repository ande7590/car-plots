package com.carplots.scraper.config

interface ScraperConfigService {
	
	def getApplicationParameter(propertyName) throws ScraperConfigServicePropertyMissing;
	void setApplicationParameter(propertyName, propertyValue)
	
	public static class ScraperConfigServicePropertyMissing extends Exception {
		public ScraperConfigServicePropertyMissing(String message) {
			super(message)			
		}
		
	}
}
