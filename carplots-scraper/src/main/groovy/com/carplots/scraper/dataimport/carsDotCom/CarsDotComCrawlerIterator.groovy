package com.carplots.scraper.dataimport.carsDotCom

import java.util.regex.Matcher
import java.util.regex.Pattern
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carplots.persistence.scraper.dao.ScraperBatchSearchDao;
import com.carplots.persistence.scraper.entities.MakeModel
import com.carplots.persistence.scraper.entities.ScraperBatch;
import com.carplots.persistence.scraper.entities.Search;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComRepository.CarsDotComRepositoryFetchException;
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Inject;

class CarsDotComCrawlerIterator 
	implements Iterator<CarsDotComCrawlerData> {
		
	private static final Logger logger = LoggerFactory.getLogger(CarsDotComCrawlerIterator.class)
		
	private final CarsDotComRepository carsDotComRepo
	private final CarplotsScraperService scraperService
	private final Iterator<Search> searchIter
	
	private boolean fetchedNext = false
	private boolean hasNext = false
	private CarsDotComCrawlerData next = null
	
	CarsDotComCrawlerIterator(final long scraperBatchId, 
		final CarsDotComRepository carsDotComRepo, 
		final CarplotsScraperService scraperService) {		
		this.carsDotComRepo = carsDotComRepo
		this.scraperService = scraperService		
		this.searchIter = scraperService.iterateScraperSearchBatch(scraperBatchId)
	}
	
	private void fetchNext() {
		hasNext = searchIter.hasNext()
		fetchedNext = true;
		if (hasNext) {
			Search search = searchIter.next()
			def pages = []			
			def totalPages = 1
			//fetch all pages for this search (i.e. results are pagenated across
			//several pages)
			for (def pageNum = 1; pageNum <= totalPages; pageNum++) {
				MakeModel mm = search.getMakeModel()
				def (makeId, modelId, radius, zipcode) = [
					 mm.makeId, mm.modelId, search.radius,
					 search.getLocation().getZipcode()]
				try {
					def pageHtml = carsDotComRepo.getSummaryPageHtml(makeId, modelId,
						zipcode, radius, pageNum)
					pages.add(pageHtml)
					//update the page count with data from first fetch
					if (pageNum == 1) {
						totalPages = getTotalPages(pageHtml)
					}
				} catch (CarsDotComRepositoryFetchException ex ) {
					logger.warn("Unable to fetch make: ${makeId}, model: ${modelId}, zipcode: ${zipcode}")
					break;
				}
			}
			next = new CarsDotComCrawlerData(search, pages)
		}
	}
	
	/*
	 * Regex the total number of pages out of the pageHtml.
	 * All comments refer to the example given about regex string.
	 */
	private int getTotalPages(String pageHtml) {
		
		int totalPages = 1
		
		//look for something like (for example) '<strong>(1-50)</strong> of 340 Vehicles"',		
		Pattern regex = 
			Pattern.compile("(\\d+\\s*-\\s*\\d+)\\D*?of\\s+(\\d+)\\s+vehicles", 
				Pattern.DOTALL | Pattern.CASE_INSENSITIVE)		
		Matcher regexMatcher = regex.matcher(pageHtml)
				
		if (regexMatcher.find()) {			
			//e.g. '1-50' 
			def currentRecordRange = regexMatcher.group(1) 	
			//split '1-50' into [1, 50]
			def parts = (currentRecordRange.replaceAll(/\s/, '')) =~ /\d+/ 
			//figure out how many records are on this page
			def recordsPerPage = ((parts[1] as int) - (parts[0] as int)) + 1 			
			//figure out how many records there are to be show total, e.g. '340'
			def totalRecords = (regexMatcher.group(2).replaceAll(/\D/, "")
				) as int 						
			totalPages = Math.ceil(totalRecords / recordsPerPage)
		}
		return totalPages 
	}

	@Override
	public boolean hasNext() {
		if (!fetchedNext) {
			fetchNext();
		}
		return hasNext
	}

	@Override
	public CarsDotComCrawlerData next() {
		fetchedNext = false
		return next
	}

	@Override
	public void remove() {
		throw new NotImplementedException("This method is not implemented")
	}
	
	static class CarsDotComCrawlerData {
		final Search search;
		final Collection<String> pages;
		
		CarsDotComCrawlerData(Search search, Collection<String> pages) {
			this.search = search
			this.pages = pages
		}
		
		public Collection<String> getPages() {
			return pages
		}
		
		public Search getSearch() {
			return search
		}
	}
}
