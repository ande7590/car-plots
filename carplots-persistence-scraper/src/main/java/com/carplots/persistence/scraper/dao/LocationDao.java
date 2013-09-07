package com.carplots.persistence.scraper.dao;

import java.util.Iterator;

import com.carplots.persistence.dao.GenericDao;
import com.carplots.persistence.scraper.entities.Location;

public interface LocationDao extends GenericDao<Location, Long> {
	Iterator<Location> iterateSearchLocations();
}
