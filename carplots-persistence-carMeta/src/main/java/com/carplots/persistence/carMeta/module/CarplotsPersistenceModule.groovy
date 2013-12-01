package com.carplots.persistence.carMeta.module

import com.carplots.common.module.persistence.ScraperPersistenceModule;
import com.google.inject.PrivateModule;

class CarplotsPersistenceModule extends PrivateModule {
	protected void configure() {
		bind(CarplotsScraperService.class).to(CarplotsScraperServiceImpl.class)
		bind(LocationDao.class).to(LocationDaoHibernateImpl.class)
		bind(ImportedDao.class).to(ImportedDaoHibernateImpl.class)
		bind(MakeModelDao.class).to(MakeModelDaoHibernateImpl.class)
		bind(ScraperBatchDao.class).to(ScraperBatchDaoHibernateImpl.class)
		bind(ScraperBatchSearchDao.class).to(ScraperBatchSearchDaoHibernateImpl.class)
		bind(ScraperRunDao.class).to(ScraperRunDaoHibernateImpl.class)
		bind(SearchDao.class).to(SearchDaoHibernateImpl.class)
		bind(InitializationService.class).annotatedWith(Scraper.class).to(ScraperPersistenceInitializationService.class)
		install(new JpaPersistModule(ScraperPersistenceModule.CARPLOTS_SCRAPER_UNIT_NAME))
		expose(CarplotsScraperService.class)
		expose(InitializationService.class).annotatedWith(Scraper.class)
	}
	
}
