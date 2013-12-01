package com.carplots.service.scraper.module;

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
import com.carplots.persistence.scraper.module.ScraperPersistenceInitializationService;
import com.carplots.persistence.scraper.module.ScraperPersistenceModule;
import com.carplots.service.scraper.CarplotsScraperService;
import com.carplots.service.scraper.CarplotsScraperServiceImpl;
import com.google.inject.persist.jpa.JpaPersistModule;

public class ScraperServiceModule extends ScraperPersistenceModule {	
	
	public final static String CARPLOTS_SCRAPER_UNIT_NAME = "jpaCarplotsScraperUnit";
	
	@Override
	protected void doConfigure() {
		super.doConfigure();
		configureService();
	}

	@Override
	protected void doExpose() {
		expose(CarplotsScraperService.class);		
	}
	
	protected void configureService() {
		bind(CarplotsScraperServiceImpl.class);
		bind(CarplotsScraperService.class).to(CarplotsScraperServiceImpl.class);
	}

}
