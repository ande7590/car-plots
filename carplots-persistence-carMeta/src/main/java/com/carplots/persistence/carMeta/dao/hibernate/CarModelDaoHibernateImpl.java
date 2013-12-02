package com.carplots.persistence.carMeta.dao.hibernate;

import java.util.Iterator;
import org.hibernate.Query;

import com.carplots.persistence.carMeta.dao.CarModelDao;
import com.carplots.persistence.carMeta.entities.CarModel;
import com.carplots.persistence.dao.hibernate.AbstractHibernateDao;
import com.carplots.persistence.dao.hibernate.ScrollableResultsIterator;

public class CarModelDaoHibernateImpl extends
		AbstractHibernateDao<CarModel, Long> implements CarModelDao {

	@Override
	public Iterator<CarModel> iterateCarModels(final String hqlQuery) {
		
		final Query query = getSession().createQuery(hqlQuery);		
		final Iterator<CarModel> iter = 
				new ScrollableResultsIterator<>(query.scroll());
		
		return iter;
	}
	

}
