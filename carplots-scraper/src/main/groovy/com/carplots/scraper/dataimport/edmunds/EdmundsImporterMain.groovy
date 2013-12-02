package com.carplots.scraper.dataimport.edmunds

import javax.persistence.EntityManager;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceInitializationService;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.dataimport.edmunds.module.EdmundsImporterModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

class EdmundsImporterMain {

	static Logger logger = LoggerFactory.getLogger(EdmundsImporterMain.class)		
	
	void run() {
		
		Injector injector = Guice.createInjector(new EdmundsImporterModule())
		
		try {
			DataImportManager edmundsManager = 
				injector.getInstance(DataImportManager.class)
			edmundsManager.importData()
		} catch (Exception ex) {
			logger.error("DataImport manager threw exxception: ", ex)
		}
	}
	
	static main(args) {
		(new EdmundsImporterMain()).run()
	}

}

