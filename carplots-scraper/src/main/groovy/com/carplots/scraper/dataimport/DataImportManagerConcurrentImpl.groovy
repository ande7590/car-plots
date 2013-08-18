package com.carplots.scraper.dataimport

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch

import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawler;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;
import com.google.inject.Inject;

class DataImportManagerConcurrentImpl implements DataImportManager {

	@Override
	public void importData() {		
		
	}
	
	//Worker for the crawler
	static class CrawlerThread implements Runnable {
		
		@Inject
		CarsDotComCrawler crawler;
		
		final BlockingQueue<CarsDotComCrawlerData> outputQ
		final CountDownLatch crawlerLatch
		
		CrawlerThread(BlockingQueue<CarsDotComCrawlerData> outputQ, CountDownLatch crawlerLatch) {
			this.outputQ = outputQ
			this.crawlerLatch = crawlerLatch
		}
		
		@Override
		public void run() {
			for (CarsDotComCrawlerData crawlerData : crawler) {
				outputQ.put(crawlerData)
			}
			crawlerLatch.countDown()
		}		
	}
	
	//Worker for scraper and emitter
	static class ScraperEmitterThread implements Runnable {

		final BlockingQueue<CarsDotComCrawlerData> inputQ
		final CountDownLatch crawlerLatch
		final CountDownLatch scraperLatch		
		
		ScraperEmitterThread() {
			
		}
		
		@Override
		public void run() {			
			while(!inputQ.isEmpty() || crawlerLatch.getCount() > 0) {
				final Collection<CarsDotComCrawlerData> workItems = new LinkedList<CarsDotComCrawlerData>();
			}
			scraperLatch.countDown()
		}		
	}
}
