package com.carplots.scraper.dataimport.carsDotCom

import java.util.Collection;

import com.carplots.persistence.scraper.entities.Imported;
import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;

interface CarsDotComScraper {
	Collection<Imported> getImported(CarsDotComCrawlerData crawlerData);
}
