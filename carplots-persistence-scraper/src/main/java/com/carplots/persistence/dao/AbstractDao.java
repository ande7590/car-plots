package com.carplots.persistence.dao;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.jodah.typetools.TypeResolver;

import com.google.inject.Inject;

public abstract class AbstractDao<ENTITY_TYPE extends Serializable, ENTITY_PK_TYPE> 
	implements GenericDao<ENTITY_TYPE, ENTITY_PK_TYPE> {
		
	@Inject
	private EntityManager em;
	
	private Class<ENTITY_TYPE> entityType;
	private Class<ENTITY_PK_TYPE> entityPkType;
	
	@SuppressWarnings("unchecked")
	public AbstractDao() {
		Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractDao.class, getClass());
		entityType = (Class<ENTITY_TYPE>)typeArguments[0];
		entityPkType = (Class<ENTITY_PK_TYPE>)typeArguments[1];
	}		
	
	protected EntityManager getEntityManager() {
		return this.em;
	}
	
	protected Class<ENTITY_TYPE> getEntityType() {
		return entityType;
	}
	
	protected Class<ENTITY_PK_TYPE> getEntityPkType() {
		return entityPkType;
	}
		
	@Override
	public ENTITY_TYPE findByPk(ENTITY_PK_TYPE pk) {		
		return getEntityManager().find(getEntityType(), pk);	
	}

	@Override
	public void persist(ENTITY_TYPE entity) {
		getEntityManager().persist(entity);		
	}

	@Override
	public ENTITY_TYPE merge(ENTITY_TYPE entity) {		
		ENTITY_TYPE mergeEntity = getEntityManager().merge(entity);
		return mergeEntity;
	}

	@Override
	public void remove(ENTITY_TYPE entity) {		
		getEntityManager().remove(entity);
	}
	
	/**
	 * JPA doesn't support the below methods natively, 
	 * fallback onto ORM-specific methods.
	 */
	@Override
	public abstract List<ENTITY_TYPE> findByExample(ENTITY_TYPE example);

	@Override
	public abstract Iterator<ENTITY_TYPE> iterateAll();
	
	@Override
	public abstract Iterator<ENTITY_TYPE> iterateByExample(ENTITY_PK_TYPE example);
	
}
