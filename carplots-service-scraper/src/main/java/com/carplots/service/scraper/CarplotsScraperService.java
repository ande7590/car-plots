package com.carplots.service.scraper;

import java.util.Collection;
import java.util.Iterator;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.entities.ScraperBatch;
import com.carplots.persistence.scraper.entities.ScraperRun;
import com.carplots.persistence.scraper.entities.Search;

public interface CarplotsScraperService {
	Collection<ScraperBatch> getScraperBatches();
	Iterator<Search> iterateScraperSearchBatch(long scraperBatchId);
	void addScraperRun(ScraperRun scraperRun);
	void updateScraperRun(ScraperRun scraperRun);
	void addImported(Imported record);
	void addImported(Collection<Imported> records);
}
