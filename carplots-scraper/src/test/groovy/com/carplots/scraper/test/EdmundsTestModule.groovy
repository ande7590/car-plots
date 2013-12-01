package com.carplots.scraper.test

import com.carplots.scraper.config.ScraperConfigService;
import com.carplots.scraper.config.ScraperConfigServiceImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryCachedImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryCachedImpl.EdmundsRepositoryCachedConfig;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryImpl.EdmundsRepositoryConfiguration;
import com.carplots.service.scraper.CarplotsScraperServiceModule;
import com.google.inject.AbstractModule

import org.apache.commons.lang.NotImplementedException

class EdmundsTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EdmundsRepository.class).to(EdmundsRepositoryCachedImpl.class)
		bind(ScraperConfigService.class).to(ScraperConfigServiceImpl.class)
		bind(EdmundsRepositoryConfiguration.class)
		bind(EdmundsRepositoryCachedConfig.class)
		install(new CarplotsScraperServiceModule())
	}
}
