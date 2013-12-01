package com.carplots.scraper.dataimport.carsDotCom

import javax.persistence.EntityManager;
import java.util.regex.Matcher
import java.util.regex.Pattern


import com.carplots.persistence.scraper.module.ScraperPersistenceInitializationService;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;
import com.carplots.scraper.test.CarsDotComTestModule
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector

import spock.lang.Shared;
import spock.lang.Specification

class CarsDotComCrawlerIteratorTest extends Specification {
	
	@Shared
	EntityManager entityManager
	
	@Shared
	CarsDotComCrawler crawler
	
	@Shared
	CarsDotComRepository repo
		
	def setupSpec() {
		Injector injector = Guice.createInjector(new CarsDotComTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		entityManager = injector.getInstance(EntityManager.class)
		crawler = injector.getInstance(CarsDotComCrawlerImpl.class)			
		repo = injector.getInstance(CarsDotComRepository.class)	
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
	
	/*def "test iterator"() {
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
	
	def "test cached repository"() {
		final def MAKE_CHRYSLER = 20008
		final def MODEL_PT_CRUISER = 21744
		final def ZIPCODE_MPLS = 55423
		final  def RADIUS_DEFAULT = 30				
		
		when: "test a valid request that we would expect to return results"
		def requestHtml = repo.getSummaryPageHtml(
			MAKE_CHRYSLER, MODEL_PT_CRUISER, ZIPCODE_MPLS, RADIUS_DEFAULT, 0) as String
		then:
		isSummaryPageCorrect(requestHtml)
		
	}
	
	static boolean isSummaryPageCorrect(String htmlData) {
		//groovy regex is acting weird, eclipse may be using a buggy version because
		//the groovyConsole works just fine when the same data and regex are used (the groovy version of the regex)
		Pattern regex = Pattern.compile("\\s+chrysler\\s+pt\\s+cruiser", Pattern.DOTALL | Pattern.CASE_INSENSITIVE)
		Matcher regexMatcher = regex.matcher(htmlData)
		boolean result = regexMatcher.find()
		return result
	}	
}
