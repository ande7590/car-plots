package com.carplots.service.carMeta.module;

import com.carplots.common.module.CarMeta;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceModule;
import com.carplots.service.carMeta.CarMetaService;
import com.carplots.service.carMeta.CarMetaServiceImpl;

public class CarMetaServiceModule extends CarMetaPersistenceModule {

	@Override
	protected void doConfigure() {
		super.doConfigure();
		configureService();
	}
	
	protected void configureService() {
		bind(CarMetaServiceImpl.class);
		bind(CarMetaService.class).to(CarMetaServiceImpl.class);
	}
	
	@Override
	protected void doExpose() {
		expose(CarMetaService.class);
	}
	
}
