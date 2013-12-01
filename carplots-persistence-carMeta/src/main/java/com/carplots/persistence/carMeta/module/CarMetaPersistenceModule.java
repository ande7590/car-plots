package com.carplots.persistence.carMeta.module;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.common.module.AbstractCarplotsModule;
import com.carplots.common.module.AbstractCarplotsPrivateModule;
import com.carplots.common.module.CarMeta;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.dao.hibernate.CarModelDaoHibernateImpl;
import com.google.inject.PrivateModule;
import com.google.inject.persist.jpa.JpaPersistModule;

public class CarMetaPersistenceModule extends AbstractCarplotsPrivateModule {

	public final static String CARPLOTS_CARMETA_UNIT_NAME = "jpaCarplotsCarMetaUnit";
	
	@Override
	protected void doConfigure() {
		configurePersistence();
	}		
	
	@Override
	protected void doExpose() {}
	
	protected void configurePersistence() {
		bind(CarModelDao.class).to(CarModelDaoHibernateImpl.class);
		bind(InitializationService.class).annotatedWith(CarMeta.class).to(CarMetaPersistenceInitializationService.class);
		install(new JpaPersistModule(CARPLOTS_CARMETA_UNIT_NAME));
	}
	
}
