	package com.carplots.scraper.dataimport.edmunds.module

import static java.lang.annotation.ElementType.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.persistence.EntityManager;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.dao.hibernate.CarModelDaoHibernateImpl;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceInitializationService;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceModule
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
import com.carplots.persistence.scraper.module.ScraperPersistenceModule
import com.carplots.scraper.config.ScraperConfigServiceModule;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.dataimport.edmunds.EdmundsMetaDataImportManager;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryCachedImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryCachedImpl.EdmundsRepositoryCachedConfig;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepositoryImpl.EdmundsRepositoryConfiguration;
import com.carplots.service.carMeta.CarMetaService;
import com.carplots.service.carMeta.CarMetaServiceImpl;
import com.carplots.service.carMeta.CarMetaServiceModule;
import com.carplots.service.scraper.CarplotsScraperService;
import com.carplots.service.scraper.CarplotsScraperServiceImpl;
import com.carplots.service.scraper.CarplotsScraperServiceModule;
import com.carplots.service.scraper.module.ScraperServiceModule;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.PrivateModule
import com.google.inject.Provider;
import com.google.inject.persist.jpa.JpaPersistModule;


class EdmundsImporterModule extends AbstractModule {
		
	@Override
	protected void configure() {
		
		bind(DataImportManager.class).to(EdmundsMetaDataImportManager.class)
		bind(EdmundsRepositoryConfiguration.class)
		bind(EdmundsRepositoryCachedConfig.class)
		bind(EdmundsRepository.class).to(EdmundsRepositoryCachedImpl.class)		
		install(new ScraperConfigServiceModule())		
		install(new ScraperServiceModule())
		install(new CarMetaServiceModule())		
	}		
}


	
