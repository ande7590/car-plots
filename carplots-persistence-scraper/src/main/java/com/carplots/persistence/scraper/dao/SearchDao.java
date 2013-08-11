package com.carplots.persistence.scraper.dao;

import java.util.Iterator;

import com.carplots.persistence.dao.GenericDao;
import com.carplots.persistence.scraper.entities.Search;

public interface SearchDao 
	extends GenericDao<Search, Long> {
	
	Iterator<Search> iterateByScraperBatchId(long scraperBatchId);
	
}
