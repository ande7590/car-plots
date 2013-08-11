package com.carplots.scraper.source.cars

import java.net.URL;

import com.carplots.persistence.scraper.entities.Search;
import com.carplots.scraper.source.*

class URLGeneratorCarsImpl extends AbstractURLGenerator {

	final Search generatorData
	final int numOnPage
	
	URLGeneratorCarsImpl(final Search generatorData, final int numOnPage) {
		this.generatorData = generatorData
		this.numOnPage = numOnPage
	}
	
	@Override
	public URL getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URL getURL(int pageNumber) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getGeneratorData() {
		return this.generatorData
	}

}
