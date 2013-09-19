package com.carplots.scraper.dataimport.edmunds

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.dataimport.AbstractScraperRepository;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.corba.se.impl.activation.ListServers;

import static groovyx.net.http.ContentType.*;
import static groovyx.net.http.Method.*
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.Method.*;

class EdmundsRepositoryImpl
	extends AbstractScraperRepository
	implements EdmundsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EdmundsRepositoryImpl.class)
	
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
	
	@Override
	public String getMakeData(String makeName)
			throws EdmundsRepositoryFetchException {
		
		if (edmundsMakes.find { it == makeName } == null) {
			throw new IllegalArgumentException('Make {$makeName} not found.')
		}
		
		def reader = getReader("/${makeName}")
		
		if (reader == null) {
			final def message = "Unable to retrieve HTTP data for ${makeName}";
			throw new EdmundsRepositoryFetchException(message)
		}
		
		return reader.getText()
	}

	@Override
	public String getMakeModelData(String makeName, String modelName)
			throws EdmundsRepositoryFetchException {
		
		if (edmundsMakes.find { it == makeName } == null) {
			throw new IllegalArgumentException('Make {$makeName} not found.')
		}
		
		def reader = getReader("/${makeName}/${modelName}")
		
		if (reader == null) {
			final def message = "Unable to retrieve HTTP data for ${makeName}, ${modelName}";
			throw new EdmundsRepositoryFetchException(message)
		}
		
		return reader.getText()
	}
		
	private def getReader(def path) {
		def http = getHttpBuilder()
		StringReader reader = null
		def retries = 0
		while (reader == null && retries < repoConfig.getNumRetries()) {
			reader = http.get(path:path, contentType: TEXT)
			if (reader == null) {
				logger.warn('Edmunds http request returned nothing, sleeping...')
				try {
					Thread.sleep(repoConfig.getNumRetries())
				} catch (Exception ex) {}
				retries++
			}
		}
		return reader
	}
	
	@Override
	protected String getScraperBaseURL() {
		return repoConfig.getScraperBaseURL()
	}

	@Override
	protected void doHandleFailure(def resp) {
		logger.error("Remote request failure ${resp.toString()}")
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
