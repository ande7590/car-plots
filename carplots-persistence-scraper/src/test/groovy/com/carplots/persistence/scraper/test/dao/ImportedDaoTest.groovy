package com.carplots.persistence.scraper.test.dao

import javax.persistence.EntityManager;
import javax.swing.tree.FixedHeightLayoutCache.SearchInfo;

import com.carplots.common.utilities.DebugUtility;
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.dao.hibernate.ImportedDaoHibernateImpl;
import com.carplots.persistence.scraper.dao.hibernate.SearchDaoHibernateImpl
import com.carplots.persistence.scraper.entities.Imported
import com.carplots.persistence.scraper.entities.Search
import com.carplots.persistence.scraper.module.ScraperPersistenceInitializationService;
import com.carplots.persistence.scraper.test.ImportedDaoTestModule;
import com.google.inject.Inject;
import com.sun.xml.internal.ws.api.pipe.Engine;

import spock.lang.Specification
import spock.guice.UseModules
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import spock.lang.Shared;
import spock.lang.Specification;

class ImportedDaoTest extends Specification {	
	
	@Shared
	ImportedDao importedDao
	
	@Shared 
	EntityManager entityManager
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new ImportedDaoTestModule())
		ScraperPersistenceInitializationService persistInitSvc =
			injector.getInstance(ScraperPersistenceInitializationService.class)
		persistInitSvc.start()
		importedDao = injector.getInstance(ImportedDao.class)
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
		importedDao != null && importedDao instanceof ImportedDaoHibernateImpl		
		entityManager != null
	}
	
	def "test find by pk"() {
		when:
		def imported = importedDao.findByPk(1l)
		then:
		imported != null
		imported.importedId == 1
		imported.searchId > 0
		
	}
	
	def "test find by example"() {
		when:
		def found = importedDao.findByExample(new Imported().with {
			searchId = 15
			it
		})
		then:
		found.size() == 2				
	}
	
	def "test iterate"() {
		
		def collectIter = { iter ->
			def records = []
			while (iter.hasNext()) {
				def item = iter.next()
				records.add(item)
			}
			records
		}
		
		when: "iterate all"
		def all = collectIter(importedDao.iterateAll())				
		then:
		all.size() > 3
		all[0].importedId > 0 
		all[1].importedId > 0
		all[2].importedId > 0
		
		when: "iterate by example"
		def byExampleIter = importedDao.iterateByExample(new Imported().with {
			searchId = 15
			it
		})		
		def byExampleResults = collectIter(byExampleIter)
		then: 
		byExampleResults.size() == 2
		
		when: "iterate no results"
		def byExampleIterNoResults = importedDao.iterateByExample(new Imported().with {
			searchId = Long.MAX_VALUE
			it
		})
		def byExampleNoResults = collectIter(byExampleIterNoResults)
		then:
		byExampleNoResults.size() == 0
		
	}
	
	def "test insert/update/delete"() {
		when:
		def newImportedRecord = new Imported().with {
			searchId = 20
			engine = "a"
			dealerPhone = "b"
			carYear = 1
			errFlg = "c"
			color = "d"
			bodyStyle = "e"
			carName = "f"
			sellerType = "g"
			sellerName = "h"
			listingId = 2
			miles = 3
			scraperRunId = 4
			price = 5
			it
		}
		importedDao.persist(newImportedRecord)
		then:
		newImportedRecord.importedId > 0
	}
	
	
	
}
