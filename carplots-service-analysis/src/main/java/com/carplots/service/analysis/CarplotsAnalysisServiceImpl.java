package com.carplots.service.analysis;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.common.module.CarMeta;
import com.carplots.common.module.Scraper;
import com.carplots.common.utilities.StringMatchUtility;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.entities.CarEngine;
import com.carplots.persistence.carMeta.entities.CarModel;
import com.carplots.persistence.carMeta.entities.CarTrim;
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.LocationDao;
import com.carplots.persistence.scraper.dao.MakeModelDao;
import com.carplots.persistence.scraper.dao.ScraperRunDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.Location;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.persistence.scraper.entities.Search;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class CarplotsAnalysisServiceImpl implements CarplotsAnalysisService {

	private final static String engineCleanRegex = "[^0-9.]";
	private final static Integer engineSizeToleranceCC = 100; 	//Cubic centimeters
	private final static long invalidEngineId = -1l;
	
	@Inject
	MakeModelDao makeModelDao;
	
	@Inject
	LocationDao locationDao;
	
	@Inject
	ImportedDao importedDao;
	
	@Inject
	ScraperRunDao scraperRunDao;
	
	@Inject
	CarModelDao carModelDao;
	
	@Inject
	SearchDao searchDao;
	
	@Inject
	public CarplotsAnalysisServiceImpl(@CarMeta final InitializationService carMetaInitSvc, 
			@Scraper final InitializationService scraperInitSvc) throws Exception {
		carMetaInitSvc.start();
		scraperInitSvc.start();		
	}
	
	@Override
	public Collection<MakeModel> getMakeModels() {				
		return asNewLinkedList(makeModelDao.iterateAll());
	}

	@Override
	public Collection<Location> getSearchLocations() {
		return asNewLinkedList(locationDao.iterateSearchLocations());
	}

	@Override
	public Collection<ScraperRun> getScraperRuns() {
		return asNewLinkedList(scraperRunDao.iterateAll());
	}

	@Override
	public Iterator<Imported> iterateImported(long makeModelId) {
		return importedDao.iterateByMakeModelId(makeModelId);
	}

	@Override
	public Iterator<Imported> iterateImported(long makeModelId, String zipcode) {
		return importedDao.iterateByMakeModelId(makeModelId, zipcode);
	}

	@Override
	public Iterator<Imported> iterateImported(long makeModelId,
			long scraperRunId) {
		return importedDao.iterateByMakeModelId(makeModelId, scraperRunId);
	}

	@Override
	public Iterator<Imported> iterateImported(long makeModelId, String zipcode,
			long scraperRunId) {
		return importedDao.iterateByMakeModelId(makeModelId, zipcode, scraperRunId);
	}
	
	//guava apparently doesn't have this...
	private static <E> LinkedList<E> asNewLinkedList(final Iterator<? extends E> iter) {
		final LinkedList<E> linkedList = new LinkedList<E>();
		while (iter.hasNext()) {
			linkedList.add(iter.next());
		}
		return linkedList;
	}

	@Override
	public Iterator<CarModel> iterateCarModel(String carModelQuery) {
		return carModelDao.iterateCarModels(carModelQuery);
	}

	@Override
	public long getNearestEngineId(Imported imported) {
		long nearestEngineId = invalidEngineId;
		try {
			nearestEngineId = doGetNearestEngineId(imported);
		} catch (Exception ex) {			
			System.out.println("ERROR GETTING NEAREST ENGINE: " + 
					((imported == null)? "NULL" : imported.getImportedId()));
		}		
		return nearestEngineId;
	}
	
	private long doGetNearestEngineId(Imported imported) {
		CarEngine nearestEngine = null;
		final Search search = searchDao.findByPk(imported.getSearchId());
				
		if (imported.getEngine() != null && search != null && search.getSearchId() > 0) {
			final CarModel carModel = new CarModel();
			carModel.setYear(imported.getCarYear());
			carModel.setMakeModelId(search.getMakeModel().getMakeModelId());
			
			final String[] cleanStringParts = imported.getEngine().replaceAll(engineCleanRegex, " ")
					.split(" ");
			final Collection<Integer> engineSizeCandidates = new LinkedList<Integer>();
			for (String s : cleanStringParts) {
				try {
					final double d = Double.parseDouble(s);
					//convert Liters to CCs
					if (d < 1000) {
						engineSizeCandidates.add((int)(d * 1000d));
					}
					else {
						engineSizeCandidates.add((int)d);
					}
				} catch (NumberFormatException ex) {}
			}
			
			final List<CarModel> models = carModelDao.findByExample(carModel);
			final Collection<CarEngine> finalEngineCandidates = new LinkedList<CarEngine>();
			String engineDesc = null; 
			boolean multipleCandidates = false;
			
			//try to find a match for the engine by comparing displacment
			if (!models.isEmpty() && !engineSizeCandidates.isEmpty())
			{								
				for (CarModel model : models)
				{
					final List<CarTrim> trims = model.getTrims();
					for (CarTrim trim : trims)
					{
						final Collection<CarEngine> engines = trim.getEngines();
						for (CarEngine engine : engines) {							
							final Integer displacement = engine.getDisplacementCC();							
							for (Integer i : engineSizeCandidates)
							{
								int epsilon = Math.abs(displacement - i);
								if (epsilon <= engineSizeToleranceCC) {
									finalEngineCandidates.add(engine);
									if (engine.getDescription() != null && engineDesc != null && 
											!engine.getDescription().equals(engineDesc))
									{
										//we only care if the engine descriptions are different
										multipleCandidates = true;										
									}
									else {
										engineDesc = engine.getDescription();
									}
								}
							}							
						}
					}
				}
			}
			
			//displacement tie-breaker, (e.g. Turbo variants, etc)
			if (!finalEngineCandidates.isEmpty()) {
				if (multipleCandidates) {					
					int minimumDistance = Integer.MAX_VALUE;
					nearestEngine = null;
					for (CarEngine e : finalEngineCandidates) {
						int dist = StringMatchUtility.getLevenshteinDistance(e.getDescription(), imported.getEngine());
						if (dist < minimumDistance) {
							minimumDistance = dist;
							nearestEngine = e;
						}
					}					
				} else {
					nearestEngine = finalEngineCandidates.iterator().next();
				}
			}
		}
		
		return (nearestEngine == null)? invalidEngineId : nearestEngine.getCarEngineId();
	}
	
}
