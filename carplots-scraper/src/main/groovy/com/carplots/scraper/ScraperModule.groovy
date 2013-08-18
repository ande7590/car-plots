package com.carplots.scraper

import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.dataimport.ImportedEmitter;
import com.carplots.scraper.dataimport.ImportedEmitterScraperServiceImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawler;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryCachedImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraper;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperHtmlImpl;
import com.carplots.scraper.dataimport.carsDotCom.DataImportManagerCarsDotComImpl;
import com.carplots.service.scraper.CarplotsScraperServiceModule
import com.google.inject.AbstractModule

class ScraperModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(DataImportManager.class).to(DataImportManagerCarsDotComImpl.class)
		bind(ImportedEmitter.class).to(ImportedEmitterScraperServiceImpl.class)	
		bind(CarsDotComScraper.class).to(CarsDotComScraperHtmlImpl.class)
		bind(CarsDotComCrawler.class).to(CarsDotComCrawlerImpl.class)
		bind(CarsDotComRepository.class).to(CarsDotComRepositoryCachedImpl.class)
		install(new CarplotsScraperServiceModule())	
	}

}
