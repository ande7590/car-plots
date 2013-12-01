package com.carplots.service.carMeta;

import com.carplots.persistence.carMeta.module.CarMetaPersistenceModule;
import com.google.inject.AbstractModule;

public class CarMetaServiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CarMetaService.class).to(CarMetaServiceImpl.class);
		install(new CarMetaPersistenceModule());
	}

}
