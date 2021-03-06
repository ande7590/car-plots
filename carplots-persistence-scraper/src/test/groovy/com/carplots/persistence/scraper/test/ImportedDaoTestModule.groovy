package com.carplots.persistence.scraper.test


import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.dao.hibernate.ImportedDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.SearchDaoHibernateImpl;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.module.ScraperPersistenceModule;
import com.google.inject.AbstractModule;

class ImportedDaoTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(ImportedDao.class).to(ImportedDaoHibernateImpl.class);
		bind(SearchDao.class).to(SearchDaoHibernateImpl.class);
		install(new ScraperPersistenceModule());		
	}
	
}
