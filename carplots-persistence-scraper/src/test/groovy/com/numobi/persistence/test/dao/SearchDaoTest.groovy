package com.numobi.persistence.test.dao

import javax.persistence.EntityManager;

import spock.lang.Shared;
import spock.lang.Specification;

import com.carplots.persistence.ScraperPersistenceInitializationService
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.google.inject.Injector
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.numobi.persistence.test.ImportedDaoTestModule
import com.numobi.persistence.test.SearchDaoTestModule;

class SearchDaoTest extends Specification {
	
	@Shared
	SearchDao searchDao
	
	@Shared
	EntityManager entityManager
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new SearchDaoTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		searchDao = injector.getInstance(SearchDao.class)
		entityManager = injector.getInstance(EntityManager.class)
	}
	
	def setup() {
		entityManager.getTransaction().begin()
	}
	
	def cleanup() {
		entityManager.getTransaction().rollback()
	}
	
	def "test iterate"() {
		when:
		def iterator = searchDao.iterateByScraperBatchId(1);
		def results = []
		while (iterator.hasNext()) {
			results.add(iterator.next())
		}
		then:
		results.size() == 13	
	}
}
