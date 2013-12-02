package com.carplots.analysis.wrapper;

import com.carplots.persistence.scraper.module.ScraperPersistenceModule;
import com.carplots.service.analysis.CarplotsAnalysisService;
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;
import com.carplots.service.analysis.module.AnalysisServiceModule;
import com.google.inject.AbstractModule;

public class CarplotsAnalysisWrapperModule extends AbstractModule {

	@Override
	protected void configure() {
		System.out.println("Calling configure");
		bind(CarplotsAnalysisServiceImpl.class);
		System.out.println("Installing analysis service module");
		install(new AnalysisServiceModule());
		System.out.println("Configure done");
	}

}
