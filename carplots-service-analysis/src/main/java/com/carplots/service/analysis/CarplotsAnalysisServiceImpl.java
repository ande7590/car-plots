package com.carplots.service.analysis;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

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

	@Inject
	MakeModelDao makeModelDao;
	
	@Inject
	LocationDao locationDao;
	
	@Inject
	ImportedDao importedDao;
	
	@Inject
	ScraperRunDao scraperRunDao;
	
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
}
