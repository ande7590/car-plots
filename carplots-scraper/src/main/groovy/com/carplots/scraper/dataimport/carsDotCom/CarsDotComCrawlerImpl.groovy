package com.carplots.scraper.dataimport.carsDotCom

import java.util.Iterator;

import spock.lang.Shared;

import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Inject;

class CarsDotComCrawlerImpl implements CarsDotComCrawler {

	@Inject
	CarsDotComRepository carsDotComRepo
	
	@Inject
	CarplotsScraperService scraperService
	
	final CarsDotComCrawlerConfig crawlerConfig = new CarsDotComCrawlerConfig()		
	
	@Override
	public Iterator<CarsDotComCrawlerData> iterator() {
		return new CarsDotComCrawlerIterator(crawlerConfig.scraperBatchId, 
			carsDotComRepo, scraperService) 
	}	
	
	//todo: java conf
	static class CarsDotComCrawlerConfig {
		long scraperBatchId = 10		
	}
	
}
