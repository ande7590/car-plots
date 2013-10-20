package com.carplots.scraper.dataimport.edmunds

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.cache.Cache;
import com.carplots.scraper.cache.ZipFileCache
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException
import com.google.inject.Inject;
import groovy.json.JsonSlurper
import org.json.JSONObject


class EdmundsRepositoryCachedImpl
	extends EdmundsRepositoryImpl {

	@Inject
	EdmundsRepositoryCachedConfig cachedRepositoryConfig

	private static String CACHE_KEY_makeJSON = 'makeJson'
	private static String CACHE_KEY_makeModelYearHTML = 'makeModelYearHtml'
	
	@Override
	public def getMakeJSON(String makeName) {
		
		def result = getCachedEntry(CACHE_KEY_makeJSON, makeName)
		if (result == null || result == "") {
			result = super.getMakeJSON(makeName)
			setCachedEntry(CACHE_KEY_makeJSON, makeName, (new JSONObject(result)).toString() )
		} else {
			result = (new JsonSlurper()).parseText(result)
		}
		return result
	}
	
	@Override
	public def getMakeModelYearJSON(String makeName, String modelName, String year) {
		def result = getCachedEntry(CACHE_KEY_makeModelYearHTML, makeName, modelName, year)
		if (result == null || result == "" || result == "{}") {
			result = super.getMakeModelYearJSON(makeName, modelName, year)
			if (result != null) {
				setCachedEntry(CACHE_KEY_makeModelYearHTML, makeName, modelName, year, (new JSONObject(result)).toString() )
			}
		} else {
			result = (new JsonSlurper()).parseText(result)
		}
		return result
	}
		
	private String getCachedEntry(String... args) {				
		def foo = 'bar'
		def entryName = getKey(args)
		def entry = null
		synchronized (cacheMutex) {
			entry = getCache().getCachedEntry(entryName)
		}
		return entry
	}
	
	private void setCachedEntry(String... args) {
		def entryName = getKey( *(args[0..args.size()-2]))
		def entry = args[-1]
		synchronized (cacheMutex) {
			getCache().setCachedEntry(entryName, entry)
		}
	}
	
	private String getKey(String... args) {
		return args.collect {
			it.replace('_', '-')
		}.join('_')
	}
			
	private final Object cacheMutex = new Object()
	private final Object cacheCreationMutex = new Object()
	private volatile Cache<String> cache
	protected Cache<String> getCache() {
		if (cache == null) {
			synchronized (cacheCreationMutex) {
				if (cache == null) {
					cache = new ZipFileCache(cachedRepositoryConfig.getCacheDir())
				}
			}
		}
		return cache
	}
				
	public static class EdmundsRepositoryCachedConfig {
		@Inject
		ScraperConfigService configService
		String getCacheDir() {
			return configService.getApplicationParameter('edmundsRepositoryCacheDirectory')
		}
		
	}
}
