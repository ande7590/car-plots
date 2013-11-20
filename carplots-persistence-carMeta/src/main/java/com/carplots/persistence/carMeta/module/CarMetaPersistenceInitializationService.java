package com.carplots.persistence.carMeta.module;

import java.text.Annotation;

import com.carplots.common.interfaces.InitializationService;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class CarMetaPersistenceInitializationService implements InitializationService 
{
	private final PersistService persistService;
	
	@Inject
	private CarMetaPersistenceInitializationService(PersistService persistService)
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
