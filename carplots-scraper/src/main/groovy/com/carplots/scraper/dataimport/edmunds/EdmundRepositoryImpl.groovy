package com.carplots.scraper.dataimport.edmunds

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.dataimport.AbstractScraperRepository;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.corba.se.impl.activation.ListServers;

import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.Method.*;

class EdmundRepositoryImpl 
	extends AbstractScraperRepository 
	implements EdmundsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EdmundRepositoryImpl.class)
	
	private static String[] edmundsMakes = [
		"acura", "alfa-romeo", "am-general", "aston-martin", "audi",
		"bentley", "bmw", "bugatti", "buick", "cadillac", "chevrolet",
		"chrysler", "daewoo", "dodge", "eagle", "ferrari", "fiat",
		"fisker", "ford", "geo", "gmc", "honda", "hummer", "hyundai",
		"infiniti", "isuzu", "jaguar", "jeep", "kia", "lamborghini",
		"land-rover", "lexus", "lincoln", "lotus", "maserati", "maybach",
		"mazda", "mclaren", "mercedes-benz", "mercury", "mini",
		"mitsubishi", "nissan", "oldsmobile", "panoz", "plymouth",
		"pontiac", "porsche", "ram", "rolls-royce", "saab", "saturn",
		"scion", "smart", "spyker", "subaru", "suzuki", "tesla",
		"toyota", "volkswagen", "volvo"
	]
	
	
	@Inject
	EdmundsRepositoryConfiguration repoConfig
	
	public String[] getMakes() {
		return edmundsMakes.clone();
	}
	
	String getMakeMetaData(def makeName) 
		throws EdmundsRepositoryFetchException {
		
		if (edmundsMakes.find { it == makeName } == null) {
			throw new IllegalArgumentException('Make {$makeName} not found.')
		}
		
		def http = getHttpBuilder()
		def query = getScraperBaseURL() + makeName
		
		StringReader reader = null
		def retries = 0
		while (reader == null && retries < repoConfig.getNumRetries()) {
			reader = http.get(query:query, contentType: ContentType.TEXT)
			if (reader == null) {
				logger.warn('Edmunds http request returned nothing, sleeping...')
				try {
					Thread.sleep(repoConfig.getNumRetries())
				} catch (Exception ex) {}
				retries++
			}
		}
	}
	
	@Override
	protected String getScraperBaseURL() {
		return repoConfig.getScraperBaseURL()
	}

	@Override
	protected void doHandleFailure(Object response) {
		throw EdmundsRepositoryFetchException("Remote request failure ${resp.toString()}")
	}
	
	static class EdmundsRepositoryConfiguration {
		@Inject
		ScraperConfigService configService

		String getScraperBaseURL() {
			return configService.getApplicationParameter('edmundsBaseURL') as String
		}
		int getNumRetries() {
			return configService.getApplicationParameter('numRetries') as int
		}
		int getFailureSleepMS() {
			return configService.getApplicationParameter('failureSleepMS') as int
		}
	}
}
