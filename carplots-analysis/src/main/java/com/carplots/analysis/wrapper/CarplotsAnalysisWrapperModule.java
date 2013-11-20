package com.carplots.analysis.wrapper;

import com.carplots.persistence.scraper.module.ScraperPersistenceModule;
import com.carplots.service.analysis.CarplotsAnalysisService;
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;
import com.google.inject.AbstractModule;

public class CarplotsAnalysisWrapperModule extends AbstractModule {

	@Override
	protected void configure() {
		System.out.println("Calling configure");
		bind(CarplotsAnalysisService.class).to(CarplotsAnalysisServiceImpl.class);
		System.out.println("Installing persistence module");
		install(new ScraperPersistenceModule());
		System.out.println("Configure done");
	}

}
