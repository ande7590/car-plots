package com.carplots.service.analysis;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.carplots.common.interfaces.InitializationService;
import com.carplots.common.module.CarMeta;
import com.carplots.common.module.Scraper;
import com.carplots.common.utilities.StringMatchUtility;
import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.dao.CarTrimDao;
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
import com.carplots.service.documentStore.DocumentStore;
import com.carplots.service.documentStore.DocumentStore.DocumentStoreException;
import com.carplots.service.documentStore.DocumentStoreCouchDBStringImpl;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class CarplotsAnalysisServiceImpl implements CarplotsAnalysisService {
	
	private static final int initialArraySize = 4096;
	
	private DocumentStore<String, String> docStore = null;	
	private final Map<Long, Search> searchMap = new HashMap<Long, Search>();
	
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
	CarTrimDao carTrimDao;
	
	@Inject
	SearchDao searchDao;		
	
	@Inject
	public CarplotsAnalysisServiceImpl(@CarMeta final InitializationService carMetaInitSvc, 
			@Scraper final InitializationService scraperInitSvc) throws Exception {
		carMetaInitSvc.start();
		scraperInitSvc.start();		
	}
	
	@Override
	@Transactional
	public Collection<MakeModel> getMakeModels() {				
		return asNewLinkedList(makeModelDao.iterateAll());
	}

	@Override
	@Transactional
	public Collection<Location> getSearchLocations() {
		return asNewLinkedList(locationDao.iterateSearchLocations());
	}

	@Override
	@Transactional
	public Collection<ScraperRun> getScraperRuns() {
		return asNewLinkedList(scraperRunDao.iterateAll());
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
	
	@Override
	public String updateDocument(String document) {		
		String message = "ERROR: <null>";
		try {
			checkDocStore();
			message = docStore.updateDocument(document);
		} catch (Exception e) {
			message = "ERROR: " + 
					((e.getMessage() == null)? "<null>" : e.getMessage());
		}
		return message;
	}

	@Override
	public String createDocument(String document) {
		String message = "ERROR: <null>";
		try {
			checkDocStore();
			message = docStore.createDocument(document);
		} catch (Exception e) {
			message = "ERROR: " + 
					((e.getMessage() == null)? "<null>" : e.getMessage());
		}
		return message;
	}
	
	private void checkDocStore() {
		if (docStore == null) 
			throw new RuntimeException("Must set document store");
	}

	@Override
	public void setDocumentStore(String documentStoreURL) {
		docStore = new DocumentStoreCouchDBStringImpl(documentStoreURL);		
	}
	
	@Override
	public void clear() {
		makeModelDao.clear();
	}

	@Transactional
	@SuppressWarnings("deprecation")
	@Override
	public Object[] getImported(long makeModelId) throws Exception {
		int arraySize = initialArraySize;
		
		final Iterator<Imported> iterator = importedDao.iterateByMakeModelId(makeModelId);
				
		final Map<Long, ScraperRun> scraperRunLookup = new HashMap<Long, ScraperRun>();
		for (ScraperRun run : getScraperRuns()) {
			scraperRunLookup.put(run.getScraperRunId(), run);
		}
		
		Object[] buffer = new Object[initialArraySize];
		final Calendar cal = Calendar.getInstance();
		int count = 0;
		while (iterator.hasNext()) {
			if (count == Integer.MAX_VALUE) {
				throw new Exception("Integer overflow, too much data");
			}
			if (count == arraySize) {
				if (arraySize >= Integer.MAX_VALUE >> 1) {
					arraySize = Integer.MAX_VALUE;
				}
				else {
					arraySize *= 2;
				}				
				Object[] newBuffer = new Object[arraySize];
				System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
				buffer = newBuffer;
			}			
			final Imported i = iterator.next();
			final long importedId = i.getImportedId();
			final Long listingId = i.getListingId();
			final Integer miles = i.getMiles();
			final Integer price = i.getPrice();
			final Integer carYear = i.getCarYear();
			final long nearestEngineId = getNearestEngineId(i);
			final Long scraperRunId = i.getScraperRunId();
			final ScraperRun scraperRun = scraperRunLookup.get(scraperRunId);
						
			if (listingId != null && miles != null && price != null && carYear != null && 
					scraperRun != null) {				
				cal.setTime(scraperRun.getScraperRunDt());
				buffer[count] = new long[]{
						importedId,
						listingId,
						(long)miles,
						(long)price,
						(long)carYear,
						nearestEngineId,
						cal.get(Calendar.YEAR),
						(cal.get(Calendar.MONTH)) / 4
				};		
			}				
			count++;
			if (count % 1000 == 0) {
				System.out.format("Processed %d\n", count);
			}
		}
		
		//R chokes on null values and we need to use lapply() to combine results.		
		final Object[] returnVal = new Object[count];
		System.arraycopy(buffer, 0, returnVal, 0, count);
		
		return returnVal;
	}	
	
	private long doGetNearestEngineId(Imported imported) {
		CarEngine nearestEngine = null;
		
		final Search search = getSearchMap().get(imported.getSearchId());
				
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
			final List<CarEngine> finalEngineCandidates = new LinkedList<CarEngine>();
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
				Collections.sort(finalEngineCandidates, new Comparator<CarEngine>() {
					@Override
					public int compare(CarEngine o1, CarEngine o2) {																		
						return (o1.getCarEngineId() < o2.getCarEngineId())?
								1  : (o1.getCarEngineId() > o2.getCarEngineId())?
								-1 : 0;
					};
				});
								
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
	

	//guava apparently doesn't have this...
	private static <E> LinkedList<E> asNewLinkedList(final Iterator<? extends E> iter) {
		final LinkedList<E> linkedList = new LinkedList<E>();
		while (iter.hasNext()) {
			linkedList.add(iter.next());
		}
		return linkedList;
	}
	
	@Transactional
	private Map<Long, Search> getSearchMap() {
		if (searchMap.size() == 0) {
			System.out.println("Building search map");
			final Iterator<Search> iter = searchDao.iterateAll();
			while (iter.hasNext()) {
				final Search next = iter.next();
				searchMap.put(next.getSearchId(), next);
			}			
		}		
		return searchMap;
	}

	@Override
	public void fixEngines() {
		
		final Map<String, CarEngine> uniqueEngines = 
				new HashMap<String, CarEngine>();
		final Map<Long, List<String>> engineMap = new HashMap<Long, List<String>>();
		final Iterator<CarModel> iter = carModelDao.iterateAll();		
				
		while (iter.hasNext()) {
			
			final CarModel carModel = iter.next();
			final Long makeModelId = carModel.getMakeModelId();
						
			if (makeModelId == null) continue;
			
			for (CarTrim trim : carModel.getTrims())
			{
				for (CarEngine engine : trim.getEngines())
				{				
					final String engineDesc = getEngineDesc(makeModelId, engine);
					if (!uniqueEngines.containsKey(engineDesc)) {
						uniqueEngines.put(engineDesc, engine);
					}
					final long carTrimId = trim.getCarTrimId();
					if (!engineMap.containsKey(carTrimId)) {
						engineMap.put(carTrimId, new LinkedList<String>());
					}
					engineMap.get(carTrimId).add(engineDesc);					
				}
			}
			System.out.println(String.format("%s", carModel.getCarModelId()));						
		}
		
		PrintWriter writer;
		try {
			writer = new PrintWriter("/tmp/query.sql", "UTF-8");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		
		for (Long carTrimId : engineMap.keySet()) {
			final CarTrim carTrim = carTrimDao.findByPk(carTrimId);
			final List<String> engines = engineMap.get(carTrimId);
			for (String engineDesc : engineMap.get(carTrimId)) {
				writer.println(String.format(
						"INSERT CarMetaDev.CarTrimEngine (CarTrimID, CarEngineID) " + 
						  "VALUES (%s, %s);", carTrim.getCarTrimId(), 
						 uniqueEngines.get(engineDesc).getCarEngineId()));
			}			
		}
	}
	
	private String getEngineDesc(long makeModelId, CarEngine engine) {
		return String.format("%s_%s_%s_%s", makeModelId, engine.getCylinders(), engine.getDisplacementCC(), engine.getHorsepower());
	}
}
