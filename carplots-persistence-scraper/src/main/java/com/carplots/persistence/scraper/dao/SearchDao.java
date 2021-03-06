package com.carplots.persistence.scraper.dao;

import java.util.Iterator;

import com.carplots.persistence.dao.jpa.GenericJPADao;
import com.carplots.persistence.scraper.entities.Search;

public interface SearchDao 
	extends GenericJPADao<Search, Long> {
	
	Iterator<Search> iterateByScraperBatchId(long scraperBatchId);
	
}
