package com.carplots.service.scraper;

import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.dao.MakeModelDao;
import com.carplots.persistence.scraper.dao.ScraperBatchDao;
import com.carplots.persistence.scraper.dao.ScraperRunDao;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.entities.ScraperBatch;
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.persistence.scraper.entities.Search;
import com.google.inject.Inject;

public class CarplotsScraperServiceImpl implements CarplotsScraperService {

	@Inject
	private ImportedDao importedDao;
	
	@Inject
	private SearchDao searchDao;
	
	@Inject
	private ScraperRunDao scraperRunDao;
	
	@Inject
	private ScraperBatchDao scraperBatchDao;
	
	
	@Override
	public Collection<ScraperBatch> getScraperBatches() {
		return scraperBatchDao.findByExample(new ScraperBatch());
	}

	@Override
	public Iterator<Search> iterateScraperSearchBatch(long scraperBatchId) {
		return searchDao.iterateByScraperBatchId(scraperBatchId);
	}

	@Override
	public void addScraperRun(ScraperRun scraperRun) {
		scraperRunDao.persist(scraperRun);
	}

	@Override
	public void updateScraperRun(ScraperRun scraperRun) {		
		scraperRunDao.merge(scraperRun);
	}

	@Override
	public void addImported(Imported imported) {		
		importedDao.persist(imported);
	}

	@Override
	public void addImported(Collection<Imported> importedRecords) {
		for (Imported i : importedRecords) {
			importedDao.persist(i);
		}
	}

}
