package com.carplots.scraper.dataimport.carsDotCom

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.common.exception.ApplicationConfigurationException;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.ScraperBatch
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.scraper.ScraperConfigService
import com.carplots.scraper.ScraperConfigService.ScraperConfigServicePropertyMissing;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.dataimport.ImportedEmitter;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Inject;

class DataImportManagerCarsDotComImpl implements DataImportManager {
	
	final static Logger logger = LoggerFactory.getLogger(DataImportManagerCarsDotComImpl.class)
	
	@Inject
	CarsDotComCrawler crawler
	
	@Inject
	CarsDotComScraper scraper
	
	@Inject
	ImportedEmitter emitter
	
	@Inject
	CarplotsScraperService scraperService
	
	@Inject
	ScraperConfigService configService
	
	@Inject
	DataImportManagerConcurrentImplConfiguration dataManagerConfig
	
	private ScraperRun scraperRun
		
	@Override
	public void importData() {
		
		logger.debug('Beginning data import')
					
		try {
			setupScraper()
			runScraper()
		} catch (Exception ex) {
			logger.error('Import exception', ex);
			isImportSuccessful = false;
		} finally {
			finishScraper()
		}
				
		logger.debug('Workers done')
	}
	
	private void setupScraper() {
		
		//retrieve the batch (data collection parameters),
		//and create a run for it (a record for this particular data collection)
		def scraperBatchId = getDataManagerConfig().scraperBatchId
		def scraperBatch = scraperService.getScraperBatches().find { batch ->
			batch.scraperBatchId == scraperBatchId
		}
		if (scraperBatch == null) {
			throw new ApplicationConfigurationException(
				"scraperBatchId '${scraperBatchId}', not found cannot start scraper.");
		}
		
		//create the DB record
		def scraperRun = new ScraperRun(
			scraperBatch: scraperBatch,
			scraperRunDt: new Date(),
			runCompleted: false)
		scraperService.addScraperRun(scraperRun)
		
		//publish the run information to the configuration service
		configService.setApplicationParameter('scraperRunId', scraperRun.scraperRunId as String)				
		this.scraperRun = scraperRun
	}
	
	private void finishScraper() {
		if (scraperRun == null) {
			logger.error('Finishing scraper with NULL scraperRun')
		} else {
			scraperRun.runCompleted = true
			scraperService.updateScraperRun(scraperRun)
		}
	}
	
	private void runScraper() {
		
		def config = dataManagerConfig
		
		//create an output queue for the crawler,
		//input queue for the scraper
		BlockingQueue<CarsDotComCrawlerData> crawlerOutputQ =
			new ArrayBlockingQueue<ConsumerWorkItem<CarsDotComCrawlerData>>(
				config.maxScraperQueueSize, true)
		
		//create an output queue for the scraper,
		//input queue for the emitter
		BlockingQueue<ConsumerWorkItem<CarsDotComCrawlerData>> scraperOutputQ =
			new ArrayBlockingQueue<CarsDotComCrawlerData>(
				config.maxEmitterQueueSize, true)
		
		AtomicBoolean abnormalTermination = new AtomicBoolean(false)
			
		def crawlerThread = new Thread(new CrawlerThreadWorker(crawler, crawlerOutputQ, abnormalTermination))
		def scraperThread = new Thread(new ScraperThreadWorker(scraper, crawlerOutputQ, scraperOutputQ,
			abnormalTermination))
		def emitterRunner = new EmitterThreadWorker(emitter, scraperOutputQ,
			abnormalTermination)
		
		//start 2 threads
		logger.debug('Starting threads')
		crawlerThread.start()
		scraperThread.start()

		//emit on the main thread
		emitterRunner.run()
		
		if (abnormalTermination.get() == true) {
			isImportSuccessful = false
		}
		else {
			isImportSuccessful = true
		}
	}
	
	private boolean isImportSuccessful = true
	private void setImportSuccessful(final boolean isImportSuccessful) {
		this.isImportSuccessful = isImportSuccessful
	}
	
	@Override
	public boolean isImportSuccessful() {
		return this.isImportSuccessful
	}
	
	//Config Object
	static class DataImportManagerConcurrentImplConfiguration {
		@Inject
		ScraperConfigService configService
					
		private int getMaxScraperQueueSize() {
			return configService.getApplicationParameter('maxScraperQueueSize') as int
		}
		
		private int getMaxEmitterQueueSize() {
			return configService.getApplicationParameter('maxEmitterQueueSize') as int
		}
		
		private int getScraperBatchId() {
			return configService.getApplicationParameter('scraperBatchId') as int			 
		}
	}

	
	//Worker for the crawler
	static class CrawlerThreadWorker implements Runnable {
		
