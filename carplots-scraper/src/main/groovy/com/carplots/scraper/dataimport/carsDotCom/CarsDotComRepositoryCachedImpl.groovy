package com.carplots.scraper.dataimport.carsDotCom

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository.CarsDotComRepositoryFetchException
import com.google.inject.Inject;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

import sun.misc.IOUtils;

class CarsDotComRepositoryCachedImpl extends CarsDotComRepositoryImpl {	
	
	@Inject
	CarsDotComCachedRepositoryConfig cachedRepositoryConfig  
			
	private boolean isCacheSetup = false	
		
	@Override
	String getSummaryPageHtml(Object makeId, Object modelId, Object zipcode,
			Object radius, Object pageNum) throws CarsDotComRepositoryFetchException {
			
		//check if cache needs creating
		if (!isCacheSetup) {
			setupCache()
			isCacheSetup = true
		}
		
		def pageHtml = getCachedEntry(makeId, modelId, zipcode, radius, pageNum)		
		if (pageHtml == null) {
			pageHtml = super.getSummaryPageHtml(makeId, modelId, zipcode, radius, pageNum)
			setCachedEntry(makeId, modelId, zipcode, radius, pageNum, pageHtml)	
		}		
		
		return pageHtml;
	}
	
	private String getCachedEntry(makeId, modelId, zipcode, radius, pageNum) {		
		def entry = null
		def fileName = [
			makeId, modelId, zipcode, radius, pageNum].join('_') + '.zip'		
		def entryPath = cachedRepositoryConfig.cacheDir + File.separator + fileName
		def cacheFile = new File(entryPath )				
		if (cacheFile.exists()) {
			def is = new FileInputStream(entryPath)
			def zipIs = new ZipInputStream(is)
			zipIs.getNextEntry()
			entry = new String(org.apache.commons.io.IOUtils.toByteArray(zipIs),
				"UTF-8")
		}
		return entry
	}
	
	private void setCachedEntry(makeId, modelId, zipcode, radius, pageNum, pageHtml) {
		
		def entryName = [
			makeId, modelId, zipcode, radius, pageNum].join('_') 
		def fileName =  entryName + '.zip'		
		
		def os = new FileOutputStream(cachedRepositoryConfig.cacheDir + File.separator + fileName)
		def zipOs = new ZipOutputStream(os)
		try {
			def entry = new ZipEntry(entryName)
			zipOs.putNextEntry(entry)
			zipOs.write(pageHtml.getBytes("UTF-8"))
		} 
		finally {
			zipOs.close()
			os.close()						
		}
	}
	
	private void setupCache() {				
		
		//create cache directory if it doesn't exist
		mkdirAll(cachedRepositoryConfig.cacheDir)
		
		//sanity check
		def cacheDir = new File(cachedRepositoryConfig.cacheDir)
		if (cacheDir.exists() == false) {
			throw new FileNotFoundException('Cache directory doesn\'t exist: ' +
				cachedRepositoryConfig.cacheDir)
		}				
	}
			
	private void mkdirAll(String path) {		
		
		def pathParts = path.
			replaceFirst('^' + File.separator , '').
			replaceFirst(File.separator + '$', '').
			split(File.separator)
		
		def currentPath = File.separator		
		pathParts.each { pathPt ->
			currentPath += pathPt + File.separator
			def f = new File(currentPath)
			if (!f.exists()) f.mkdir()			
		}
	}
			
	static class CarsDotComCachedRepositoryConfig {		
		@Inject
		ScraperConfigService configService	
		
		String getCacheDir() {
			return configService.getApplicationParameter('repositoryCacheDirectory')
		}	
	}
}
