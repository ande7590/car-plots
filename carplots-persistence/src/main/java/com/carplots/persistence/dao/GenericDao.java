package com.carplots.persistence.dao;

import java.util.Iterator;
import java.util.List;

public interface GenericDao<ENTITY_TYPE, ENTITY_PK_TYPE> {

	ENTITY_TYPE findByPk(ENTITY_PK_TYPE pk);
	Iterator<ENTITY_TYPE> iterateAll();
	Iterator<ENTITY_TYPE> iterateByExample(ENTITY_TYPE example);
	List<ENTITY_TYPE> findByExample(ENTITY_TYPE example);
	
	void persist(ENTITY_TYPE entity);
	ENTITY_TYPE merge(ENTITY_TYPE entity);
	void remove(ENTITY_TYPE entity);
	
}
