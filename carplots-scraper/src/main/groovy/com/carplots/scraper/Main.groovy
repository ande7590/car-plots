package com.carplots.scraper

import javax.persistence.EntityManager;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.persistence.ScraperPersistenceInitializationService;
import com.carplots.scraper.dataimport.DataImportManager
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector

class Main {
	
	static Logger logger = LoggerFactory.getLogger(Main.class)
	
	void run() {
		
		//load dependency injection framework
		Injector injector = Guice.createInjector(new ScraperModule())
		
		//start persistence framework
		injector.getInstance(ScraperPersistenceInitializationService.class).start()
		EntityManager entityManager = injector.getInstance(EntityManager.class)

		//start a transaction (doesn't really matter since 
		//mySQL storage engine doesn't support it)
		entityManager.getTransaction().begin()
		
		//begin importing data
		try {
			injector.getInstance(DataImportManager.class).importData()
			logger.debug('Done importing data')
			entityManager.getTransaction().commit()
		}
		catch (Exception ex) {
			//once again, doesn't matter with the current storage engine
			logger.warn('Caught exception from data import', ex)
			entityManager.getTransaction().rollback()
		}					
	}
	
	static main(args) {
		logger.debug('Starting program')
		(new Main()).run()				
		logger.debug('Finishing program')
	}		

}
