package com.carplots.scraper.test

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.ScraperConfigService.ScraperConfigServicePropertyMissing
import com.carplots.scraper.dataimport.edmunds.EdmundRepositoryImpl;
import com.carplots.scraper.dataimport.edmunds.EdmundRepositoryImpl.EdmundsRepositoryConfiguration;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository;
import com.carplots.service.scraper.CarplotsScraperServiceModule;
import com.google.inject.AbstractModule

import org.apache.commons.lang.NotImplementedException

class EdmundsTestModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(EdmundsRepository.class).to(EdmundRepositoryImpl.class)
		bind(ScraperConfigService.class).to(EdmundsTestScraperConfigServiceImpl.class)
		bind(EdmundsRepositoryConfiguration.class)
		install(new CarplotsScraperServiceModule())
	}

	
	public static class EdmundsTestScraperConfigServiceImpl implements ScraperConfigService {

		final def params = [
			'edmundsBaseURL':'http://www.google.com?q=',
			'numRetries' : 3,
			'failureSleepMS' : 5000]
	
		@Override
		public Object getApplicationParameter(Object propertyName)
				throws ScraperConfigServicePropertyMissing {
			if (params[propertyName] == null)
				throw new IllegalArgumentException('Property name not registered with mock ' + propertyName)
			return params[propertyName];
		}

		@Override
		public void setApplicationParameter(Object propertyName,
				Object propertyValue) {
			throw new NotImplementedException('not supported')			
		}
	}
}
