package com.carplots.service.analysis.module;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.common.module.AbstractCarplotsModule;
import com.carplots.common.module.CarMeta;
import com.carplots.common.module.Scraper;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceInitializationService;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceModule;
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.LocationDao;
import com.carplots.persistence.scraper.dao.MakeModelDao;
import com.carplots.persistence.scraper.dao.ScraperRunDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.module.ScraperPersistenceInitializationService;
import com.carplots.persistence.scraper.module.ScraperPersistenceModule;
import com.carplots.service.analysis.CarplotsAnalysisService;
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;

public class AnalysisServiceModule extends AbstractCarplotsModule {

	@Override
	protected void configure() {	
		configureScraperDaos();
		configureCarMetaDaos();
		bind(CarplotsAnalysisService.class)
			.to(CarplotsAnalysisServiceImpl.class);
	}
	
	protected void configureScraperDaos() {
		install(new ScraperPersistenceModule() {			
			@Override
			protected void doConfigure() {
				super.doConfigure();				
				bind(InitializationService.class).annotatedWith(Scraper.class)
					.to(ScraperPersistenceInitializationService.class);
			}			
			@Override
			protected void doExpose() {
				expose(InitializationService.class).annotatedWith(Scraper.class);		
				expose(MakeModelDao.class);
				expose(LocationDao.class);
				expose(ImportedDao.class);
				expose(ScraperRunDao.class);
				expose(SearchDao.class);
			}
		});
	}
	
	protected void configureCarMetaDaos() {
		install(new CarMetaPersistenceModule() {
			@Override
			protected void doConfigure() {
				super.doConfigure();
				bind(InitializationService.class).annotatedWith(CarMeta.class)
					.to(CarMetaPersistenceInitializationService.class);
			}
			@Override
			protected void doExpose() {
				expose(InitializationService.class).annotatedWith(CarMeta.class);
				expose(CarModelDao.class);
			}
		});
	}

}
