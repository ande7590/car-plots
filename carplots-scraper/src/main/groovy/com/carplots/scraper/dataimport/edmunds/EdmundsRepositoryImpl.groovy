package com.carplots.scraper.dataimport.edmunds

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method.*

import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxBinary
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.carplots.scraper.ScraperConfigService
import com.carplots.scraper.dataimport.edmunds.EdmundsRepository.EdmundsRepositoryFetchException
import com.google.inject.Inject

/*
 * The majority of this is sort of throw-away since this
 * import should only happen once every couple years or so. 
 */
class EdmundsRepositoryImpl
	implements EdmundsRepository {
	
	private static final Logger logger = LoggerFactory.getLogger(EdmundsRepositoryImpl.class)
	
	public static String[] edmundsMakes = [
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
	
	public static String[] edmundsMakesJSON = [
		"Acura", "Alfa Romeo", "AM General", "Aston Martin", "Audi", "Bentley",
		"BMW", "Bugatti", "Buick", "Cadillac", "Chevrolet", "Chrysler",
		"Daewoo", "Dodge", "Eagle", "Ferrari", "FIAT", "Fisker",
		"Ford", "Geo", "GMC", "Honda", "HUMMER", "Hyundai",
		"Infiniti", "Isuzu", "Jaguar", "Jeep", "Kia", "Lamborghini",
		"Land Rover", "Lexus", "Lincoln", "Lotus", "Maserati", "Maybach",
		"Mazda", "McLaren", "Mercedes-Benz", "Mercury", "MINI", "Mitsubishi",
		"Nissan", "Oldsmobile", "Panoz", "Plymouth", "Pontiac", "Porsche",
		"Ram", "Rolls-Royce", "Saab", "Saturn", "Scion", "smart",
		"Spyker", "Subaru", "Suzuki", "Tesla", "Toyota", "Volkswagen",
		"Volvo"]

	
	
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
			
	@Override
	public Object getMakeJSON(String makeName) {
		
		if (!edmundsMakesJSON.find { it.equals(makeName)} ) {
			throw new IllegalArgumentException("bad makeName argument: ${makeName}")
		}
		
		HTTPBuilder http = new HTTPBuilder(repoConfig.getMakeJSONSearchURL() + java.net.URLEncoder.encode(makeName))
		http.handler.failure = { resp ->
			doHandleFailure(resp)
		}
		
		def jsonResult = null
		def retries = 0		
		while (jsonResult == null && retries < repoConfig.getNumRetries()) {
			jsonResult = http.get(contentType: JSON)
			if (jsonResult == null) {
				logger.warn('Edmunds JSON request returned nothing, sleeping...')
				try {
					Thread.sleep(repoConfig.getFailureSleepMS())
				} catch (Exception ex) {} 							
				retries++
			}						
		}
		
		if (jsonResult == null) {
			logger.error('Unable to retrieve HTTP data, crawler aborting.')
			throw new EdmundsRepositoryFetchException()
		} 
				
		return jsonResult
	}
	
	protected void doHandleFailure(def resp) {
		logger.error("Remote HTTP request failure ${resp.toString()}")
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
		
	public static class EdmundsRepositoryConfiguration {
		@Inject
		ScraperConfigService configService

		String getScraperBaseURL() {
			return configService.getApplicationParameter('edmundsBaseURL') as String
		}
		String getMakeJSONSearchURL() {
			return configService.getApplicationParameter('edmundsFindMakeSearchJSONURL') as String
		}
		
		int getNumRetries() {
			return configService.getApplicationParameter('numRetries') as int
		}
		
		String getFirefoxBinaryPath() {
			return configService.getApplicationParameter('firefoxBinaryPath') as String
		}
		String getFirefoxProfilePath() {
			return configService.getApplicationParameter('firefoxProfilePath') as String
		}
		
	}

}
