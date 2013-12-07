package com.carplots.persistence.scraper.dao;

import java.util.Iterator;

import com.carplots.persistence.dao.jpa.GenericJPADao;
import com.carplots.persistence.scraper.entities.Imported;

public interface ImportedDao extends GenericJPADao<Imported, Long> {
	Iterator<Imported> iterateByMakeModelId(long makeModelId);	
	Iterator<Imported> iterateByMakeModelId(long makeModelId, String zipcode);
	Iterator<Imported> iterateByMakeModelId(long makeModelId, long scraperRunId);
	Iterator<Imported> iterateByMakeModelId(long makeModelId, String zipcode, 
			long scraperRunId);
}
