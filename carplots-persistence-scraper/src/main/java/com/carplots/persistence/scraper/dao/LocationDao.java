package com.carplots.persistence.scraper.dao;

import java.util.Iterator;

import com.carplots.persistence.dao.jpa.GenericJPADao;
import com.carplots.persistence.scraper.entities.Location;

public interface LocationDao extends GenericJPADao<Location, Long> {
	Iterator<Location> iterateSearchLocations();
}
