package com.carplots.scraper.dataimport.carsDotCom

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean

import org.slf4j.LoggerFactory;

import com.carplots.persistence.scraper.entities.Search;
import com.carplots.scraper.dataimport.DataImportManager;
import com.carplots.scraper.source.AbstractURLGenerator;
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Inject;
import com.sun.org.apache.xpath.internal.operations.String;

/**
 * Controls the threaded import process workers, coordinates cross-thread
 * data sharing between the workers. 
 * 	 Fetcher - CURLs for the HTML data
 * 	 Processor - Reads the HTML data into Imported records
 * 	 Emitter - Sends the Imported records to their destination
 * @author Mike Anderson
 */
class DataImportManagerCarsDotComImpl implements DataImportManager {
	
	final org.slf4j.Logger logger = LoggerFactory.getLogger(DataImportManagerCarsDotComImpl.class)
	
	@Inject
	CarplotsScraperService scraperService
	
	@Inject
	DataImportManagerImplConfiguration config
	
	DataImportManagerCarsDotComImpl() {		
		logger.debug("Constructing data fetcher for $scraperBatchId.", scraperBatchId)	
	}

	/**
	 * Configures the DataImportManager
	 */
	class DataImportManagerImplConfiguration {
		def scraperBatchId
	}		




	@Override
	public void importData() {
		// TODO Auto-generated method stub
		
	}
}
