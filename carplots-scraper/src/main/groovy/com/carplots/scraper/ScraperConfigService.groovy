package com.carplots.scraper

interface ScraperConfigService {
	
	def getApplicationParameter(propertyName) throws ScraperConfigServicePropertyMissing;
	void setApplicationParameter(propertyName, propertyValue)
	
	public static class ScraperConfigServicePropertyMissing extends Exception {
		public ScraperConfigServicePropertyMissing(String message) {
			super(message)			
		}
		
	}
}
