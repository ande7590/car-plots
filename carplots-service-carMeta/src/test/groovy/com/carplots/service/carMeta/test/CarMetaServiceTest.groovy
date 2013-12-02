package com.carplots.service.carMeta.test

import com.carplots.service.carMeta.CarMetaService;
import com.carplots.service.carMeta.module.CarMetaServiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import spock.lang.Shared;
import spock.lang.Specification;

class CarMetaServiceTest extends Specification {

	@Shared
	CarMetaService carMetaService
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new CarMetaServiceModule())
		carMetaService = injector.getInstance(CarMetaService.class)
	}
	
	def "test guice module"() {
		expect:
		carMetaService != null
	}
	
}
