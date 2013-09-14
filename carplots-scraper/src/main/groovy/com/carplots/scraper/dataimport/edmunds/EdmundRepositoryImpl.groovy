package com.carplots.scraper.dataimport.edmunds

import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.scraper.ScraperConfigService;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.corba.se.impl.activation.ListServers;

import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.Method.*;

class EdmundRepositoryImpl {
	
	private static final Logger logger = LoggerFactory.getLogger(EdmundRepositoryImpl.class)
	
	private static String[] edmundsMakes = [
		"acura", "alfa-romeo", "am-general", "aston-martin", "audi",
		"bentley", "bmw", "bugatti", "buick", "cadillac", "chevrolet",
		"chrysler", "daewoo", "dodge", "eagle", "ferrari", "fiat",
		"fisker", "ford", "geo", "gmc", "honda", "hummer", "hyundai",
		"infiniti", "isuzu", "jaguar", "jeep", "kia", "lamborghini",
		"land-rover", "lexus", "lincoln", "lotus", "maserati", "maybach",
		"mazda", "mclaren", "mercedes-benz", "mercury", "mini",
		"mitsubishi", "nissan", "oldsmobile", "panoz", "plymouth",
		"pontiac", "porsche", "ram", "rolls-royce", "saab", "saturn",
		"scion", "smart", "spyker", "subaru", "suzuki", "tesla",
		"toyota", "volkswagen", "volvo"
	]
	
	static String baseURL = 'http://www.edmunds.com/api/vehicle-directory-ajax/findmakemodels' +
			'?fmt=json&ps=all&yearFormat=expanded&excludepreprod&make='
	
	@Inject
	EdmundsRepositoryConfiguration repoConfig
	
	public String[] getMakes() {
		return edmundsMakes.clone();
	}
	
	public def getMakeMetaDate(def makeName) {
		
	}
	
	static class EdmundsRepositoryConfiguration {
		@Inject
		ScraperConfigService configService
	}
}
