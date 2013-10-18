package com.carplots.scraper.test.module

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.ScraperConfigServiceImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryCachedImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryCachedImpl.EdmundsRepositoryCachedConfig;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryImpl.EdmundsRepositoryConfiguration;
import com.google.inject.AbstractModule;

class EdmundsJSONFetchModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ScraperConfigService.class).to(ScraperConfigServiceImpl.class)
		bind(EdmundsRepositoryConfiguration.class)
		bind(EdmundsRepositoryCachedConfig.class)
		bind(EdmundsRepository.class).to(EdmundsRepositoryCachedImpl.class)
	}

}
