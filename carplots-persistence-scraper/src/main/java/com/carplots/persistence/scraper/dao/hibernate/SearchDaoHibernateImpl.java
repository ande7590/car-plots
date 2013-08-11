package com.carplots.persistence.scraper.dao.hibernate;

import java.util.Iterator;

import org.hibernate.Query;
import org.hibernate.Session;

import com.carplots.persistence.dao.hibernate.AbstractHibernateDao;
import com.carplots.persistence.dao.hibernate.ScrollableResultsIterator;
import com.carplots.persistence.scraper.dao.SearchDao;
import com.carplots.persistence.scraper.entities.Search;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

public class SearchDaoHibernateImpl 
	extends AbstractHibernateDao<Search, Long> 
	implements SearchDao {

	@Override
	public Iterator<Search> iterateByScraperBatchId(long scraperBatchId) {
		
		final String iterateSearchByScraperBatchIdQuery = 
				"select scraperBatchSearch.search from ScraperBatchSearch scraperBatchSearch " + 
						"where scraperBatchSearch.scraperBatch.scraperBatchId = :scraperBatchId";
		
		final Object delegate = this.getEntityManager().getDelegate();
		if (!(delegate instanceof Session)) {
			throw new IllegalStateException("SearchDaoHibernateImpl expects that delegate is hibernate session.");
		}
		
		final Session session = (Session)delegate;
		final Query query = session.createQuery(iterateSearchByScraperBatchIdQuery)
				.setLong("scraperBatchId", scraperBatchId);
		
		return new ScrollableResultsIterator<Search>(query.scroll());
	}

}
