package com.carplots.scraper.dataimport.carsDotCom

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

class CarsDotComRepositoryImpl implements CarsDotComRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(CarsDotComRepositoryImpl.class)
	
	//TODO: javaconf
	private static final int FAILURE_SLEEP = 5000
	private static final int RECORDS_PER_PAGE = 250
	private static final int NUM_RETRIES = 3
	private static final String BASE_QUERY_URL = 
		"http://www.cars.com/for-sale/searchresults.action?stkTyp=U&tracktype=usedcc&searchSource=QUICK_FORM&enableSeo=1&rpp=${RECORDS_PER_PAGE}"
	
	private final HTTPBuilder http;
		
	CarsDotComRepositoryImpl() {		
		def http = new HTTPBuilder(BASE_QUERY_URL)		
		http.handler.failure = { resp ->
			doHandleFailure(resp)	
		}		
		this.http = http
	}
		
	/* (non-Javadoc)
	 * @see com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository#getSummaryPageHtml(java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	String getSummaryPageHtml(def makeId, def modelId, def zipcode, def radius, def pageNum) {
		
		def query = [
			mkId: makeId,
			mdId: modelId,
			zc: zipcode,
			rd: radius
		]
		if (pageNum > 1) 
			query.rn = ((pageNum as int) * RECORDS_PER_PAGE) as String			
		
		StringReader reader = null
		def retries = 0		
		while (reader == null && retries < NUM_RETRIES ) {
			reader = http.get(query:query, contentType: TEXT)
			if (reader == null) {
				logger.warn('Cars.com http request returned nothing, sleeping...')
				try {
					Thread.sleep(FAILURE_SLEEP)
				} catch (Exception ex) {} 							
				retries++
			}						
		}
		
		if (reader == null) {
			logger.error('Unable to retrieve HTTP data, crawler aborting.')
			throw new Exception('Unable to retrieve HTTP data.')
		} 
				
		return reader.getText() 
	}
	
	void doHandleFailure(def resp) {
		logger.error("Remote HTTP request failure ${resp.toString()}")
	}
	
	
}
