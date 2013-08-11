package com.carplots.scraper.dataimport.carsDotCom

import java.util.Iterator;

import com.carplots.scraper.dataimport.carsDotCom.CarsDotComCrawlerIterator.CarsDotComCrawlerData;

interface CarsDotComCrawler 
	extends Iterable<CarsDotComCrawlerData> {
}
