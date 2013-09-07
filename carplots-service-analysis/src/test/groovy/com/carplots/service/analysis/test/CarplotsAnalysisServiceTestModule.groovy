package com.carplots.service.analysis.test
import com.carplots.persistence.ScraperPersistenceModule;
import com.carplots.service.analysis.CarplotsAnalysisService;
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;
import com.google.inject.AbstractModule


class CarplotsAnalysisServiceTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(CarplotsAnalysisService.class).to(CarplotsAnalysisServiceImpl.class);
		install(new ScraperPersistenceModule());
	}

}
