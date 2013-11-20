package com.carplots.persistence.scraper.module;

import java.text.Annotation;

import com.carplots.common.interfaces.InitializationService;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class ScraperPersistenceInitializationService implements InitializationService 
{
	private final PersistService persistService;
	
	@Inject
	private ScraperPersistenceInitializationService(PersistService persistService)
	{
		this.persistService = persistService;
	}
	
	@Override
	public void start() throws Exception {
		persistService.start();
	}

	@Override
	public void stop() {
		persistService.stop();
	}

}
