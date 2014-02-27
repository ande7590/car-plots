package com.carplots.scraper.dataimport.edmunds

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carplots.scraper.dataimport.edmunds.EdmundsRepository;
import com.carplots.scraper.test.module.EdmundsJSONFetchModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

class EdmundsImporter {

	//corresponds to number of running FireFox instances used to scrape
	final static def numScraperThreads = 3
	//stagger the start of threads since we need to load FireFox
	final static def scraperThreadStartDelay = 30000
	
	static Logger logger = LoggerFactory.getLogger(EdmundsImporter.class)

	void run() {
		
		//load dependency injection framework
		Injector injector = Guice.createInjector(new EdmundsJSONFetchModule())		
		EdmundsRepository repo = injector.getInstance(EdmundsRepository.class)
		
		//scrape stuff from the web into disk caches
		populateCache(repo)		
		
		//create SQL from scraped data
	}
	
	static main(args) {
		logger.debug('Starting program')
		(new EdmundsImporter()).run()
		logger.debug('Finishing program')
	}

			
	//create SQL to insert into the DB (should have run populateCache first)
	void buildSQL(final EdmundsRepository repo) {
		StringBuilder sb = new StringBuilder()
		def importKey = 0
		EdmundsRepositoryImpl.edmundsMakesJSON.each { makeName ->
			def jsonData = repo.getMakeJSON(makeName)
			def models = jsonData["models"]
			jsonData["models"].each { modelDataKey, modelData ->
				if (!modelDataKey.contains(':') || modelDataKey.endsWith(':')) {
					modelDataKey = '${modelDataKey}:none'
				}
				try {
					def (modelDesc, bodyDesc) = modelDataKey.split(':')
					modelDesc = cleanStr(modelDesc)
					bodyDesc = cleanStr(bodyDesc)
					def modelName = cleanStr(modelData["modelname"])
					def submodel = cleanStr(modelData["submodel"])
					def link = cleanStr(modelData["link"])
					def bodyTypes = modelData['bodytypes']
					def years = modelData['years']
					bodyTypes.each { bodyType ->
						bodyType = cleanStr(bodyType)
						years["USED"].each { yr ->
							sb.append("INSERT INTO Edmunds.Imported (ImportKey, MakeName, ModelName, Year, SubModelDesc, EdmundsLink, BodyDesc) VALUES (${importKey}, '${makeName}', '${modelName}', '${yr}', '${submodel}', '${link}', '${bodyType}');\n");
							try {
								def linkParts = link.split('/')
								def makeData = repo.getMakeModelYearJSON(linkParts[1], linkParts[2], "$yr" as String)
							} catch (Exception ex) {
								logger.error('Failed to fetch meta data', ex)
							}
							importKey++
						}
					}
				} catch (Exception ex) {
					logger.error('Edmunds fetch error', ex)
				}
			}
		}
		
		def makeDataOutputSQL = new File('/home/mike/.devTest/edmunds_make_data.sql')
		if (makeDataOutputSQL.exists()) {
			makeDataOutputSQL.delete()
		}
		makeDataOutputSQL.append(sb.toString())
		def metaDataOutputSQL = new File('/home/mike/.devTest/edmunds_meta_data.sql')
	}
	
	//fetch everything to populate the filesystem cache, we can do 
	//something useful later much faster since all the data resides
	//on disk and doesn't need to be scraped
	void populateCache(final EdmundsRepository repo) {
		
		final Queue<Object> inputQ = new ConcurrentLinkedQueue<Object>()		
		
		//get make data, contains all car models and years along with basic
		//model data (e.g. bodyStyle), populate work queue to fetch detailed
		//data
		EdmundsRepositoryImpl.edmundsMakesJSON.each { makeName ->
			def jsonData = repo.getMakeJSON(makeName)
			def models = jsonData["models"]
			
			jsonData["models"].each { modelDataKey, modelData ->
				if (!modelDataKey.contains(':') || modelDataKey.endsWith(':')) {
					modelDataKey = '${modelDataKey}:none'
				}
				def (modelDesc, bodyDesc) = modelDataKey.split(':')
				modelDesc = cleanStr(modelDesc)
				bodyDesc = cleanStr(bodyDesc)
				def modelName = cleanStr(modelData["modelname"])
				def submodel = cleanStr(modelData["submodel"])
				def link = cleanStr(modelData["link"])
				def bodyTypes = modelData['bodytypes']
				def years = modelData['years']
				bodyTypes.each { bodyType ->
					bodyType = cleanStr(bodyType)
					years["USED"].each { yr ->
						                         
						inputQ.add([
							makeName: linkParts[1],
							modelName: linkParts[2],
							year: "$yr" as String						
						])
					}
				}
			}
		}
		
		//fetch detailed model data for each year, make, model, trim, and engine variant.
		final CountDownLatch numThreadsRunning = new CountDownLatch(numScraperThreads)		
		for (i in 1..numScraperThreads) {
			final def threadNum = i
			Thread.start {
				try {
					while(true) {
						def jsonData = inputQ.poll()
						if (jsonData == null) {
							logger.debug('Edmunds scraper ${i} done, no more items')
							break
						} else {
							def makeData = repo.getMakeModelYearJSON(
								jsonData['makeName'], jsonData['modelName'], jsonData['year'])
						}
					}
				}
				catch (Exception ex) {
					logger.error('Worker ${i} caught exception', ex)
				}
				finally {
					numThreadsRunning.countDown()
				}
			}
			//stagger thread starts			
			Thread.sleep(scraperThreadStartDelay)
		}
		
		numThreadsRunning.await()
		logger.info('All threads finished populating cache')
	}
	
	def cleanStr(def str) {
		return (str != null)? str.replace("'", "").replace("\\", "") : "";
	}
}
