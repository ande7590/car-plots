package com.carplots.service.analysis;

import groovy.json.JsonBuilder;

import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.carMeta.entities.CarModel;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.Location;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.persistence.scraper.entities.Search;

public interface CarplotsAnalysisService {	

	Collection<MakeModel> getMakeModels();
	Collection<Location> getSearchLocations();
	Collection<ScraperRun> getScraperRuns();
	
	Object[] getImported(long makeModelId) throws Exception;
	
	Iterator<CarModel> iterateCarModel(String carModelQuery);
	
	long getNearestEngineId(Imported imported);
	
	void setDocumentStore(String documentStoreURL);
	String updateDocument(String document);	
	String createDocument(String document);
	
	void fixEngines();
	void clear();
}
