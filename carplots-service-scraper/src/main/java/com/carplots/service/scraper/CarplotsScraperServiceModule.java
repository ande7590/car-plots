package com.carplots.service.scraper;

import com.carplots.persistence.scraper.module.ScraperPersistenceModule;
import com.google.inject.AbstractModule;

public class CarplotsScraperServiceModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(CarplotsScraperService.class).to(CarplotsScraperServiceImpl.class);
		install(new ScraperPersistenceModule());		
	}

}
