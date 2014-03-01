package com.carplots.persistence.dao.hibernate;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;

import com.carplots.persistence.dao.jpa.AbstractJPADao;

public class AbstractHibernateDao<ENTITY_TYPE extends Serializable, ENTITY_PK_TYPE> 
	extends AbstractJPADao<ENTITY_TYPE, ENTITY_PK_TYPE>
{	
	@Override
	public List<ENTITY_TYPE> findByExample(ENTITY_TYPE entity) {
		
		final Example entityExample = Example.create(entity).excludeZeroes();
		final Criteria criteria = getSession().createCriteria(getEntityType()).add(entityExample);
		
		return criteria.list();
	}

	@Override
	public Iterator<ENTITY_TYPE> iterateAll() {
		
		final Criteria criteria = getSession().createCriteria(getEntityType());
		
		return new ScrollableResultsIterator<>(criteria.scroll());		
	}

	@Override
	public Iterator<ENTITY_TYPE> iterateByExample(ENTITY_TYPE example) {
		
		final Example entityExample = Example.create(example).excludeZeroes();
		final Criteria criteria = getSession().createCriteria(getEntityType()).add(entityExample);
		
		return new ScrollableResultsIterator<>(criteria.scroll());
	}
	
	@SuppressWarnings("unchecked")
	protected Session getSession() {
		
		final EntityManager entityManager = getEntityManager();
		final Object delegate = entityManager.getDelegate();

		if (!(delegate instanceof Session)) {
			throw new IllegalStateException("AbstractHibernateDao expects JPA implementation to be Hibernate, " + 
					"invalid delegate type (was not Hibernate Session).");
		}
		
		final Session session = (Session)delegate;
		
		return session;
	}

	@Override
	public void clear() {		
		getEntityManager().clear();
	}

	
}
