package com.carplots.scraper.test

import com.carplots.persistence.ScraperPersistenceModule
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawler;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryCachedImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraper;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperHtmlImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperStats;
import com.carplots.service.scraper.CarplotsScraperServiceModule
import com.google.inject.AbstractModule;

class CarsDotComTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CarsDotComScraper.class).to(CarsDotComScraperHtmlImpl.class)
		bind(CarsDotComCrawler.class).to(CarsDotComCrawlerImpl.class)
		bind(CarsDotComRepository.class).to(CarsDotComRepositoryCachedImpl.class)
		install(new CarplotsScraperServiceModule())
	}
	
}
