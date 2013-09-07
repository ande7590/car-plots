package com.carplots.persistence.scraper.dao.hibernate;

import java.util.Iterator;

import org.hibernate.Query;

import com.carplots.persistence.dao.hibernate.AbstractHibernateDao;
import com.carplots.persistence.dao.hibernate.ScrollableResultsIterator;
import com.carplots.persistence.scraper.dao.LocationDao;
import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.persistence.scraper.entities.Location;

public class LocationDaoHibernateImpl extends
		AbstractHibernateDao<Location, Long> implements
		LocationDao {

	@Override
	public Iterator<Location> iterateSearchLocations() {
		
		//only return locations that we've actually searched on
		final String modelIdQuery = 
				"SELECT loc.* FROM Location loc WHERE ScraperSearch = 1";
		
		final Query query = getSession().createSQLQuery(modelIdQuery)
				.addEntity(Location.class);
		
		final Iterator<Location> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}

}
