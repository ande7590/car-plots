package com.carplots.service.carMeta;

import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.entities.CarModel;
import com.google.inject.Inject;

public class CarMetaServiceImpl implements CarMetaService {

	@Inject
	CarModelDao carModelDao;
	
	@Override
	public void createModel(final CarModel carModel) {
		carModelDao.persist(carModel);
	}
}
