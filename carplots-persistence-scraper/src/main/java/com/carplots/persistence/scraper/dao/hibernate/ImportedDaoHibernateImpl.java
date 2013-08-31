package com.carplots.persistence.scraper.dao.hibernate;

import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;

import com.carplots.persistence.dao.hibernate.AbstractHibernateDao;
import com.carplots.persistence.dao.hibernate.ScrollableResultsIterator;
import com.carplots.persistence.scraper.dao.ImportedDao;
import com.carplots.persistence.scraper.entities.Imported;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class ImportedDaoHibernateImpl
	extends AbstractHibernateDao<Imported, Long> 
	implements ImportedDao {
	
	@Override
	public Iterator<Imported> iterateByMakeId(long makeId) {		

		//ORM mappings don't represent de-normalized table structure, hand code SQL
		final String makeIdQuery = 
				"SELECT i.* FROM Imported i " +
						"INNER JOIN Search search ON i.CarSearchID = search.SearchID " +
				"WHERE search.MakeID = :makeId";
		
		final Query query = getSession().createSQLQuery(makeIdQuery)
				.addEntity(Imported.class)
				.setLong("makeId", makeId);
		final Iterator<Imported> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}

	@Override
	public Iterator<Imported> iterateByModelId(long modelId) {
		
		//ORM mappings don't represent de-normalized table structure, hand code SQL
		final String modelIdQuery = 
				"SELECT i.* FROM Imported i " +
						"INNER JOIN Search search ON i.CarSearchID = search.SearchID " +
				"WHERE search.ModelID = :modelId";
		
		final Query query = getSession().createSQLQuery(modelIdQuery)
				.addEntity(Imported.class)
				.setLong("modelId", modelId);
		final Iterator<Imported> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}	
	
}
