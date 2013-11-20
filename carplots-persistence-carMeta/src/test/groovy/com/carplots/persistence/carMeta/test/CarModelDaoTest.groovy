package com.carplots.persistence.carMeta.test

import com.carplots.persistence.carMeta.dao.CarModelDao
import com.carplots.persistence.carMeta.dao.hibernate.CarModelDaoHibernateImpl;
import com.carplots.persistence.carMeta.entities.CarModel;
import com.carplots.persistence.carMeta.entities.CarTrim;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceInitializationService;
import com.carplots.persistence.carMeta.module.CarMetaPersistenceModule;
import com.google.inject.Guice;

import com.google.inject.Injector

import javax.persistence.EntityManager
import spock.lang.Shared;
import spock.lang.Specification;

class CarModelDaoTest extends Specification {

	@Shared
	CarModelDao carModelDao
	
	@Shared
	EntityManager entityManager
	
	def setupSpec() {
		Injector injector = Guice.createInjector(new CarMetaPersistenceModule())
		CarMetaPersistenceInitializationService persistInitSvc =
			injector.getInstance(CarMetaPersistenceInitializationService.class)
		persistInitSvc.start()
		carModelDao = injector.getInstance(CarModelDao.class)
		entityManager = injector.getInstance(EntityManager.class)
	}
	
	def setup() {
		entityManager.getTransaction().begin()
	}
	
	def cleanup() {
		//entityManager.getTransaction().rollback()
		entityManager.getTransaction().commit()
	}
	
	def "test guice setup"() { 
		expect:
		carModelDao != null && carModelDao instanceof CarModelDaoHibernateImpl
		entityManager != null
	}
	
	def "test insert"() {
		
		when:
		CarModel modelToInsert = new CarModel()
		CarTrim trim1 = new CarTrim()
		trim1.trimName = "XLS"
		trim1.transmission = "6 speed auto"
		trim1.driveTrain = "AWD"		
		modelToInsert.setMakeName("carmaker")
		modelToInsert.setModelName("carmodel")
		modelToInsert.setYear(2003)
		modelToInsert.setTrims([trim1])
		
		carModelDao.persist(modelToInsert)
		
		then:
		modelToInsert.carModelId > 0
		
	}
	
	def "test update"() {
		
	}
}
