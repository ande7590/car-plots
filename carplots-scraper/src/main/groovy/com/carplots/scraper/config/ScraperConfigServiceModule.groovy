package com.carplots.scraper.config

import com.google.inject.AbstractModule;

class ScraperConfigServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ScraperConfigService.class).to(ScraperConfigServiceImpl.class)
	}

}
