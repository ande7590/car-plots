package com.carplots.scraper.dataimport.carsDotCom


import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.Search
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData
import com.carplots.scraper.test.CarsDotComTestModule
import com.google.inject.Guice;
import com.google.inject.Injector
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import spock.lang.Shared;
import spock.lang.Specification;

class CarsDotComScraperTest extends Specification {

/*	final static Logger logger = LoggerFactory.getLogger(CarsDotComScraperTest.class)
	
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
		
		when: 'make sure that data is read properly by inspecting each field'
		def is = this.getClass().getClassLoader().getResourceAsStream(
			'scraper_data/page1.html')
		def page = IOUtils.toString(is)	
		def search = new Search()
		search.setSearchId(999l)
		def data = new CarsDotComCrawlerData(search, [page])
		Collection<Imported> out = scraper.getImported(data)
	
		then: 'verify that the correct number of entities was parsed'
		
		//one item is unparsable due to bad price field
		out.size() == 7
		
		//ideal case, all fields populated
		out[0].listingId == 122487607
		out[0].miles == 20650
		out[0].carYear == 2010
		out[0].price == 14495
		out[0].bodyStyle == "Wagon"
		out[0].carName == "Chrysler PT Cruiser Classic"
		out[0].color == "Brilliant Black Crystal Pearlcoat"
		out[0].dealerPhone == "8883574505"
		out[0].engine == "24L I4 16V MPFI DOHC"
		out[0].errFlg == "0"
		out[0].sellerName == "Waconia Dodge Chrysler Jeep RAM"
		out[0].sellerType == "unknown"
		
		//an element (dealer) is removed from the page, ensure that
		//the errFlg reflects this.
		out[1].listingId == 112986303
		out[1].miles == 52595
		out[1].carYear == 2007
		out[1].price == 10995
		out[1].bodyStyle == "Convertible"
		out[1].carName == "Chrysler PT Cruiser Touring"
		out[1].color == "Inferno Red Crystal Pearl"
		out[1].dealerPhone == "8667582880"
		out[1].engine == "24L I4 16V MPFI DOHC Turbo"
		out[1].errFlg == "1"
		out[1].sellerType == "unknown"
		out[1].sellerName == ''
		
		//test a minimal success case, i.e. one that
		//only has price, year, miles, listingId 		
		out[6].listingId == 116810341
		out[6].miles == 50391
		out[6].price == 8977
		out[6].carYear == 2008
		out[6].carName == 'Chrysler PT Cruiser Touring'
		out[6].errFlg == '1'
		out[6].engine == ''
		out[6].sellerName == ''
		out[6].color == ''
		out[6].bodyStyle == ''
		out[6].dealerPhone == ''			
		
		
	}*/
	
}
