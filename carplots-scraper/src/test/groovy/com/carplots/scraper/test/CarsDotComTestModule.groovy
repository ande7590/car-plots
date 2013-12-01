package com.carplots.scraper.test

import com.carplots.scraper.config.ScraperConfigService;
import com.carplots.scraper.config.ScraperConfigServiceImpl;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.dataimport.ImportedEmitter;
import com.carplots.scraper.dataimport.ImportedEmitterScraperServiceImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawler;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryCachedImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraper;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperHtmlImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperStats;
import com.carplots.scraper.dataimport.carsDotCom.DataImportManagerCarsDotComImpl;
import com.carplots.service.scraper.CarplotsScraperServiceModule
import com.google.inject.AbstractModule;

class CarsDotComTestModule extends AbstractModule {

	@Override
	protected void configure() {	
		bind(CarsDotComCrawler.class).to(CarsDotComCrawlerImpl.class)
		bind(ScraperConfigService.class).to(ScraperConfigServiceImpl.class)
		bind(CarsDotComRepository.class).to(CarsDotComRepositoryCachedImpl.class)
		bind(CarsDotComScraper.class).to(CarsDotComScraperHtmlImpl.class)
		install(new CarplotsScraperServiceModule())
	}
	
}
