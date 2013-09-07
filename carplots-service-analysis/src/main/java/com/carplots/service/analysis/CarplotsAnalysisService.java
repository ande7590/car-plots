package com.carplots.service.analysis;

import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.Location;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.persistence.scraper.entities.Search;

public interface CarplotsAnalysisService {	

	Collection<MakeModel> getMakeModels();
	Collection<Location> getSearchLocations();
	Collection<ScraperRun> getScraperRuns();
	
	Iterator<Imported> iterateImported(long makeModelID);
	Iterator<Imported> iterateImported(long makeModelID, String zipcode);
	Iterator<Imported> iterateImported(long makeModelID, long scraperRunId);
	Iterator<Imported> iterateImported(long makeModelID, 
			String zipcode, long scraperRunId);
}
