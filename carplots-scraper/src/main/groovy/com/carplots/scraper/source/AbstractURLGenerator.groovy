package com.carplots.scraper.source

abstract class AbstractURLGenerator {
	abstract Object getGeneratorData();
	abstract URL getURL();
	abstract URL getURL(int pageNumber);
}
