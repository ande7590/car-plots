package com.carplots.persistence.testDriver;

import java.net.URL;
import java.net.URLClassLoader;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.carplots.persistence.ScraperPersistenceInitializationService;
import com.carplots.persistence.ScraperPersistenceModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.Transactional;

public class TestDriver  {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//printClasspath();
		Injector injector = Guice.createInjector(new ScraperPersistenceModule());
		final ScraperPersistenceInitializationService pis = 
				injector.getInstance(ScraperPersistenceInitializationService.class);
		try {
			pis.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//app.run();
		
//		final TestDriverModule mod = new TestDriverModule();
//		mod.run();
		
	}
	
}
