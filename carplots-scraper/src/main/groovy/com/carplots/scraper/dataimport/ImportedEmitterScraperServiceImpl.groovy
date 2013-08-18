package com.carplots.scraper.dataimport

import javax.persistence.EntityManager;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.service.scraper.CarplotsScraperService;
import com.google.inject.Inject;

class ImportedEmitterScraperServiceImpl implements ImportedEmitter {

	@Inject
	CarplotsScraperService scraperService
	
	@Override
	public void emit(Imported imported) {
		scraperService.addImported(imported)
	}		
}
