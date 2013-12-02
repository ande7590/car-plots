package com.carplots.persistence.scraper.dao.hibernate;

import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;

import com.carplots.persistence.dao.hibernate.AbstractHibernateDao;
import com.carplots.persistence.dao.hibernate.ScrollableResultsIterator;
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.entities.Imported;

public class ImportedDaoHibernateImpl
	extends AbstractHibernateDao<Imported, Long> 
	implements ImportedDao {	

	@Override
	public Iterator<Imported> iterateByMakeModelId(long makeModelId) {
		
		//ORM mappings don't represent de-normalized table structure, hand code SQL
		final String modelIdQuery = 
				"SELECT i.* FROM Imported i " +
						"INNER JOIN Search search ON i.CarSearchID = search.SearchID " +
				"WHERE search.MakeModelID = :makeModelId";
		
		final Query query = getSession().createSQLQuery(modelIdQuery)
				.addEntity(Imported.class)
				.setLong("makeModelId", makeModelId);
		final Iterator<Imported> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}

	@Override
	public Iterator<Imported> iterateByMakeModelId(long makeModelId, String zipcode) {
		
		//ORM mappings don't represent de-normalized table structure, hand code SQL
		final String modelIdZipcodeQuery = 
				"SELECT i.* FROM Imported i " +
						"INNER JOIN Search search ON i.CarSearchID = search.SearchID " +
				"WHERE search.MakeModelID = :makeModelId AND " +
						" search.Zipcode = :zipcode";
		
		final Query query = getSession().createSQLQuery(modelIdZipcodeQuery)
				.addEntity(Imported.class)
				.setLong("makeModelId", makeModelId)
				.setString("zipcode", zipcode);
		final Iterator<Imported> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}

	@Override
	public Iterator<Imported> iterateByMakeModelId(long makeModelId,
			long scraperRunId) {
		
		//ORM mappings don't represent de-normalized table structure, hand code SQL
		final String modelIdScraperRunQuery = 
				"SELECT i.* FROM Imported i " +
						"INNER JOIN Search search ON i.CarSearchID = search.SearchID " +
				"WHERE search.MakeModelID = :makeModelId AND " + 
						"i.ScraperRunId = :scraperRunId";
		
		final Query query = getSession().createSQLQuery(modelIdScraperRunQuery)
				.addEntity(Imported.class)
				.setLong("makeModelId", makeModelId)
				.setLong("scraperRunId", scraperRunId);
		final Iterator<Imported> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}

	@Override
	public Iterator<Imported> iterateByMakeModelId(long makeModelId,
			String zipcode, long scraperRunId) {
		
		//ORM mappings don't represent de-normalized table structure, hand code SQL
		final String modelIdScraperRunQuery = 
				"SELECT i.* FROM Imported i " +
						"INNER JOIN Search search ON i.CarSearchID = search.SearchID " +
				"WHERE search.MakeModelID = :makeModelId AND  " +
					"search.Zipcode = :zipcode AND " +
					"i.ScraperRunId = :scraperRunId";
		
		final Query query = getSession().createSQLQuery(modelIdScraperRunQuery)
				.addEntity(Imported.class)
				.setLong("makeModelId", makeModelId)
				.setString("zipcode", zipcode)
				.setLong("scraperRunId", scraperRunId);
		final Iterator<Imported> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}	
	
}
