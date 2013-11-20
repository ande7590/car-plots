package com.carplots.persistence.carMeta.module;

import com.carplots.common.modules.AbstractCarplotsModule;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.dao.hibernate.CarModelDaoHibernateImpl;
import com.google.inject.persist.jpa.JpaPersistModule;

public class CarMetaPersistenceModule extends AbstractCarplotsModule{

	public final static String CARPLOTS_CARMETA_UNIT_NAME = "jpaCarplotsCarMetaUnit";
	
	@Override
	protected void configure() {
		bind(CarModelDao.class).to(CarModelDaoHibernateImpl.class);		
		bindInitializationService(CarMetaPersistenceInitializationService.class);
		install(new JpaPersistModule(CARPLOTS_CARMETA_UNIT_NAME));
	}

}
