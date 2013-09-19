package com.carplots.scraper.dataimport.edmunds

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.cache.Cache;
import com.carplots.scraper.cache.ZipFileCache
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException
import com.google.inject.Inject;

class EdmundsRepositoryCachedImpl
	extends EdmundsRepositoryImpl {

	@Inject
	EdmundsRepositoryCachedConfig cachedRepositoryConfig	
	
	@Override
	public String getMakeModelData(String makeName, String modelName)
			throws EdmundsRepositoryFetchException {
		
		def pageHtml = getCachedEntry(makeName, modelName)
		if (pageHtml == null) {
			pageHtml = super.getMakeModelData(makeName, modelName)
			setCachedEntry(makeName, modelName, pageHtml) 
		}
				
		return pageHtml
	}
			
	@Override
	public String getMakeData(String makeName)
			throws EdmundsRepositoryFetchException {
		// TODO Auto-generated method stub
		return super.getMakeData(makeName);
	}
	
	private String getCachedEntry(makeName, modelName) {
		def entryName = "${makeName}_${modelName}"
		def entry = null
		synchronized (cacheMutex) {
			entry = getCache().getCachedEntry(entryName)
		}
		return entry
	}
	
	private void setCachedEntry(makeName, modelName, pageHtml) {
		def entryName = "${makeName}_${modelName}"
		synchronized (cacheMutex) {
			getCache().setCachedEntry(entryName, pageHtml)
		}	
	}	
			
	private final Object cacheMutex = new Object()
	private volatile Cache<String> cache
	protected Cache<String> getCache() {
		if (cache == null) {
			synchronized (cacheMutex) {
				if (cache == null) {
					cache = new ZipFileCache(cachedRepositoryConfig.getCacheDir())
				}
			}
		}
		return cache
	}
				
	static class EdmundsRepositoryCachedConfig {
		@Inject
		ScraperConfigService configService
		String getCacheDir() {
			return configService.getApplicationParameter('edmundsRepositoryCacheDirectory')
		}
	}
}
