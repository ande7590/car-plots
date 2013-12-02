package com.carplots.persistence.carMeta.dao;

import java.util.Iterator;

import com.carplots.persistence.carMeta.entities.CarModel;
import com.carplots.persistence.dao.GenericDao;

public interface CarModelDao extends GenericDao<CarModel, Long> {
	
	Iterator<CarModel> iterateCarModels(final String hqlQuery);
	
}
