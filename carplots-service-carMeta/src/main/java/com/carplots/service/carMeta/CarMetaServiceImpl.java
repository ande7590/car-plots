package com.carplots.service.carMeta;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.entities.CarModel;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class CarMetaServiceImpl implements CarMetaService {

	@Inject
	CarModelDao carModelDao;
	
	@Inject
	public CarMetaServiceImpl(final InitializationService initSvc) throws Exception {
		initSvc.start();
	}
	
	@Override
	@Transactional
	public void createModel(final CarModel carModel) {
		carModelDao.persist(carModel);
	}
}
