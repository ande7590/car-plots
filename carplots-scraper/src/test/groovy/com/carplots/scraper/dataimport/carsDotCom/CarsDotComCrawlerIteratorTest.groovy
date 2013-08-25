package com.carplots.scraper.dataimport.carsDotCom

import javax.persistence.EntityManager;

import com.carplots.persistence.ScraperPersistenceInitializationService
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;
import com.carplots.scraper.test.CarsDotComTestModule
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector

import spock.lang.Shared;
import spock.lang.Specification

class CarsDotComCrawlerIteratorTest extends Specification {
	
	/*@Shared
	EntityManager entityManager
	
	@Shared
	CarsDotComCrawler crawler
		
	def setupSpec() {
		Injector injector = Guice.createInjector(new CarsDotComTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		entityManager = injector.getInstance(EntityManager.class)
		crawler = injector.getInstance(CarsDotComCrawlerImpl.class)				
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
		crawler != null
	}
	
	def "test iterator"() {
		when:
		def items = []		
		def num = 0
		for (CarsDotComCrawlerData data : crawler) {
			if (num > 3) {
				break
			}	
			items.add(data)		
			num++		
		}
		for (CarsDotComCrawlerData data : crawler) {
			if (num > 3) {
				break
			}
			items.add(data)
			num++
		}
		then:
		items.size() == 3
		items.collect { itm ->
			itm.pages.size() > 0 && itm.search != null
		} == [true] * items.size()
		
	}*/
}
