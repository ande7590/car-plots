package com.carplots.service.analysis.test

import javax.persistence.EntityManager;

import spock.lang.Shared
import spock.lang.Specification
import com.carplots.persistence.ScraperPersistenceInitializationService
import com.carplots.service.analysis.CarplotsAnalysisService
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Injector
import com.numobi.persistence.test.ImportedDaoTestModule

class CarplotsAnalysisServiceTest extends Specification {

	@Shared
	CarplotsAnalysisService analysisService
	
	@Shared
	EntityManager entityManager
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new CarplotsAnalysisServiceTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		analysisService = injector.getInstance(CarplotsAnalysisService.class)
		entityManager = injector.getInstance(EntityManager.class)
	}
	
	def setup() {
		entityManager.getTransaction().begin()
	}
	
	def cleanup() {
		entityManager.getTransaction().rollback()
	}
	
	def "test guice setup"() {
		expect:
		analysisService != null 
		analysisService instanceof CarplotsAnalysisServiceImpl		
	}
	
	def "test getter methods"() {
		
		final long MAKEMODELID = 371
		final long SCRAPERRUNID = 160
		final String ZIPCODE = '89146'
		final int SUCCESS_THRESHOLD = 3
		
		expect: 
		
		analysisService.getMakeModels().size() > 0
		analysisService.getScraperRuns().size() > 0
		analysisService.getSearchLocations().size() > 0
						
		hasAtLeast(SUCCESS_THRESHOLD,
			analysisService.iterateImported(MAKEMODELID))
		
		hasAtLeast(
			SUCCESS_THRESHOLD,
			analysisService.iterateImported(MAKEMODELID, SCRAPERRUNID))
				
		hasAtLeast(
			SUCCESS_THRESHOLD,
			analysisService.iterateImported(MAKEMODELID, ZIPCODE, SCRAPERRUNID))
		
		hasAtLeast(
			SUCCESS_THRESHOLD,
			analysisService.iterateImported(MAKEMODELID, ZIPCODE))
		
	}		
	
	static boolean hasAtLeast(int atLeast, Iterator iter) {
		int i = 0
		while (iter.hasNext()) {
			def obj = iter.next()
			i++
			if (i >= atLeast) return true
		}
		return false
	}
}