		final static Logger logger = LoggerFactory.getLogger(CrawlerThreadWorker.class)
		
		final CarsDotComCrawler crawler
		final BlockingQueue<ConsumerWorkItem<CarsDotComCrawlerData>> outputQ
		final AtomicBoolean abnormalTermination
		
		CrawlerThreadWorker(
			final CarsDotComCrawler crawler,
			final BlockingQueue<CarsDotComCrawlerData> outputQ,
			final AtomicBoolean abnormalTermination) {
			this.crawler = crawler
			this.outputQ = outputQ
			this.abnormalTermination = abnormalTermination
		}
		
		@Override
		public void run() {
			logger.debug('Starting crawler')
			try {
				//crawl the website via iterator
				for (CarsDotComCrawlerData crawlerData : crawler) {
					logger.trace('Adding crawlerData item')
					outputQ.put(new ConsumerWorkItem<CarsDotComCrawlerData>(crawlerData, false))
				}
			}
			catch (Exception ex) {
				logger.error('Crawler caught exception.', ex)
				this.abnormalTermination.set(true)
			}
			finally {
				//signal end by adding sentinel
				def endSentinel = new ConsumerWorkItem<CarsDotComCrawlerData>(null, true)
				outputQ.put(endSentinel)
				logger.debug('done crawling')
			}
		}
	}
	
	//Worker for scraper
	static class ScraperThreadWorker implements Runnable {

		final static Logger logger = LoggerFactory.getLogger(ScraperThreadWorker.class)
				
		final CarsDotComScraper scraper
		final BlockingQueue<CarsDotComCrawlerData> inputQ
		final BlockingQueue<Imported> outputQ
		final AtomicBoolean abnormalTermination
		
		ScraperThreadWorker(
			final CarsDotComScraper scraper,
			final BlockingQueue<CarsDotComCrawlerData> inputQ,
			final BlockingQueue<Imported> outputQ,
			final AtomicBoolean abnormalTermination) {
			this.scraper = scraper
			this.inputQ = inputQ
			this.outputQ = outputQ
			this.abnormalTermination = abnormalTermination
		}
		
		@Override
		public void run() {
			def isDone = false
			try {
				while(!isDone) {
					ConsumerWorkItem<CarsDotComCrawlerData> producerData = inputQ.take()
					if (producerData.isEndSentinel == true) {
						logger.debug('Scraper done, no more items')
						isDone = true
					} else {
						logger.trace('Parsing crawler data')
						def crawlerData = producerData.getItem()
						scraper.getImported(crawlerData).each {imported ->
							outputQ.put(new ConsumerWorkItem<Imported>(imported, false))
						}
					}
				}
			}
			catch (Exception ex) {
				logger.error('ScraperThreadWorker caught exception', ex)
				this.abnormalTermination(true)
			}
			finally {
				ConsumerWorkItem<Imported> sentinel = new ConsumerWorkItem<Imported>(null, true)
				outputQ.add(sentinel)
			}
		}
	}
	
	//worker item for emitter
	static class EmitterThreadWorker implements Runnable {

		static final Logger logger = LoggerFactory.getLogger(EmitterThreadWorker.class)
				
		final ImportedEmitter emitter
		final BlockingQueue<ConsumerWorkItem<Imported>> inputQ
		final AtomicBoolean abnormalTermination
		
		EmitterThreadWorker(
			final ImportedEmitter emitter,
			final BlockingQueue<ConsumerWorkItem<Imported>> inputQ,
			final AtomicBoolean abnormalTermination) {
			this.emitter = emitter
			this.inputQ = inputQ
			this.abnormalTermination = abnormalTermination
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					ConsumerWorkItem<Imported> importedItemToEmit = inputQ.take()
					if (importedItemToEmit.isEndSentinel() == true) {
						break
					} else {
						emitter.emit(importedItemToEmit.getItem())
					}
				}
			}
			catch (Exception ex) {
				logger.error('Emitter thread caught exception', ex)
				abnormalTermination.set(true)
			}
		}
	}
	
	static class ConsumerWorkItem<T> {
		
		final T item
		final boolean isEndSentinel
		
		T getWorkItem() {
			return item
		}
		
		boolean isEndSentinel() {
			return isEndSentinel
		}
		
		ConsumerWorkItem(final T item, final boolean isEndSentinel) {
			if (isEndSentinel && item != null) {
				throw new IllegalArgumentException("item must be null if isEndSentinel is true")
			}
			this.item = item
			this.isEndSentinel = isEndSentinel
		}
	}
}
