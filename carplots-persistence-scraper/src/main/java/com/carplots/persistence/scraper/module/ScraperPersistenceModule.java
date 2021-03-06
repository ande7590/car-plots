package com.carplots.persistence.scraper.module;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.common.module.AbstractCarplotsPrivateModule;
import com.carplots.common.module.Scraper;
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.LocationDao;
import com.carplots.persistence.scraper.dao.MakeModelDao;
import com.carplots.persistence.scraper.dao.ScraperBatchDao;
import com.carplots.persistence.scraper.dao.ScraperBatchSearchDao;
import com.carplots.persistence.scraper.dao.ScraperRunDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.dao.hibernate.ImportedDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.LocationDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.MakeModelDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.ScraperBatchDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.ScraperBatchSearchDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.ScraperRunDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.SearchDaoHibernateImpl;
import com.google.inject.persist.jpa.JpaPersistModule;

public class ScraperPersistenceModule extends AbstractCarplotsPrivateModule {	
	
	public final static String CARPLOTS_SCRAPER_UNIT_NAME = "jpaCarplotsScraperUnit";
	
	@Override
	protected void doConfigure() {
		configurePersistence();
	}

	@Override
	protected void doExpose() {
		
	}
	
	protected void configurePersistence() {		
		bind(LocationDao.class).to(LocationDaoHibernateImpl.class);
		bind(ImportedDao.class).to(ImportedDaoHibernateImpl.class);
		bind(MakeModelDao.class).to(MakeModelDaoHibernateImpl.class);
		bind(ScraperBatchDao.class).to(ScraperBatchDaoHibernateImpl.class);
		bind(ScraperBatchSearchDao.class).to(ScraperBatchSearchDaoHibernateImpl.class);
		bind(ScraperRunDao.class).to(ScraperRunDaoHibernateImpl.class);
		bind(SearchDao.class).to(SearchDaoHibernateImpl.class);
		bind(InitializationService.class).to(ScraperPersistenceInitializationService.class);
		install(new JpaPersistModule(CARPLOTS_SCRAPER_UNIT_NAME));
	}

}
