package com.carplots.scraper.dataimport.carsDotCom

import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository.CarsDotComRepositoryFetchException
import com.carplots.scraper.cache.Cache
import com.carplots.scraper.cache.ZipFileCache;
import com.carplots.scraper.config.ScraperConfigService;
import com.google.inject.Inject;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import sun.misc.IOUtils;

class CarsDotComRepositoryCachedImpl
	extends CarsDotComRepositoryImpl {
	
	@Inject
	CarsDotComCachedRepositoryConfig cachedRepositoryConfig
		
	@Override
	String getSummaryPageHtml(Object makeId, Object modelId, Object zipcode,
			Object radius, Object pageNum) throws CarsDotComRepositoryFetchException {
		
		def pageHtml = getCachedEntry(makeId, modelId, zipcode, radius, pageNum)
		if (pageHtml == null) {
			pageHtml = super.getSummaryPageHtml(makeId, modelId, zipcode, radius, pageNum)
			setCachedEntry(makeId, modelId, zipcode, radius, pageNum, pageHtml)
		}
		
		return pageHtml;
	}	
	
	private String getCachedEntry(makeId, modelId, zipcode, radius, pageNum) {
		def entryName = [
			makeId, modelId, zipcode, radius, pageNum].join('_')
		def entry = null
		synchronized (cacheMutex) {
			entry = getCache().getCachedEntry(entryName)
		}
		return entry
	}
	
	private void setCachedEntry(makeId, modelId, zipcode, radius, pageNum, pageHtml) {
		def entryName = [
			makeId, modelId, zipcode, radius, pageNum].join('_')
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
			
	static class CarsDotComCachedRepositoryConfig {
		@Inject
		ScraperConfigService configService
		String getCacheDir() {
			return configService.getApplicationParameter('carsDotComRepositoryCacheDirectory')
		}
	}
}
