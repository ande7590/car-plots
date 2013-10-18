package com.carplots.scraper.dataimport.edmunds

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovy.json.JsonSlurper;
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method.*

import org.openqa.selenium.JavascriptExecutor;
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
	
	@Override
	public Object getMakeModelYearJSON(String makeName, String modelName,
			String year) {
		
		WebDriver webDriver = getWebDriver()
		JsonSlurper jsSlurper = new JsonSlurper()
		
		def stylesToProcess = [null]					
		def stylesCollectedDataMap = [:]
		
		try {
			while (stylesToProcess.size() > 0) {
				
				def style = stylesToProcess.pop()
				def queryURL = [
					repoConfig.getScraperBaseURL(),
					makeName.toLowerCase(),
					modelName.toLowerCase(),
					year].join('/')
				
				if (style != null) {
					queryURL += "?style=${style}"
				}
				webDriver.get(queryURL)
				
				(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver d) {
						return d.getPageSource().contains('</body>')
					};
				})
				
				JavascriptExecutor js = (JavascriptExecutor) webDriver
				
				for (int second = 0;; second++) {
					if(second >= 30){
						break;
					}
						js.executeScript("window.scrollBy(0,400)", "");
						Thread.sleep(100);
				}
				
				if (style == null) {									
					def stylesListJSON = js.executeScript('''						
							var specsSelectOptions = document.getElementById("specs-diff").children[0].options
							var results = new Object()
							for (var i=0;i<specsSelectOptions.length;i++) {
							    var opt = specsSelectOptions[i]
							    var text = opt.text
							    var value = opt.value
							    results[value] = text
							}
							return JSON.stringify(results)																																
					''');
					if (stylesListJSON == null) {
						throw new EdmundsRepositoryFetchException()
					}
					
					def stylesListData = jsSlurper.parseText(stylesListJSON)					
					for (String key : stylesListData.keySet()) {
						stylesToProcess << key
						stylesCollectedDataMap[key] = [
							desc: stylesListData[key]
						]
					}
					
					style = js.executeScript('''return document.getElementById("specs-diff").getElementsByTagName("select")[0].value''')
				} 
				
				
				def styleDataJSON = js.executeScript('''
					var dataTableCandidates = document.getElementById("specs-pod").getElementsByTagName("div")
					var dataTables = []
					for (var i=0; i<dataTableCandidates.length; i++) {
						var elem = dataTableCandidates[i]
						if (elem.className.indexOf("data-table") >= 0) {
							dataTables.push(elem)
						}
					}
					var data = {}
					for (var i=0; i<dataTables.length; i++) {
						var dt = dataTables[0];
						var items = dt.getElementsByTagName('li')
						for (var j=0; j<items.length; j++) {
							var itm = items[j]
							var attrName = itm.getElementsByTagName('span')[0].innerHTML
							var attrVal = itm.getElementsByTagName('em')[0].innerHTML
							data[attrName] = attrVal
						}
					}
					return JSON.stringify(data)
				''')
				
				
				def styleAllData = jsSlurper.parseText(styleDataJSON)
				stylesCollectedDataMap[style] = stylesCollectedDataMap[style] + styleAllData  
			}
			
		} catch (Exception ex) {
			throw new EdmundsRepositoryFetchException();
		}
		
		return stylesCollectedDataMap
	}
	
	protected void doHandleFailure(def resp) {
		logger.error("Remote HTTP request failure ${resp.toString()}")
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
		
		String getFireFoxBinaryLocation() {
			return configService.getApplicationParameter('seleniumFireFoxBinaryLocation')
		}
		String getFireFoxProfileLocation() {
			return configService.getApplicationParameter('seleniumFireFoxProfileLocation')
		}
	}
	
}
