package com.carplots.scraper

import com.carplots.scraper.config.ScraperConfigService;
import com.carplots.scraper.config.ScraperConfigServiceImpl;
import com.carplots.scraper.config.ScraperConfigServiceModule;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.dataimport.ImportedEmitter;
import com.carplots.scraper.dataimport.ImportedEmitterScraperServiceImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawler;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerImpl.CarsDotComCrawlerConfig;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryCachedImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryCachedImpl.CarsDotComCachedRepositoryConfig;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepositoryImpl.CarsDotComRepositoryConfiguration;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraper;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperHtmlImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComScraperHtmlImpl.CarsDotComScraperHtmlImplConfig;
import com.carplots.scraper.dataimport.carsDotCom.DataImportManagerCarsDotComImpl;
import com.carplots.scraper.dataimport.carsDotCom.DataImportManagerCarsDotComImpl.DataImportManagerConcurrentImplConfiguration;
import com.carplots.service.scraper.CarplotsScraperServiceModule
import com.google.inject.AbstractModule
import com.google.inject.name.Names;


class ScraperModule extends AbstractModule {

	@Override
	protected void configure() {
		
		install(new ScraperConfigServiceModule())
		
		bind(DataImportManagerConcurrentImplConfiguration.class)
		bind(CarsDotComCrawlerConfig.class)
		bind(CarsDotComCachedRepositoryConfig.class)
		bind(CarsDotComRepositoryConfiguration.class)		
		bind(CarsDotComScraperHtmlImplConfig.class)

		bind(DataImportManager.class).to(DataImportManagerCarsDotComImpl.class)
		bind(ImportedEmitter.class).to(ImportedEmitterScraperServiceImpl.class)
		bind(CarsDotComScraper.class).to(CarsDotComScraperHtmlImpl.class)
		bind(CarsDotComCrawler.class).to(CarsDotComCrawlerImpl.class)
		bind(CarsDotComRepository.class).to(CarsDotComRepositoryCachedImpl.class)
		install(new CarplotsScraperServiceModule())
	}

}
