package com.carplots.persistence.scraper.test

import com.carplots.persistence.ScraperPersistenceModule
import com.google.inject.AbstractModule;
import com.carplots.persistence.scraper.dao.*;
import com.carplots.persistence.scraper.dao.hibernate.*;

class SearchDaoTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SearchDao.class).to(SearchDaoHibernateImpl.class);
		install(new ScraperPersistenceModule());		
	}

}
