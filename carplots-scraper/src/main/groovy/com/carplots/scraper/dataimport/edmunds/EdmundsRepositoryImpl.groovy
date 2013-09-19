package com.carplots.scraper.dataimport.edmunds

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

import com.carplots.scraper.ScraperConfigService;
import com.carplots.scraper.dataimport.AbstractHttpBuilderScraperRepository;
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sun.corba.se.impl.activation.ListServers;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import static groovyx.net.http.ContentType.*;
import static groovyx.net.http.Method.*
import groovyx.net.http.HTTPBuilder;
import groovyx.net.http.Method.*;

class EdmundsRepositoryImpl
	implements EdmundsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EdmundsRepositoryImpl.class)
	
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
	
	
	@Inject
	EdmundsRepositoryConfiguration repoConfig
	
	public String[] getMakes() {
		return edmundsMakes.clone();
	}
	
	@Override
	public String getMakeData(String makeName)
			throws EdmundsRepositoryFetchException {
		
		if (edmundsMakes.find { it == makeName } == null) {
			throw new IllegalArgumentException('Make {$makeName} not found.')
		}
		
		def html = getPageHtml("/${makeName}")
		
		if (html == null || html.trim().isEmpty()) {
			final def message = "Unable to retrieve HTTP data for ${makeName}";
			throw new EdmundsRepositoryFetchException(message)
		}
		
		return html
	}

	@Override
	public String getMakeModelData(String makeName, String modelName)
			throws EdmundsRepositoryFetchException {
		
		if (edmundsMakes.find { it == makeName } == null) {
			throw new IllegalArgumentException('Make {$makeName} not found.')
		}
		
		String html = getPageHtml("/${makeName}/${modelName}")
		
		if (html == null || html.trim().isEmpty()) {
			final def message = "Unable to retrieve HTTP data for ${makeName}, ${modelName}";
			throw new EdmundsRepositoryFetchException(message)
		}
		
		return html
	}
			
	private def getMakeJSON(def makeName) {
		
		def http = new HTTPBuilder(repoConfig.getMakeJSONSearchURL())
		def query = ''
		
	}
		
	private def getPageHtml(def path) {
		def webDriver = getWebDriver()
		webDriver.get(repoConfig.getScraperBaseURL() + path)
		(new WebDriverWait(webDriver, 10)).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver d) {
				return webDriver.getPageSource().contains('</body>')
			};
		})
		return webDriver.getPageSource()
	}
	
	
	WebDriver driver = null;
	private WebDriver getWebDriver() {
		if (driver == null) {
			def binary = new FirefoxBinary(new File(repoConfig.getFirefoxBinaryPath()))
			def profile = new FirefoxProfile(new File(repoConfig.getFirefoxProfilePath()))
			driver = new FirefoxDriver(binary, profile)
		}
		return driver
	}
		
	static class EdmundsRepositoryConfiguration {
		@Inject
		ScraperConfigService configService

		String getScraperBaseURL() {
			return configService.getApplicationParameter('edmundsBaseURL') as String
		}
		String getMakeJSONSearchURL() {
			return configService.getApplicationParameter('edmundsMakeSearchJSONURL') as String
		}
		
		String getFirefoxBinaryPath() {
			return configService.getApplicationParameter('firefoxBinaryPath') as String
		}
		String getFirefoxProfilePath() {
			return configService.getApplicationParameter('firefoxProfilePath') as String
		}
		
	}

}
