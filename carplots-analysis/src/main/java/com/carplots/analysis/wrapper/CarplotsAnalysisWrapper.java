package com.carplots.analysis.wrapper;

import java.util.Collection;
import java.util.LinkedList;

import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.carplots.persistence.scraper.entities.Location;
import com.carplots.persistence.scraper.entities.MakeModel;
import com.carplots.persistence.scraper.module.ScraperPersistenceInitializationService;
import com.carplots.service.analysis.CarplotsAnalysisService;
import com.carplots.service.analysis.CarplotsAnalysisServiceImpl;
import com.google.inject.Guice;
import com.google.inject.Injector;


/*
 * This class is instantiated by the R interpreter (via rJava), and used
 * as a conduit from R into Carplots Java API. 
 */
public class CarplotsAnalysisWrapper {
	
	//static Logger logger = LoggerFactory.getLogger(CarplotsAnalysisWrapper.class);
	
	private Injector injector;
	private final Object initLock = new Object();
	private volatile boolean didInit = false;
	private CarplotsAnalysisServiceImpl carplotsAnalysisService = null;
			
	//return object rather than interface because it is easier to work with in R
	public CarplotsAnalysisServiceImpl getCarplotsAnalysisService() throws Exception   {
		if (!didInit) {			
			init();			
		}
		return carplotsAnalysisService;
	}
	
	//thread safe initializer
	private void init() throws Exception {
		if (!didInit) {
			synchronized (initLock) {
				if (!didInit) {
									
					System.out.println("init started, creating injector");
					//load dependency injection framework
					Injector injector = Guice.createInjector(
							new CarplotsAnalysisWrapperModule());					
					
					System.out.println("getting analysis service from injector");
					carplotsAnalysisService = injector.getInstance(
							CarplotsAnalysisServiceImpl.class);

					System.out.println("init done");					
					didInit = true;
				}
			}
		}		
	}	
	
	
}