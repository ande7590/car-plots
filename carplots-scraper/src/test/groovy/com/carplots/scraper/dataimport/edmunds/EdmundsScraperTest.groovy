package com.carplots.scraper.dataimport.edmunds


import com.carplots.persistence.scraper.module.ScraperPersistenceInitializationService;
import com.carplots.scraper.config.ScraperConfigService;
import com.carplots.scraper.config.ScraperConfigServiceImpl;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawler
import com.carplots.scraper.test.CarsDotComTestModule
import com.carplots.scraper.test.EdmundsTestModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector

import javax.persistence.EntityManager

import spock.lang.Shared;
import spock.lang.Specification;

class EdmundsScraperTest extends Specification {
	
	@Shared
	EntityManager entityManager
	
	@Shared
	EdmundsRepository edmundsRepository
		
	def setupSpec() {
		Injector injector = Guice.createInjector(new EdmundsTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		entityManager = injector.getInstance(EntityManager.class)
		edmundsRepository = injector.getInstance(EdmundsRepository.class)
	}
	
	def setup() {
		entityManager.getTransaction().begin()
	}
	
	def cleanup() {
		entityManager.getTransaction().rollback()
	}
			
	def "test guice setup"() {
		expect:
		entityManager != null
		edmundsRepository != null
	}
	
	def 'test get make repository'() {
		when:
		def requestData = edmundsRepository.getMakeData('acura') 
		then:
		requestData != null && requestData.size() > 10
		
		when:
		requestData = edmundsRepository.getMakeModelData('acura', 'ilx')
		then:
		requestData != null && requestData.size() > 10
	}
	
}
