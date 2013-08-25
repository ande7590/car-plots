package com.carplots.scraper.dataimport.carsDotCom

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository.CarsDotComRepositoryFetchException
import com.google.inject.Inject;

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class CarsDotComRepositoryImpl implements CarsDotComRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(CarsDotComRepositoryImpl.class)
	
	@Inject
	CarsDotComRepositoryConfiguration repoConfig	
		
	CarsDotComRepositoryImpl() {}
	
	
	private volatile ThreadLocal<HTTPBuilder> httpBuilderThreadLocal
	private final Object threadLocalLock = new Object()
	private HTTPBuilder getHttpBuilder() {
		if (httpBuilderThreadLocal == null) {
			synchronized (threadLocalLock) {
				if (httpBuilderThreadLocal == null) {
					httpBuilderThreadLocal = getHttpBuilderThreadLocal(
						repoConfig.scraperBaseURL)
				}	
			}			
		}
		return httpBuilderThreadLocal.get()
	}
	
			
	@Override
	String getSummaryPageHtml(def makeId, def modelId, def zipcode, def radius, def pageNum) 
		throws CarsDotComRepositoryFetchException {
		
		def http = getHttpBuilder()
			
		def query = [
			mkId: makeId,
			mdId: modelId,
			zc: zipcode,
			rd: radius
		]
		if (pageNum > 1) 
			query.rn = ((pageNum as int) * repoConfig.getRecordsPerPage()) as String			
		
		StringReader reader = null
		def retries = 0		
		while (reader == null && retries < repoConfig.getNumRetries()) {
			reader = http.get(query:query, contentType: TEXT)
			if (reader == null) {
				logger.warn('Cars.com http request returned nothing, sleeping...')
				try {
					Thread.sleep(repoConfig.getFailureSleepMS())
				} catch (Exception ex) {} 							
				retries++
			}						
		}
		
		if (reader == null) {
			logger.error('Unable to retrieve HTTP data, crawler aborting.')
			throw new CarsDotComRepositoryFetchException('Unable to retrieve HTTP data.')
		} 
				
		return reader.getText() 
	}
	
	void doHandleFailure(def resp) {
		logger.error("Remote HTTP request failure ${resp.toString()}")
	}	
	
	private def getHttpBuilderThreadLocal(final String scraperBaseURL) {
		return new ThreadLocal<HTTPBuilder>() {
			protected def initialValue() {
				def http = new HTTPBuilder(scraperBaseURL)
				http.handler.failure = { resp ->
					doHandleFailure(resp)
				}
				return http
			}
		}
	}
	
	static class CarsDotComRepositoryConfiguration {
		@Inject
		ScraperConfigService configService		
		
		int getFailureSleepMS() {
			return configService.getApplicationParameter('failureSleepMS') as int
		}		
		int getRecordsPerPage() { 
			return configService.getApplicationParameter('recordsPerPage') as int
		}		
		int getNumRetries() {
			return configService.getApplicationParameter('numRetries') as int
		}			
		String getScraperBaseURL() {
			return configService.getApplicationParameter('scraperBaseURL')
		}
	}	
	
}
