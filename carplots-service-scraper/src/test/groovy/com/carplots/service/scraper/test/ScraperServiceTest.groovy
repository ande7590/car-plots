package com.carplots.service.scraper.test

import com.carplots.service.scraper.CarplotsScraperService;
import com.carplots.service.scraper.module.ScraperServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import spock.lang.Shared;
import spock.lang.Specification;

class ScraperServiceTest extends Specification {

	@Shared
	CarplotsScraperService carplotsScaperService
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new ScraperServiceModule())		
		carplotsScaperService = injector.getInstance(CarplotsScraperService.class)
	}
	
	def "test guice module"() {
		expect:
		carplotsScaperService != null
	}
		
}
