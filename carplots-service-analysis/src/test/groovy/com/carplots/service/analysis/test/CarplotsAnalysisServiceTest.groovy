package com.carplots.service.analysis.test

import javax.persistence.EntityManager;

import spock.lang.Shared
import spock.lang.Specification
import com.carplots.service.analysis.CarplotsAnalysisService
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;
import com.carplots.service.analysis.module.AnalysisServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector

class CarplotsAnalysisServiceTest extends Specification {

	@Shared
	CarplotsAnalysisService carplotsAnalysisService
	
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new AnalysisServiceModule())
		carplotsAnalysisService = injector.getInstance(CarplotsAnalysisService.class)
	}
	
	def "test guice setup"() {
		expect:
		carplotsAnalysisService != null		
	}
}
