package com.carplots.scraper.dataimport.carsDotCom

import java.util.Iterator;

import com.carplots.scraper.config.ScraperConfigService;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData
import com.carplots.service.scraper.CarplotsScraperService
import com.google.inject.Inject

class CarsDotComCrawlerImpl implements CarsDotComCrawler {

	@Inject
	CarsDotComRepository carsDotComRepo
	
	@Inject
	CarplotsScraperService scraperService
	
	@Inject
	CarsDotComCrawlerConfig crawlerConfig
	
	@Override
	public Iterator<CarsDotComCrawlerData> iterator() {
		return new CarsDotComCrawlerIterator(crawlerConfig.scraperBatchId,
			carsDotComRepo, scraperService)
	}
	
	static class CarsDotComCrawlerConfig {
		@Inject
		ScraperConfigService configService
		int getScraperBatchId() {
			return configService.getApplicationParameter('scraperBatchId') as int
		}
	}
	
}
