package com.carplots.persistence.carMeta.test

import com.carplots.persistence.carMeta.dao.CarModelDao
import com.carplots.persistence.carMeta.dao.hibernate.CarModelDaoHibernateImpl;
import com.carplots.persistence.carMeta.entities.CarEngine;
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
				
		CarEngine engine1 = new CarEngine(cylinders: 6, description: "engine1", displacementCC: 3000);
		CarEngine engine2 = new CarEngine(cylinders: 4, description: "engine2", displacementCC: 1000);
		CarTrim trim1 = new CarTrim(trimName: "XLS", transmission: "5 speed", driveTrain: "AWD", engines: [engine1])
		CarTrim trim2 = new CarTrim(trimName: "XLT", transmission: "6 speed", driveTrain: "FWD", engines: [engine1, engine2])
		CarTrim trim3 = new CarTrim(trimName: "LS", transmission: "4 speed", driveTrain: "RWD", engines: [engine1, engine2])
		def trims = [trim1, trim2, trim3]
		CarModel modelToInsert = new CarModel(makeName: "Test Make", modelName: "Test Model", year: 2003, trims: trims)
		carModelDao.persist(modelToInsert)
		
		then:
		modelToInsert.carModelId > 0
		
	}
	
	def "test update"() {
		
	}
}
