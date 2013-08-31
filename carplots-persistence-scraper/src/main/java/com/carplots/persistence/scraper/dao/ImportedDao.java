package com.carplots.persistence.scraper.dao;

import java.util.Iterator;

import com.carplots.persistence.dao.GenericDao;
import com.carplots.persistence.scraper.entities.Imported;

public interface ImportedDao extends GenericDao<Imported, Long> {
	Iterator<Imported> iterateByMakeId(long makeId);
	Iterator<Imported> iterateByModelId(long modelId);
}
