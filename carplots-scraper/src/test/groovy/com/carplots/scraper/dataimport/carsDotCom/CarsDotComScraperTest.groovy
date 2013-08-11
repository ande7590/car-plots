package com.carplots.scraper.dataimport.carsDotCom

import com.carplots.persistence.ScraperPersistenceInitializationService
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData
import com.carplots.scraper.test.CarsDotComTestModule
import com.google.inject.Guice;
import com.google.inject.Injector
import javax.persistence.EntityManager;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import spock.lang.Shared;
import spock.lang.Specification;

class CarsDotComScraperTest extends Specification {

	final static Logger logger = LoggerFactory.getLogger(CarsDotComScraperTest.class)
	
	@Shared
	EntityManager entityManager
		
	@Shared
	CarsDotComScraper scraper;
		
	def setupSpec() {
		Injector injector = Guice.createInjector(new CarsDotComTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		entityManager = injector.getInstance(EntityManager.class)
		scraper = injector.getInstance(CarsDotComScraper.class)
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
		scraper != null
	}
	
	def "test scraper"() {
		
		when:
		def page = new File("/home/mike/Documents/dump2").readLines().join('\n');
		logger.warn(page)				
		def data = new CarsDotComCrawlerData(null, [page])
		def output = scraper.getImported(data)
		then:
		page.size() > 0
		
	}
	
}
